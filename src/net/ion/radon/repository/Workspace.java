package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ARADON_GROUP;
import static net.ion.radon.repository.NodeConstants.ARADON_UID;
import static net.ion.radon.repository.NodeConstants.ID;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;

import org.apache.commons.collections.map.LRUMap;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.WriteResult;
import com.mongodb.MapReduceCommand.OutputType;

public class Workspace {

	private DBCollection collection;
	private static Map<String, Workspace> wss = new ConcurrentHashMap<String, Workspace>(new LRUMap(30));

	private Workspace(DBCollection collection) {
		this.collection = collection;
	}

	static Workspace load(DBCollection collection){
		String key = collection.getFullName();
		
		if (!wss.containsKey(key)) {
			final Workspace workspace = createWorkspace(collection);
		    wss.put(key, workspace);
			
			return workspace;
		} else {
			return wss.get(key);
		}
	}
	
	private static PropertyFamily ARADON_INDEX = PropertyFamily.create(ARADON_GROUP, 1).put(ARADON_UID, -1);
	private static PropertyFamily PATH_INDEX = PropertyFamily.create(NodeConstants.PATH, 1);


	private static Workspace createWorkspace(DBCollection collection) {
		Workspace result = new Workspace(collection) ;
		if (result.getName() == null || result.getName().startsWith("_")) return result ;

//		collection.ensureIndex(ARADON_INDEX.getDBObject(), PropertyFamily.create().put("name", "_aradon_id").put("unique", Boolean.TRUE).getDBObject());
//		collection.ensureIndex(PATH_INDEX.getDBObject(), PropertyFamily.create().put("name", "_path_id").put("unique", Boolean.TRUE).getDBObject());
		if (collection.getName().startsWith("system.")) return result ;
		
		collection.ensureIndex(ARADON_INDEX.getDBObject());
		// collection.ensureIndex(new BasicDBObject("__aradon.group", 1)) ;
		collection.ensureIndex(PATH_INDEX.getDBObject(), PropertyFamily.create().put("name", "_path_id").put("unique", Boolean.FALSE).getDBObject());
		
		return result;
	}

	
	public long count(){
		return getCollection().count() ;
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
	

	public int mergeNodes(Session session, Map<String, Node> modified) {
		int index = modified.size();

		Node[] targets = modified.values().toArray(new Node[0]);

		List<Node> forInsert = ListUtil.newList();
		List<Node> forUpdate = ListUtil.newList();
		for (Node node : targets) {
			if (node.isNew()) {
				forInsert.add(node);
			} else {
				forUpdate.add(node);
			}
		}
		
		for (Node updateNode : forUpdate) {
			session.getWorkspace(updateNode.getWorkspaceName()).save(session, updateNode);
		}

		for (Node insertNode : forInsert) {
			session.getWorkspace(insertNode.getWorkspaceName()).append(session, insertNode);
		}
		
		return index ;
	}

	
	public NodeCursorImpl find(Session session, PropertyQuery iquery, Columns columns) {
		return NodeCursorImpl.create(session, iquery, this.getName(), collection.find(iquery.getDBObject(), columns.getDBOjbect()));
	}
	
	public NodeResult remove(Session session, PropertyQuery query) {
		NodeResult result = NodeResult.create(query, collection.remove(query.getDBObject())) ;
		setLastResult(session, result) ;
		return result;
	}
	
	public Node findOne(Session session, PropertyQuery iquery, Columns column) {
		NodeCursor nc = find(session, iquery, column);
		Explain explain = nc.explain() ;
		
		Node result = (nc.hasNext()) ? nc.next() : null;
		session.setAttribute(Explain.class.getCanonicalName(), explain) ;
		
		return result ;
	}

	
	
	
	
	
	
	
	void makeUniqueIndex(IPropertyFamily props, String indexName) {
		makeIndex(props, indexName, true);
	}

	Node newNode(Session session) {
		final String newId = new ObjectId().toString();
		return newNode(session, newId, PropertyFamily.create(ID, new ObjectId(newId)));
	}

	Node newNode(Session session, String name) {
		return newNode(session, name, PropertyFamily.create());
	}


	NodeCursor mapreduce(Session session, String mapFunction, String reduceFunction, String finalFunction, CommandOption options, PropertyQuery condition) {
		if (StringUtil.isNotBlank(options.getOutputCollection()) && session.getWorkspace(options.getOutputCollection()).count() <= 0) {
			if (options.getOutputType() == OutputType.MERGE || options.getOutputType() == OutputType.REDUCE) {
				options.setOutputType(OutputType.REPLACE) ;
			}
		}
		
		MapReduceCommand command = new MapReduceCommand(collection, mapFunction, reduceFunction, options.getOutputCollection(), options.getOutputType(), condition.getDBObject());
		if (StringUtil.isNotBlank(finalFunction)) command.setFinalize(finalFunction) ;
		options.apply(command) ;

		MapReduceOutput out = collection.mapReduce(command) ;
		return ApplyCursor.create(session,  condition, out) ;
	}
	
	Object applyMapReduce(Session session, String mapFunction, String reduceFunction, String finalFunction, CommandOption options, PropertyQuery condition, ApplyHander handler) {
		// MapReduceOutput out = collection.mapReduce(mapFunction, reduceFunction, null, MapReduceCommand.OutputType.INLINE, condition.getDBObject()) ;
		NodeCursor nc = mapreduce(session, mapFunction, reduceFunction, finalFunction, options, condition) ;
		
		Object result = handler.handle(nc);
		return result ;
	}
	
	
	List<Node> group(Session session, IPropertyFamily keys, PropertyQuery condition, IPropertyFamily initial, String reduce) {
		BasicDBList list = (BasicDBList) collection.group(keys.getDBObject(), condition.getDBObject(), initial.getDBObject(), reduce) ;
		List<Node> nodes = ListUtil.newList();
		for(Object obj : list){
			nodes.add(NodeImpl.load(session, condition, getName(), (DBObject) obj));
		}
		return nodes ;

	}

	/* update start */

	NodeResult merge(Session session, MergeQuery query, TempNode tnode) {
		Map<String, Serializable> map = MapUtil.newMap() ;
		map.putAll(tnode.toMap()) ;

		Node found = findOne(session, PropertyQuery.load(query), Columns.append().add(NodeConstants.ID)) ;
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
		
		return updateNode(session, PropertyQuery.load(query), mod, true, true) ;
	}
	
	NodeResult findAndOverwrite(Session session, PropertyQuery query, Map<String, ?> props) {
		DBObject find = collection.findOne(query.getDBObject());
		if (find == null) return NodeResult.NULL;
		
		NodeImpl findNode = NodeImpl.load(session, query, collection.getName(), find);
		findNode.clearProp(false);
		
		for (Entry<String, ?> entry : props.entrySet()) {
			find.put(entry.getKey(), entry.getValue());
		}
		
		// collection.findAndModify(query.getDBObject(), NodeObject.load(props).getDBObject()) ;
		
		return NodeResult.create(query, collection.save(find));
	}

	NodeResult findAndUpdate(Session session, PropertyQuery query, Map<String, ?> props) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$set", appendLastModified(props)) ;
		
		return updateNode(session, query, mod, false, true) ;
	}
	
	NodeResult inc(Session session, PropertyQuery query, String propId, long value){
		DBObject mod = new BasicDBObject("$inc", new BasicDBObject(propId, value)) ;
		return updateNode(session, query, mod, true, true) ;
	}

	NodeResult setMerge(Session session, PropertyQuery query, Map<String, ?> values) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$set", appendLastModified(values)) ;
		
		return updateNode(session, query, mod, true, true) ;
	}
	
	NodeResult unset(Session session, PropertyQuery query, BasicDBObject value) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$unset", value) ;
		
		return updateNode(session, query, mod, false, true) ;
	}

	NodeResult pull(Session session, PropertyQuery query, Map<String, ?> values) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$pull", NodeObject.load(values).getDBObject()) ;
		
		return updateNode(session, query, mod, false, true) ;
	}

	NodeResult push(Session session, PropertyQuery query, Map<String, ?> values) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$push", NodeObject.load(values).getDBObject()) ;
		return updateNode(session, query, mod, false, true) ;
	}
	
	

	private NodeResult updateNode(Session session, PropertyQuery query, DBObject values, boolean upset, boolean multi){
		WriteResult wr = collection.update(query.getDBObject(), values, upset, multi);
		NodeResult result = NodeResult.create(query, wr);
		setLastResult(session, result) ;
		return result ;
	}
	


	/* update end */
	
	
	
	
	List<DBObject> getIndexInfo() {
		return collection.getIndexInfo();
	}
	
	private DBCollection getCollection(){
		return collection ;
	}

	private Node newNode(Session session, String name, PropertyFamily props) {
		return NodeImpl.create(session, this.getName(), NodeObject.load(props.getDBObject()), "/", name);
	}

	private final static String RESULT_KEY = NodeResult.class.getCanonicalName() ;
	private void setLastResult(Session session, NodeResult result) {
		session.setAttribute(RESULT_KEY, result);
	}

	private NodeResult save(Session session, Node node) {
		DBObject inmod = node.getDBObject();
		inmod.put(NodeConstants.LASTMODIFIED, GregorianCalendar.getInstance().getTimeInMillis()) ;
//		DBObject mod = new BasicDBObject("$set", inmod) ;
// 		return NodeResult.create(collection.save(inmod)) ;
		
		return updateNode(session, PropertyQuery.createById(node.getIdentifier()), inmod, true, false) ;
	}
	
	private NodeResult append(Session session, Node node){
		DBObject inmod = node.getDBObject();
		inmod.put(NodeConstants.LASTMODIFIED, GregorianCalendar.getInstance().getTimeInMillis()) ;
		
		WriteResult wr = collection.insert(inmod);
		NodeResult result = NodeResult.create(node.getQuery(), wr);
		setLastResult(session, result) ;
		return result ;
	}
	
	private DBObject appendLastModified(Map<String, ?> values) {
		DBObject inmod = NodeObject.load(values).getDBObject();
		inmod.put(NodeConstants.LASTMODIFIED, GregorianCalendar.getInstance().getTimeInMillis()) ;
		return inmod;
	}
	
	public String toString(){
		return collection.getFullName() ;
	}



	
}
