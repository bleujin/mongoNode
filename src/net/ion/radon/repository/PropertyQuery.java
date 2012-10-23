package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ARADON_GROUP;
import static net.ion.radon.repository.NodeConstants.ARADON_UID;
import static net.ion.radon.repository.NodeConstants.ID;

import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.util.DoubleKeyHashMap;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

public class PropertyQuery implements IPropertyFamily {

	private static final long serialVersionUID = -4825662715495266491L;
	private NodeObject nobject;
	public final static PropertyQuery EMPTY = new PropertyQuery(NodeObject.create());
	private transient DoubleKeyHashMap<String, PropertyQuery, Node> cacheMap = new DoubleKeyHashMap<String, PropertyQuery, Node>();

	private PropertyQuery(NodeObject nobject) {
		this.nobject = nobject;
	}

	public static PropertyQuery load(NodeObject query) {
		return new PropertyQuery(query);
	}

	public static PropertyQuery load(IPropertyFamily rquery) {
		return load(NodeObject.load(rquery.getDBObject()));
	}

	public static PropertyQuery createById(String objectId) {
		return PropertyQuery.create().put(ID, new ObjectId(objectId));
	}

	public static PropertyQuery createByAradon(String groupId, Object uId) {
		return PropertyQuery.create().aradonId(groupId, uId) ;
	}

	public static PropertyQuery createByAradon(String groupId) {
		return PropertyQuery.create().aradonGroup(groupId);
	}
	
	public static PropertyQuery createByPath(String path) {
		return PropertyQuery.create().path(path);
	}
	
	
	public static PropertyQuery create(String key, Object val) {
		return new PropertyQuery(NodeObject.create(key, val));
	}

	public static PropertyQuery create() {
		return new PropertyQuery(NodeObject.create());
	}

	
	public PropertyQuery aradonId(String groupId, Object uId){
//		BasicDBObject bdb = new BasicDBObject() ;
//		bdb.put("group", groupId) ;
//		bdb.put("uid", uId) ;
		
//		put(NodeConstants.ARADON, bdb) ;
		
		put(ARADON_GROUP, groupId);
		put(ARADON_UID, uId);
		// put(ARADON_GHASH, HashFunction.hashGeneral(groupId));

		return this ;
	}
	
	public PropertyQuery aradonGroup(String groupId){
//		BasicDBObject bdb = new BasicDBObject() ;
//		bdb.put("group", groupId) ;

		put(NodeConstants.ARADON_GROUP, groupId);
		return this ;
	}
	
	public PropertyQuery path(String path){
		put(NodeConstants.PATH, path);
		return this ;
	}

	public PropertyQuery put(String key, Object val) {

		nobject.put(key, val);
		return this;
	}

	public DBObject getDBObject() {
		return nobject.getDBObject();
	}

	public int size() {
		return nobject.size();
	}

	public Map<String, ? extends Object> toMap() {
		return nobject.toMap();
	}

	public boolean isEmpty(){
		return toMap().size() <= 0 ;
	}
	
	public String toString() {
		return nobject.toString();
	}

	public PropertyQuery and(IPropertyFamily... conds) {
		for (IPropertyFamily cond : conds) {
			for (Entry<String, ? extends Object> c : cond.toMap().entrySet()) {
				nobject.put(c.getKey(), c.getValue());
			}
		}
		// nobject.put("$and", list) ;
		return this;
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
		nobject.put("$or", list);
		return this;
	}

	public PropertyQuery in(String key, Object[] objects) {
		BasicDBList list = new BasicDBList();
		for (Object obj : objects) {
			list.add(obj);
		}

		nobject.put(key, PropertyQuery.create(QueryOperators.IN, list).getDBObject());
		return this;
	}

	public PropertyQuery nin(String key, Object[] objects) {
		BasicDBList list = new BasicDBList();
		for (Object obj : objects) {
			list.add(obj);
		}
		nobject.put(key, PropertyQuery.create(QueryOperators.NIN, list).getDBObject());
		return this;
	}

	public PropertyQuery ne(String key, Object value) {
		nobject.put(key, new BasicDBObject(QueryOperators.NE, value));
		return this;
	}

	public PropertyQuery eq(String key, Object value) {
		nobject.put(key, value);
		return this;
	}

	public PropertyQuery gt(String key, Object value) { // key > val
		nobject.put(key, new BasicDBObject(QueryOperators.GT, value));
		return this;
	}

	public PropertyQuery gte(String key, Object value) { // key >= val
		nobject.put(key, new BasicDBObject(QueryOperators.GTE, value));
		return this;
	}

	public PropertyQuery lt(String key, Object value) { // key < val
		nobject.put(key, new BasicDBObject(QueryOperators.LT, value));
		return this;
	}

	public PropertyQuery lte(String key, Object value) { // key <= val
		nobject.put(key, new BasicDBObject(QueryOperators.LTE, value));
		return this;
	}

	public PropertyQuery between(String key, Object openValue, Object closeValue) {
		BasicDBObject dbo = new BasicDBObject(QueryOperators.GTE, openValue);
		dbo.put(QueryOperators.LTE, closeValue);
		nobject.put(key, dbo);
		return this;
	}

	public PropertyQuery eleMatch(String key, PropertyQuery eleQuery) {
		nobject.put(key, new BasicDBObject("$elemMatch", eleQuery.getDBObject()));
		return this;
	}

	public PropertyQuery isExist(String key) {
		nobject.put(key, new BasicDBObject(QueryOperators.EXISTS, true));
		return this;
	}

	public PropertyQuery isNotExist(String key) {
		nobject.put(key, new BasicDBObject(QueryOperators.EXISTS, false));
		return this;
	}

	public PropertyQuery where(String where) {
		nobject.put(QueryOperators.WHERE, where);
		return this;
	}

	public PropertyQuery not(PropertyQuery query) {
		nobject.put("$not", query.getDBObject());
		return this;
	}
	

	public int hashCode() {
		return nobject.hashCode();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof PropertyQuery))
			return false;
		PropertyQuery that = (PropertyQuery) obj;
		return nobject.equals(that.nobject);
	}

	Node corelateNode(Session session, String wsName, PropertyQuery query) {
		if (!cacheMap.containsKey(wsName, query) || this == EMPTY) {
			Node node = session.getWorkspace(wsName).findOne(session, query, Columns.ALL);
			cacheMap.put(wsName, query, node);
		}
		return cacheMap.get(wsName, query);
	}


}
