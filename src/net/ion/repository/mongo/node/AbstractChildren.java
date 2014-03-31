package net.ion.repository.mongo.node;

import net.ion.repository.mongo.Fqn;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public abstract class AbstractChildren<T extends NodeCommon<T>, C extends AbstractChildren> {


	private DBObject filters = new BasicDBObject() ;
	private BasicDBObject fields = new BasicDBObject() ;
	private final Fqn parent;

	protected AbstractChildren(Fqn parent){
		this.filters.put("_parent", parent.toString()) ;
		this.parent = parent ;
	}
	
	public DBObject filters(){
		return filters ;
	}
	
	public C gt(String propId, Object value) {
		filters.put(propId, new BasicDBObject("$gt", value)) ;
		return (C) this ;
	}


	public C gte(String propId, Object value) {
		filters.put(propId, new BasicDBObject("$gte", value)) ;
		return (C) this ;
	}


	public C lt(String propId, Object value) {
		filters.put(propId, new BasicDBObject("$lt", value)) ;
		return (C) this ;
	}


	public C lte(String propId, Object value) {
		filters.put(propId, new BasicDBObject("$lte", value)) ;
		return (C) this ;
	}


	public C eq(String propId, Object value) {
		filters.put(propId, value) ;
		return (C) this ;
	}


	public C between(String propId, Object min, Object max){
		BasicDBObject between = new BasicDBObject() ;
		between.put("$gte", min) ;
		between.put("$lte", max) ;
		filters.put(propId, between) ;
		return (C) this ;
	}

	
	public C in(String propId, Object... values){
		BasicDBList blist = new BasicDBList() ;
		for (Object val : values) {
			blist.add(val) ;
		}
		filters.put(propId, new BasicDBObject("$in", blist)) ;
		return (C) this ;
	}
	
	
	public C exclude(String... propIds){
		for (String propId : propIds) {
			fields.put(propId, 0) ;
		}
		return (C) this ;
	}
	
	protected DBObject fields(){
		return fields ;
	}
	
	
	
	

//	public Iterator<T> iterator(){
//		return toList().iterator() ;
//	}


//	public <F> F transform(Function<Iterator<T>, F> fn){
//		return fn.apply(iterator()) ;
//	}
//
//	public void debugPrint() {
//		while(hasNext()){
//			T node = next();
//			node.session().credential().tracer().println(node) ;
//		}
//	}
	
}






