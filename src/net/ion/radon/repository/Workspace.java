package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ARADON_GROUP;
import static net.ion.radon.repository.NodeConstants.ARADON_UID;
import static net.ion.radon.repository.NodeConstants.ID;
import static net.ion.radon.repository.NodeConstants.PATH;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.map.LRUMap;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
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
	

	private static Workspace createWorkspace(Repository repository, DBCollection collection) {
		Workspace result = new Workspace(repository, collection) ;
		if (result.getName().startsWith("_")) return result ;
		
		PropertyFamily props = PropertyFamily.create(ARADON_GROUP, 1).put(ARADON_UID, -1);
		collection.ensureIndex(props.getDBObject());
		
		return result;
	}


	public List<Node> group(PropertyQuery keys, PropertyQuery conds, PropertyQuery initial, String reduce) {
		BasicDBList list = (BasicDBList) collection.group(keys.getDBObject(), conds.getDBObject(), initial.getDBObject(), reduce);
		List<Node> nodes = ListUtil.newList();
		for(Object obj : list){
			nodes.add(NodeImpl.load(collection.getName(), (DBObject) obj));
		}
		return nodes;
	}
	
	public String getName() {
		return collection.getName();
	}
	
	public void drop() {
		wss.remove(collection.getFullName());
		collection.dropIndexes() ;
		collection.drop();
	}
	
	public void makeIndex(IPropertyFamily props, String indexName, boolean unique) {
		collection.ensureIndex(props.getDBObject(), indexName, unique) ;
	}
	
	public void makeUniqueIndex(IPropertyFamily props, String indexName) {
		makeIndex(props, indexName, true);
	}

	public String toString(){
		return collection.getFullName() ;
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

	Node findAndRemove(IPropertyFamily pfQuery) {
		final DBObject dbo = collection.findAndRemove(pfQuery.getDBObject());
		if (dbo == null)
			return null;
		return NodeImpl.load(collection.getName(), dbo);
	}

	NodeResult findAndOverwrite(IPropertyFamily query, Map<String, ?> props) {
		DBObject find = collection.findOne(query.getDBObject());
		if (find == null) return NodeResult.NULL;
		
		NodeImpl findNode = NodeImpl.load(collection.getName(), find);
		findNode.clearProp(false);
		
		for (Entry<String, ?> entry : props.entrySet()) {
			find.put(entry.getKey(), entry.getValue());
		}
		
		return NodeResult.create(collection.save(find));
	}

	NodeResult findAndUpdate(IPropertyFamily query, Map<String, ?> props) {
		DBObject find = collection.findOne(query.getDBObject());
		if (find == null) return NodeResult.NULL;
		
		for (Entry<String, ?> entry : props.entrySet()) {
			find.put(entry.getKey(), entry.getValue());
		}
		return NodeResult.create(collection.save(find));
	}
	
	
	
	

	public Node merge(String path, JSONObject jso) {
		Node findNode = findByPath(path);
		if (findNode == null) {
			String name = StringUtil.substringAfterLast(path, "/") ;
			String parentPath = StringUtil.defaultIfEmpty(StringUtil.substringBeforeLast(path, "/"), "/") ;
			findNode = NodeImpl.create(getName(), NodeObject.create(), parentPath, name);
		} else {
			findNode.clearProp();
		}
		
		Iterator<String> kiter = jso.keys();
		while (kiter.hasNext()) {
			String key = kiter.next();
			recursiveSave(findNode, key, jso.get(key));
		}
		collection.save(findNode.getDBObject());
		return findNode ;
	}

	
	private void recursiveSave(INode node, String key, Object obj) {
		if (obj instanceof JSONArray) {
			JSONArray jsa = (JSONArray) obj;
			Iterator iter = jsa.iterator();
			while (iter.hasNext()) {
				recursiveSave(node, key, iter.next());
			}
		} else if (obj instanceof JSONObject) {
			InNode inner = node.inner(key);
			JSONObject jso = (JSONObject) obj;
			Iterator<String> kiter = jso.keys();
			while (kiter.hasNext()) {
				String ikey = kiter.next();
				recursiveSave(inner, ikey, jso.get(ikey));
			}
		} else {
			node.append(key, obj);
		}
	}
	

	NodeResult update(IPropertyFamily query, Map<String, ?> values) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$set", new BasicDBObject(values)) ;
		
		WriteResult result = collection.updateMulti(query.getDBObject(), mod) ;
		return NodeResult.create(result) ;
	}
	
	NodeResult pull(PropertyQuery query, Map<String, ?> values) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$pull", new BasicDBObject(values)) ;
		
		WriteResult result = collection.updateMulti(query.getDBObject(), mod) ;
		return NodeResult.create(result) ;
	}

	NodeResult push(PropertyQuery query, Map<String, ?> values) {
		DBObject mod = new BasicDBObject() ;
		mod.put("$push", values) ;
		
		WriteResult result = collection.updateMulti(query.getDBObject(), mod) ;
		return NodeResult.create(result) ;
	}



	Node merge(IPropertyFamily query, Node modNode) {
		DBObject find = collection.findOne(query.getDBObject());

		if (find != null) {
			for (Entry<String, ?> entry : modNode.toPropertyMap().entrySet()) {
				find.put(entry.getKey(), entry.getValue());
			}
			collection.save(find);
			return NodeImpl.load(collection.getName(), find);
		} else {
			collection.save(modNode.getDBObject()) ;
			return modNode ;
		}
	}
	Node findOne(IPropertyFamily af) {
		final DBObject one = collection.findOne(af.getDBObject());
		if (one == null)
			return null;
		return NodeImpl.load(collection.getName(),  one);
	}
	
	Node findByPath(String path) {
		return  findOne(PropertyQuery.create(PATH, path));
	}

	NodeCursor find(IPropertyFamily af) {
		return NodeCursor.create(this, collection.find(af.getDBObject()));
	}

	NodeCursor find() {
		return NodeCursor.create(this, collection.find());
	}
	
	
	
	
	
	NodeResult save(Node node) {
		final WriteResult wresult = collection.save(node.getDBObject());
		return NodeResult.create(wresult);
	}
	
	NodeResult merge(Node newNode) {
		final WriteResult wresult = collection.update(PropertyQuery.createById(newNode.getIdentifier()).getDBObject(), newNode.getDBObject());
		if (wresult.getN() <1) collection.insert(newNode.getDBObject());
		return NodeResult.create(wresult);
	}

	NodeResult remove(Node node) {
		NodeResult result = NodeResult.create(collection.remove(PropertyQuery.createById(node.getIdentifier()).getDBObject())) ;
		getReferenceManager().removeAboutReference(node) ;
		return result;
	}

	NodeResult removeQuery(IPropertyFamily query) {
		NodeResult result = NodeResult.create(collection.remove(query.getDBObject())) ;
		return result;
	}
	
	Node getNodeById(String objectId) {
		return NodeImpl.load(collection.getName(), collection.findOne(PropertyQuery.createById(objectId).getDBObject()));
	}
	
	ReferenceManager getReferenceManager() {
		return repository.getReferenceManager();
	}

	List<DBObject> getIndexInfo() {
		return collection.getIndexInfo();
	}



	
}
