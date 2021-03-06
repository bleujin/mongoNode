package net.ion.repository.mongo.node;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.ion.framework.util.ArrayUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.SetUtil;
import net.ion.framework.util.StringUtil;
import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.PropertyId;
import net.ion.repository.mongo.Workspace;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.index.SessionJob;
import net.ion.repository.mongo.node.WriteNode.Touch;
import net.ion.repository.mongo.util.ReadChildrenEachs;
import net.ion.repository.mongo.util.WriteChildrenEachs;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.sun.xml.internal.ws.addressing.WsaClientTube;

public class WriteChildren extends AbstractChildren<WriteNode, WriteChildren> {

	private int skip = 0;
	private int offset = 1000;
	private DBObject orderBy = new BasicDBObject();

	private final WriteSession session;
	private final DBCollection collection;
	private final Fqn parent;

	public WriteChildren(WriteSession session, boolean includeSub, DBCollection collection, Fqn parent) {
		this.session = session;
		this.collection = collection;
		this.parent = parent ;
		if (includeSub){
			put("_parent", new BasicDBObject("$gt", (parent.isRoot()) ? "/" : (parent.toString() + "/"))) ;
		} else { 
			put("_parent", parent.toString()) ;
		}
	}

	public WriteChildren skip(int skip) {
		this.skip = skip;
		return this;
	}

	public WriteChildren offset(int offset) {
		this.offset = offset;
		return this;
	}

	public WriteChildren ascending(String propId) {
		orderBy.put(propId, 1);
		return this;
	}

	public WriteChildren descending(String propId) {
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

	public <T> T eachNode(WriteChildrenEach<T> readJob) {
		DBCursor cursor = null;
		try {
			cursor = collection.find(filters(), fields(), skip, offset).sort(orderBy).limit(offset);
			session.attribute(Explain.class.getCanonicalName(), Explain.create(cursor.explain())) ;
			WriteChildrenIterator citer = WriteChildrenIterator.create(session, cursor);
			T result = readJob.handle(citer);
			return result;
		} finally {
			IOUtil.close(cursor);
		}
	}

	public WriteNode firstNode() {
		return eachNode(new WriteChildrenEach<WriteNode>(){
			@Override
			public WriteNode handle(WriteChildrenIterator citer) {
				return citer.hasNext() ? citer.next() : null ;
			}
		});
	}

	
	public List<WriteNode> toList() {
		return eachNode(WriteChildrenEachs.LIST);
	}

	public void debugPrint() {
		eachNode(WriteChildrenEachs.DEBUG);
	}

	public int count() {
		return eachNode(WriteChildrenEachs.COUNT) ;
	}

	public void remove() {
		session.addSessionJob(new SessionJob() {
			@Override
			public void run(Workspace workspace) {
				workspace.collection(session.readSession()).remove(filters()) ;
			}
		}) ;
	}

	
	
	
	
	public void findUpdate() {
		session.addSessionJob(new SessionJob() {
			@Override
			public void run(Workspace workspace) {
				set().put("_lastmodified", GregorianCalendar.getInstance().getTimeInMillis());
				BasicDBObject forApply = new BasicDBObject();
				if (set.size() > 0) forApply.put("$set", set) ;
				if (unset.size() > 0) forApply.put("$unset", unset) ;
				if (inc.size() > 0) forApply.put("$inc", inc) ;
				
				workspace.collection(session.readSession()).update(filters(), forApply, false, true) ;
			}
		}) ;
	}
	

	private BasicDBObject set = new BasicDBObject() ;
	private BasicDBObject unset = new BasicDBObject() ;
	private BasicDBObject inc = new BasicDBObject() ;
	
	private BasicDBObject set(){
		return set ;
	}
	public WriteChildren unset(String name){
		unset.put(StringUtil.lowerCase(name), 1) ;
		return this ;
	}

	
	public WriteChildren increase(String name, int i) {
		inc.put(StringUtil.lowerCase(name), i) ;
		return this ;
	}
	
	public WriteChildren property(String name, String value) {
		return property(PropertyId.fromString(name), value);
	}

	public WriteChildren property(String name, long value) {
		return property(PropertyId.fromString(name), value);
	}

	public WriteChildren property(String name, boolean value) {
		return property(PropertyId.fromString(name), Boolean.valueOf(value));
	}

	public WriteChildren property(String name, Date date) {
		return property(name, date.getTime());
	}


//
//
//	public WriteNode unset(String name) {
//		found().removeField(name) ;
//		touch(this.fqn, Touch.MODIFY) ;
//		return this;
//	}
//
//	public WriteNode unset(String name, Object... values) {
//		Object vals = found().removeField(name) ;
//		if (BasicDBList.class.isInstance(vals)){
//			BasicDBList valList = (BasicDBList) vals;
//			valList.removeAll(SetUtil.create(values)) ;
//			found().put(name, valList) ;
//		} else if (ArrayUtil.contains(values, vals)) {
//			found().removeField(name) ;
//		}
//		
//		touch(this.fqn, Touch.MODIFY) ;
//		return this;
//	}
//
//	public WriteNode unref(String refName) {
//		return unset(PropertyId.refer(refName).fullString()) ;
//	}
//
//	public WriteChildren unref(String refName, String... refNames) {
//		return unset(PropertyId.refer(refName).fullString(), refNames) ;
//	}

	
//	public WriteChildren append(String name, Object... values) {
//		return append(PropertyId.fromString(name), values) ;
//	}
//	
//	public WriteChildren append(PropertyId pId, Object... values) {
//		set().put(pId.fullString(), mergedList(pId, values)) ;
//		return this;
//	}
//
//	public WriteChildren refTos(String name, String... refPaths) {
//		PropertyId pId = PropertyId.refer(name);
//		return property(pId, mergedList(pId, refPaths)) ;
//	}
	

	
	public WriteChildren refTo(String name, String refPath) {
		return property(PropertyId.refer(name), refPath) ;
	}

	private WriteChildren property(PropertyId pid, Object value) {
		set().put(pid.fullString(), value) ;
		return this;
	}
	
	private BasicDBList mergedList(PropertyId pId, Object...values){
		Object val = fields().get(pId.fullString()) ;
		BasicDBList result = null ;
		if (val == null) {
			result = new BasicDBList() ;
		} else if (BasicDBList.class.isInstance(val)){
			result = (BasicDBList)val ;
		} else {
			result = new BasicDBList() ;
			result.add(val) ;
		}
		
		for (Object v : values) {
			result.add(v) ;
		}
		
		return result ;
	}

	public IteratorList<WriteNode> iterator(){
		return eachNode(WriteChildrenEachs.ITERATOR) ;
	}




	
//	public WriteNode clear(){
//		touch(Touch.MODIFY) ;
//		
//		BasicDBObject newOb = new BasicDBObject() ;
//		for(String key : found().keySet()){
//			if (key.startsWith("_")) newOb.put(key, found().get(key)) ;
//		}
//		
//		super.found(newOb) ;
//		return this ;
//	}
}
