package net.ion.repository.mongo.node;

import net.ion.framework.util.StringUtil;
import net.ion.repository.mongo.Fqn;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

public abstract class AbstractChildren<T extends NodeCommon<T>, C extends AbstractChildren> {


	private DBObject filters = new BasicDBObject() ;
	private BasicDBObject fields = new BasicDBObject() ;

	protected DBObject filters(){
		return filters ;
	}
	
	private C putFilter(String propId, Object value){
		filters.put(StringUtil.lowerCase(propId), value) ;
		return (C) this ;
	}
	
	protected C put(String propId, Object val){
		return putFilter(propId, val) ;
	}
	
	public C gt(String propId, Object value) {
		return putFilter(propId, new BasicDBObject(QueryOperators.GT, value)) ;
	}


	public C gte(String propId, Object value) {
		return putFilter(propId, new BasicDBObject(QueryOperators.GTE, value)) ;
	}


	public C lt(String propId, Object value) {
		return putFilter(propId, new BasicDBObject(QueryOperators.LT, value)) ;
	}


	public C lte(String propId, Object value) {
		return putFilter(propId, new BasicDBObject(QueryOperators.LTE, value)) ;
	}


	public C eq(String propId, Object value) {
		return putFilter(propId, value) ;
	}


	public C between(String propId, Object min, Object max){
		BasicDBObject between = new BasicDBObject() ;
		between.put(QueryOperators.GTE, min) ;
		between.put(QueryOperators.LTE, max) ;
		return putFilter(propId, between) ;
	}

	public C exist(String propId) {
		return putFilter(propId, new BasicDBObject(QueryOperators.EXISTS, true)) ;
	}

	public C notExist(String propId) {
		return putFilter(propId, new BasicDBObject(QueryOperators.EXISTS, false)) ;
	}

	public C in(String propId, Object... values){
		BasicDBList blist = new BasicDBList() ;
		for (Object val : values) {
			blist.add(val) ;
		}
		return putFilter(propId, new BasicDBObject(QueryOperators.IN, blist)) ;
	}
	
	
//	public C exclude(String... propIds){
//		for (String propId : propIds) {
//			fields().put(propId, 0) ;
//		}
//		return (C) this ;
//	}
	
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






