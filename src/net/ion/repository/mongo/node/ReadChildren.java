package net.ion.repository.mongo.node;

import java.util.Iterator;
import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.db.Rows;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.ReadSession;
import net.ion.repository.mongo.convert.rows.AdNodeRows;
import net.ion.repository.mongo.expression.ExpressionParser;
import net.ion.repository.mongo.expression.SelectProjection;
import net.ion.repository.mongo.expression.TerminalParser;
import net.ion.repository.mongo.util.ReadChildrenEachs;
import net.ion.rosetta.Parser;

import com.google.common.collect.Iterators;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class ReadChildren extends AbstractChildren<ReadNode, ReadChildren> {

	private int skip = 0;
	private int offset = 1000;
	private DBObject orderBy = new BasicDBObject();

	private final ReadSession session;
	private final DBCollection collection;

	private String hintIndexName;
	private DBObject hint;
	private final Fqn parent;

	public ReadChildren(ReadSession session, boolean includeSub, DBCollection collection, Fqn parent) {
		this.session = session;
		this.collection = collection;
		this.parent = parent;

		if (includeSub) {
			put("_parent", new BasicDBObject("$gt", (parent.isRoot()) ? "/" : (parent.toString() + "/")));
		} else {
			put("_parent", parent.toString());
		}
	}

	public ReadChildren skip(int skip) {
		this.skip = skip;
		return this;
	}

	public ReadChildren offset(int offset) {
		this.offset = offset;
		return this;
	}

	public ReadChildren ascending(String propId) {
		orderBy.put(propId, 1);
		return this;
	}

	public ReadChildren descending(String propId) {
		orderBy.put(propId, -1);
		return this;
	}

	public Rows toRows(String expr) {
		Parser<SelectProjection> parser = ExpressionParser.selectProjection();
		SelectProjection sp = TerminalParser.parse(parser, expr);
		return AdNodeRows.create(session, iterator(), sp);
	}

	public Rows toRows(Page _page, String expr) {
		Parser<SelectProjection> parser = ExpressionParser.selectProjection();
		SelectProjection sp = TerminalParser.parse(parser, expr);
		Page page = (_page == Page.ALL) ? Page.create(10000, 1) : _page; // limit

		IteratorList<ReadNode> iterator = this.iterator();
		Iterators.skip(iterator, page.getSkipOnScreen());
		Iterator<ReadNode> limitIter = Iterators.limit(iterator, page.getOffsetOnScreen());

		List<ReadNode> screenList = ListUtil.newList();
		while (limitIter.hasNext()) {
			screenList.add(limitIter.next());
		}

		int count = screenList.size();
		Page pageOnScreen = Page.create(page.getListNum(), page.getPageNo() % page.getScreenCount(), page.getScreenCount());
		return AdNodeRows.create(session, pageOnScreen.subList(screenList).iterator(), sp, count, "cnt");
	}

	public ReadNode firstNode() {
		return eachNode(ReadChildrenEachs.FIRSTNODE);
	}

	public ReadChildren hint(String indexName) {
		this.hintIndexName = indexName;
		return this;
	}

	public ReadChildren hint(DBObject hint) {
		this.hint = hint;
		return this;
	}

	public <T> T eachNode(ReadChildrenEach<T> readJob) {
		DBCursor cursor = null;
		try {
			cursor = collection.find(filters(), fields(), skip, offset).sort(orderBy).limit(offset);
			if (this.hint != null)
				cursor.hint(hint);
			else if (this.hintIndexName != null)
				cursor.hint(hintIndexName);

			session.attribute(Explain.class.getCanonicalName(), Explain.create(cursor.explain()));
			ReadChildrenIterator citer = ReadChildrenIterator.create(session, cursor);
			T result = readJob.handle(citer);
			return result;
		} finally {
			IOUtil.close(cursor);
		}
	}

	public List<ReadNode> toList() {
		return eachNode(ReadChildrenEachs.LIST);
	}
	
	public IteratorList<ReadNode> iterator(){
		return eachNode(ReadChildrenEachs.ITERATOR) ;
	}

	public void debugPrint() {
		eachNode(ReadChildrenEachs.DEBUG);
	}

	public int count() {
		return eachNode(ReadChildrenEachs.COUNT);
	}

	public MapReduce mapreduce() {
		return MapReduce.create(this);
	}

	public ReadSession session() {
		return session;
	}

}
