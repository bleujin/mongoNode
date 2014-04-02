package net.ion.repository.mongo.node;

import java.util.List;

import net.ion.framework.util.IOUtil;
import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.ReadSession;
import net.ion.repository.mongo.util.ReadChildrenEachs;

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

	public ReadChildren(ReadSession session, boolean includeSub, DBCollection collection, Fqn parent) {
		super(parent);
		this.session = session;
		this.collection = collection;
		if (includeSub){
			put("_parent", new BasicDBObject("$gt", parent.toString() + "/")) ;
		} else { 
			put("_parent", parent.toString()) ;
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

	
	public ReadChildren hint(String indexName){
		this.hintIndexName = indexName ;
		return this ;
	}
	
	public ReadChildren hint(DBObject hint){
		this.hint = hint ;
		return this ;
	}
	
	public <T> T eachNode(ReadChildrenEach<T> readJob) {
		DBCursor cursor = null;
		try {
			cursor = collection.find(filters(), fields(), skip, offset).sort(orderBy).limit(offset);
			if (this.hint != null) cursor.hint(hint) ;
			else if (this.hintIndexName != null) cursor.hint(hintIndexName) ;
			
			session.attribute(Explain.class.getCanonicalName(), Explain.create(cursor.explain())) ;
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

	public void debugPrint() {
		eachNode(ReadChildrenEachs.DEBUG);
	}

}
