package net.ion.repository.mongo.node;

import java.util.List;

import net.ion.framework.util.IOUtil;
import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.ReadJob;
import net.ion.repository.mongo.ReadSession;
import net.ion.repository.mongo.util.ReadJobs;

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

	public ReadChildren(ReadSession session, DBCollection collection, Fqn parent) {
		super(parent);
		this.session = session;
		this.collection = collection;
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

	// public Rows toAdRows(String expr) {
	// Parser<SelectProjection> parser = ExpressionParser.selectProjection();
	// SelectProjection sp = TerminalParser.parse(parser, expr);
	// return AdNodeRows.create(session, iterator(), sp);
	// }
	//
	//
	// public Rows toAdRows(Page _page, String expr) {
	// Parser<SelectProjection> parser = ExpressionParser.selectProjection();
	// SelectProjection sp = TerminalParser.parse(parser, expr);
	// Page page = (_page == Page.ALL) ? Page.create(10000, 1) : _page; // limit
	//
	// checkReload() ;
	// Iterators.skip(this.iter, page.getSkipOnScreen()) ;
	// Iterator<ReadNode> limitIter = Iterators.limit(this.iter, page.getOffsetOnScreen());
	//
	// List<ReadNode> screenList = ListUtil.newList() ;
	// while(limitIter.hasNext()){
	// screenList.add(limitIter.next()) ;
	// }
	//
	// int count = screenList.size();
	// Page pageOnScreen = Page.create(page.getListNum(), page.getPageNo() % page.getScreenCount(), page.getScreenCount()) ;
	// return AdNodeRows.create(session, pageOnScreen.subList(screenList).iterator(), sp, count, "cnt");
	// }

	// public ReadNode firstNode() {
	// return hasNext() ? next() : null;
	// }

	public <T> T find(ReadJob<T> readJob) {
		DBCursor cursor = null;
		try {
			cursor = collection.find(filters(), fields(), skip, offset).sort(orderBy).limit(offset);
			ChildrenIterator citer = ChildrenIterator.create(session, cursor);
			T result = readJob.handle(citer);
			return result;
		} finally {
			IOUtil.close(cursor);
		}
	}

	public List<ReadNode> toList() {
		return find(ReadJobs.LIST);
	}

	public void debugPrint() {
		find(ReadJobs.DEBUG);
	}

}
