package net.ion.radon.repository;


import static net.ion.radon.repository.NodeConstants.ARADON_GROUP;
import static net.ion.radon.repository.NodeConstants.ARADON_UID;
import static net.ion.radon.repository.NodeConstants.ARADON_GHASH;
import static net.ion.radon.repository.NodeConstants.ID;

import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.util.HashFunction;
import net.ion.radon.repository.myapi.AradonQuery;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class PropertyQuery implements IPropertyFamily{
	
	private static final long serialVersionUID = -4825662715495266491L;
	private NodeObject nobject ;
	public final static PropertyQuery EMPTY = new PropertyQuery(NodeObject.create()) ;
	
	private PropertyQuery(NodeObject nobject) {
		this.nobject = nobject ;
	}

	public static PropertyQuery load(NodeObject query) {
		return new PropertyQuery(query);
	}

	public static PropertyQuery createById(String objectId) {
		return new PropertyQuery(NodeObject.create(ID, new ObjectId(objectId)));
	}
	
	public static PropertyQuery createByAradon(String groupId, Object uId) {
		PropertyQuery query = PropertyQuery.create();
		query.put(ARADON_GROUP, groupId);
		query.put(ARADON_UID, uId);
		query.put(ARADON_GHASH, HashFunction.hashGeneral(groupId));

		return query;
	}

	public static PropertyQuery createByAradon(String groupId) {
		PropertyQuery query = PropertyQuery.create();
		query.put(ARADON_GROUP, groupId);

		return query;
	}

	public static PropertyQuery create(String key, Object val) {
		return new PropertyQuery(NodeObject.create(key, val)) ;
	}

	public static PropertyQuery create() {
		return new PropertyQuery(NodeObject.create());
	}

//	public Object put(String key, String val) {
//		return bdbo.put(key, val) ;
//	}
	
	public PropertyQuery put(AradonQuery query){
		nobject.put(ARADON_GROUP, query.getGroupId()) ;
		if (query.getUId() != null) nobject.put(ARADON_UID, query.getUId()) ;
		return this ;
	}
	
	public PropertyQuery put(String key, Object val) {
		
		nobject.put(key, val) ;
		return this ;
	}

	public DBObject getDBObject() {
		return nobject.getDBObject();
	}

	public int size() {
		return nobject.size() ;
	}
	public Map<String, ? extends Object> toMap() {
		return nobject.toMap();
	}
	
	public String toString(){
		return nobject.toString() ;
	}

	public PropertyQuery and(IPropertyFamily... conds) {
		BasicDBList list = makeListCondition(conds);
		for (IPropertyFamily cond : conds) {
			for (Entry<String, ? extends Object> c : cond.toMap().entrySet()) {
				nobject.put(c.getKey(), c.getValue()) ;
			}
		}		
//		nobject.put("$and", list) ;
		return this ;
	}

	private BasicDBList makeListCondition(IPropertyFamily... conds) {
		BasicDBList list = new BasicDBList();
		for (IPropertyFamily condition : conds) {
			list.add(condition.getDBObject());
		}
		return list;
	}
	
	public PropertyQuery or(IPropertyFamily... conds) {
		BasicDBList list = makeListCondition(conds);
		nobject.put("$or", list) ;
		return this ;
	}
	
	public PropertyQuery in(String key, Object[] objects) {
		BasicDBList list = new BasicDBList();
		for (Object obj : objects) {
			list.add(obj);
		}
		
		nobject.put(key, PropertyQuery.create("$in", list).getDBObject());
		return this ;
	}
	
	public PropertyQuery nin(String key, Object[] objects){
		BasicDBList list = new BasicDBList();
		for(Object obj : objects){
			list.add(obj);
		}
		nobject.put(key, PropertyQuery.create("$nin", list).getDBObject());
		return this;
	}
	
	public PropertyQuery ne(String key, Object value) {
		nobject.put(key,  new BasicDBObject("$ne", value));
		return this ;
	}

	public PropertyQuery eq(String key, Object value) {
		nobject.put(key,  value);
		return this ;
	}

	public PropertyQuery gt(String key, Object value) { // key > val
		nobject.put(key, new BasicDBObject("$gt", value)) ;
		return this ;
	}

	public PropertyQuery gte(String key, Object value) { // key >= val
		nobject.put(key, new BasicDBObject("$gte", value)) ;
		return this ;
	}

	public PropertyQuery lt(String key, Object value) { // key < val
		nobject.put(key, new BasicDBObject("$lt", value)) ;
		return this ;
	}

	public PropertyQuery lte(String key, Object value) { // key <= val
		nobject.put(key, new BasicDBObject("$lte", value)) ;
		return this ;
	}
	
	public PropertyQuery between(String key, Object openValue, Object closeValue) {
		BasicDBObject dbo = new BasicDBObject("$gte", openValue) ;
		dbo.put("$lte", closeValue) ;
		nobject.put(key, dbo) ;
		return this;
	}

	public PropertyQuery eleMatch(String key, PropertyQuery eleQuery) {
		nobject.put(key, new BasicDBObject("$elemMatch", eleQuery.getDBObject())) ;
		return this ;
	}


	public PropertyQuery isExist(String key) {
		nobject.put(key, new BasicDBObject("$exists", true)) ;
		return this ;
	}
	
	public PropertyQuery isNotExist(String key) {
		nobject.put(key, new BasicDBObject("$exists", false)) ;
		return this ;
	}

	public PropertyQuery where(String where) {
		nobject.put("$where", where) ;
		return this ;
	}

	public PropertyQuery not(PropertyQuery query) {
		nobject.put("$not", query.getDBObject()) ;
		return this;
	}




}
