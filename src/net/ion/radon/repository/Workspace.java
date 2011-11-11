package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ARADON_GROUP;
import static net.ion.radon.repository.NodeConstants.ARADON_UID;
import static net.ion.radon.repository.NodeConstants.ID;
import static net.ion.radon.repository.NodeConstants.PATH;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.map.LRUMap;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.WriteResult;

public class Workspace {

	private DBCollection collection;
	private Repository repository;
	private static Map<String, Workspace> wss = new ConcurrentHashMap<String, Workspace>(new LRUMap(20));

	private Workspace(Repository repository, DBCollection collection) {
		this.repository = repository;
		this.collection = collection;
	}

	static Workspace create(Repository repository, DBCollection collection){
		if (!wss.containsKey(collection.getFullName())) {
			final Workspace workspace = createWorkspace(repository, collection);
		    wss.put(collection.getFullName(), workspace);
			
			return workspace;
		} else {
			return wss.get(collection.getFullName());
		}
	}
	
	private static PropertyFamily ARADON_INDEX = PropertyFamily.create(ARADON_GROUP, 1).put(ARADON_UID, -1);
	private static PropertyFamily PATH_INDEX = PropertyFamily.create(NodeConstants.PATH, 1);


	private static Workspace createWorkspace(Repository repository, DBCollection collection) {
		Workspace result = new Workspace(repository, collection) ;
		if (result.getName() == null || result.getName().startsWith("_")) return result ;

//		NodeCursor.create(result, collection.find()).each(PageBean.ALL, new Closure(){
//			public void execute(Object _node) {
//				Debug.line(((Node)_node).getAradonId(), _node) ;
//			}
//		}) ;
		
//		collection.ensureIndex(ARADON_INDEX.getDBObject(), PropertyFamily.create().put("name", "_aradon_id").put("unique", Boolean.TRUE).getDBObject());
//		collection.ensureIndex(PATH_INDEX.getDBObject(), PropertyFamily.create().put("name", "_path_id").put("unique", Boolean.TRUE).getDBObject());
		if (collection.getName().startsWith("system.")) return result ;
		
		collection.ensureIndex(ARADON_INDEX.getDBObject());
		// collection.ensureIndex(new BasicDBObject("__aradon.group", 1)) ;
		collection.ensureIndex(PATH_INDEX.getDBObject(), PropertyFamily.create().put("name", "_path_id").put("unique", Boolean.FALSE).getDBObject());
		
		return result;
	}

	NodeCursor mapreduce(String mapFunction, String reduceFunction, String finalFunction, CommandOption options, PropertyQuery condition) {
		MapReduceCommand command = new MapReduceCommand(collection, mapFunction, reduceFunction, null, MapReduceCommand.OutputType.INLINE, condition.getDBObject());
		if (StringUtil.isNotBlank(finalFunction)) command.setFinalize(finalFunction) ;
		options.apply(command) ;

		MapReduceOutput out = collection.mapReduce(command) ;
		return ApplyCursor.create(condition, out) ;
	}
	
	Object applyMapReduce(String mapFunction, String reduceFunction, String finalFunction, CommandOption options, PropertyQuery condition, ApplyHander handler) {
		String outputColName = null ;
		MapReduceCommand command = new MapReduceCommand(collection, mapFunction, reduceFunction, outputColName, MapReduceCommand.OutputType.INLINE, condition.getDBObject());
		if (StringUtil.isNotBlank(finalFunction)) command.setFinalize(finalFunction) ;
		options.apply(command) ;

		MapReduceOutput out = collection.mapReduce(command) ;
		// MapReduceOutput out = collection.mapReduce(mapFunction, reduceFunction, null, MapReduceCommand.OutputType.INLINE, condition.getDBObject()) ;
		NodeCursor nc = mapreduce(mapFunction, reduceFunction, finalFunction, options, condition) ;
		
		Object result = handler.handle(nc);
		return result ;
	}
	
	
	List<Node> group(IPropertyFamily keys, PropertyQuery condition, IPropertyFamily initial, String reduce) {
		BasicDBList list = (BasicDBList) collection.group(keys.getDBObject(), condition.getDBObject(), initial.getDBObject(), reduce) ;
		List<Node> nodes = ListUtil.newList();
		for(Object obj : list){
			nodes.add(NodeImpl.load(condition, getName(), (DBObject) obj));
		}
		return nodes ;

	}
	
	
	public String getName() {
		return collection.getName();
	}
	
	public void drop() {
		wss.remove(collection.getFullName());
		collection.drop();
	}
	
	public void makeIndex(IPropertyFamily props, String indexName, boolean unique) {
		BasicDBObject options = new BasicDBObject() ;
		options.put("name", indexName) ;
		options.put("unique", Boolean.TRUE) ;
		
		collection.ensureIndex(props.getDBObject(), options) ;
	}
	
	public void makeUniqueIndex(IPropertyFamily props, String indexName) {
		makeIndex(props, indexName, true);
	}

	long count(IPropertyFamily pf) {
		return collection.count(pf.getDBObject());
	}

	Node newNode(String name, PropertyFamily props) {
		return NodeImpl.create(this.getName(), NodeObject.load(props.getDBObject()), "/", name);
	}

	Node newNode() {
		final String newId = new ObjectId().toString();
		return newNode(newId, PropertyFamily.create(ID, new ObjectId(newId)));
	}

	Node newNode(String name) {
		return newNode(name, PropertyFamily.create());
	}



	/* update start */

	NodeResult merge(MergeQuery query, TempNode tnode) {
		Map<String, Serializable> map = MapUtil.newMap() ;
		map.putAll(tnode.toMap()) ;

		Node found = findOne(PropertyQuery.load(query), Columns.append().add(NodeConstants.ID)) ;
		if (found != null){
			tnode.putProperty(PropertyId.reserved(NodeConstants.ID), found.getId()) ;
		} else { // if newNode
			Map queryMap = query.getDBObject().toMap();
			for (Object key : queryMap.keySet()) {
				Object value = queryMap.get(key) ;
				if (value instanceof DBObject){
					continue ;
				}
				map.put(key.toString(), (Serializable)value) ;
			}
		}
		
		DBObject mod = new BasicDBObject() ;
		mod.put("$set", appendLastModified(map)) ;
		
		return updateNode(PropertyQuery.load(query), mod, true, true) ;
	}

	
	Node findAndRemove(PropertyQuery pquery) {
		final DBObject dbo = collection.findAndRemove(pquery.getDBObject());
		return NodeImpl.load(pquery, collection.getName(), dbo);
	}

	NodeResult findAndOverwrite(PropertyQuery query, Map<String, ?> props) {
		DBObject find = collection.findOne(query.getDBObject());
		if (find == null) return NodeResult.NULL;
		
		NodeImpl findNode = NodeImpl.load(query, collection.getName(), find);
		findNode.clearProp(false);
		
		for (Entry<String, ?> entry : props.entrySet()) {
			find.put(entry.getKey(), entry.getValue());
		}
		
		// collection.findAndModify(query.getDBObject(), NodeObject.load(props).getDBObject()) ;
		
		return NodeResult.create(query, collection.save(find));
	}

	NodeResult findAndUpdate(PropertyQuery query, Map<String, ?> props) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$set", appendLastModified(props)) ;
		
		return updateNode(query, mod, false, true) ;
	}
	
	NodeResult inc(PropertyQuery query, String propId, int value){
		DBObject mod = new BasicDBObject("$inc", new BasicDBObject(propId, value)) ;
		return updateNode(query, mod, true, true) ;
	}

	NodeResult set(PropertyQuery query, Map<String, ?> values) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$set", appendLastModified(values)) ;
		
		return updateNode(query, mod, false, true) ;
	}

	public NodeResult unset(PropertyQuery query, BasicDBObject value) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$unset", value) ;
		
		return updateNode(query, mod, false, true) ;
	}


	NodeResult pull(PropertyQuery query, Map<String, ?> values) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$pull", NodeObject.load(values).getDBObject()) ;
		
		return updateNode(query, mod, false, true) ;
	}

	NodeResult push(PropertyQuery query, Map<String, ?> values) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$push", NodeObject.load(values).getDBObject()) ;
		return updateNode(query, mod, false, true) ;
	}

	NodeResult updateNode(PropertyQuery query, DBObject values, boolean upset, boolean multi){
		WriteResult wr = collection.update(query.getDBObject(), values, upset, multi);
		return NodeResult.create(query, wr) ;
	}

	NodeResult save(Node node) {
		DBObject inmod = node.getDBObject();
		inmod.put(NodeConstants.LASTMODIFIED, GregorianCalendar.getInstance().getTimeInMillis()) ;
//		DBObject mod = new BasicDBObject("$set", inmod) ;
// 		return NodeResult.create(collection.save(inmod)) ;
		
		return updateNode(PropertyQuery.createById(node.getIdentifier()), inmod, true, false) ;
	}
	
	NodeResult append(Node node){
		DBObject inmod = node.getDBObject();
		inmod.put(NodeConstants.LASTMODIFIED, GregorianCalendar.getInstance().getTimeInMillis()) ;
		
		WriteResult wr = collection.insert(inmod);
		return NodeResult.create(node.getQuery(), wr) ;
	}
	
	private DBObject appendLastModified(Map<String, ?> values) {
		DBObject inmod = NodeObject.load(values).getDBObject();
		inmod.put(NodeConstants.LASTMODIFIED, GregorianCalendar.getInstance().getTimeInMillis()) ;
		return inmod;
	}

	/* update end */
	
	
	
	
	
	

	Node findOne(PropertyQuery iquery) {
		return findOne(iquery, Columns.ALL);
	}
	
	public Node findOne(PropertyQuery iquery, Columns column) {
		NodeCursor nc = find(iquery, column);
		Explain explain = nc.explain() ;
		
		Node result = (nc.hasNext()) ? nc.next() : null;
		Session.getCurrent().setAttribute(Explain.class.getCanonicalName(), explain) ;
		
		return result ;
	}

	
	Node findByPath(String path) {
		return  findOne(PropertyQuery.create(PATH, path));
	}

	NodeCursorImpl find(PropertyQuery iquery) {
		return NodeCursorImpl.create(iquery, this.getName(), collection.find(iquery.getDBObject()));
	}

	NodeCursorImpl find(PropertyQuery iquery, Columns columns) {
		return NodeCursorImpl.create(iquery, this.getName(), collection.find(iquery.getDBObject(), columns.getDBOjbect()));
	}

	
	

	NodeResult remove(Node node) {
		PropertyQuery query = PropertyQuery.createById(node.getIdentifier());
		NodeResult result = NodeResult.create(query, collection.remove(query.getDBObject())) ;
		getReferenceManager().removeAboutReference(node) ;
		return result;
	}

	NodeResult removeQuery(PropertyQuery query) {
		NodeResult result = NodeResult.create(query, collection.remove(query.getDBObject())) ;
		return result;
	}
	
	Node getNodeById(String objectId) {
		PropertyQuery query = PropertyQuery.createById(objectId);
		return NodeImpl.load(query, collection.getName(), collection.findOne(query.getDBObject()));
	}
	
	ReferenceManager getReferenceManager() {
		return repository.getReferenceManager();
	}

	List<DBObject> getIndexInfo() {
		return collection.getIndexInfo();
	}

	public String toString(){
		return collection.getFullName() ;
	}

	public DBCollection getCollection(){
		return collection ;
	}




	
}
