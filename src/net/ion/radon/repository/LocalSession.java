package net.ion.radon.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.AradonQuery;

public class LocalSession extends Session {


	private Repository repository;
	private Workspace workspace;
	private Map<String, Node> modified = MapUtil.newMap();
	private final RootNode root;
	private Map<String, Object> attributes = MapUtil.newCaseInsensitiveMap() ;
	
	private LocalSession(Repository repository, String wname) {
		this.repository = repository;
		this.workspace = repository.getWorkspace(wname);
		this.root = new RootNode(this) ; 
	}

	static synchronized Session create(Repository repository, String wname) {
		Session session = getThreadLocal().get();
		if (session == null) {
			session = new LocalSession(repository, wname);
			getThreadLocal().set(session);
		} else {
			session.changeWorkspace(wname) ;
		}
		return session;
	}

	public Session changeWorkspace(String wname) {
		this.workspace = repository.getWorkspace(wname);
		return this ;
	}


	public ReferenceManager getReferenceManager() {
		return repository.getReferenceManager();
	}
	
	public ISequence getSequence(String prefix, String id) {
		return ISequence.createOrLoad(repository.getWorkspace("_sequence"), prefix, id) ;
	}

	public static Session getCurrent() {
		Session session = getThreadLocal().get();
		
		if (session == null){
			throw new IllegalStateException("not logined");
		}

		return session;
	}

	public int commit() {
		int index = modified.size();
		
		Node[] targets = modified.values().toArray(new Node[0]);
		
		List<Node> forInsert = ListUtil.newList() ;
		List<Node> forUpdate = ListUtil.newList() ;
		for (Node node : targets) {
			if (node.isNew()) {
				forInsert.add(node) ;
			} else {
				forUpdate.add(node) ;
			}
		}
		String canonicalName = NodeResult.class.getCanonicalName();
		for (Node updateNode : forUpdate) {
			NodeResult nodeResult = repository.getWorkspace(updateNode.getWorkspaceName()).save(updateNode);
			setAttribute(canonicalName, nodeResult) ;
		}
		
		for (Node insertNode : forInsert) {
			NodeResult nodeResult = repository.getWorkspace(insertNode.getWorkspaceName()).append(insertNode);
			setAttribute(canonicalName, nodeResult) ;
		}
		
		this.clear();
		return index;
	}
	
	public NodeResult getLastResultInfo(){
		return getAttribute(NodeResult.class.getCanonicalName(), NodeResult.class) ;
	}

	void notify(Node target, NodeEvent event) {
		modified.put(target.getIdentifier(), target);
	}

	public void dropWorkspace() {
		workspace.drop();
	}

	public Node newNode() {
		return workspace.newNode();
	}

	public TempNode tempNode(){
		return TempNodeImpl.create(NodeObject.create()) ;
	}
	
	public Node mergeNode(MergeQuery mergeQuery) {
		
		Node found = SessionQuery.create(this, PropertyQuery.load(NodeObject.load(mergeQuery.getDBObject()))).findOne(Columns.Meta) ;
		if (found == null){
			Node newNode = newNode() ;
			Map<String, ? extends Object> queryMap = mergeQuery.data();
			for (Entry<String, ? extends Object> entry : queryMap.entrySet()) {
				newNode.getDBObject().put(entry.getKey().toLowerCase(), entry.getValue()) ;
			}
			return newNode ;
		} else {
			
			NodeObject metaInfo = NodeObject.create();
			for (String metakey : Columns.MetaColumns) {
				metaInfo.putProperty( PropertyId.reserved(metakey), (Object)found.get(metakey)) ;
			} 
			return NodeImpl.load(getCurrentWorkspaceName(), metaInfo) ;
		}
	}

	public Node newNode(String name) {
		return workspace.newNode(name);
	}

	List<Node> findAllWorkspace(AradonQuery query) {
		if (query.getUId() != null){
			Node node = createQuery().findOneInDB(query.getGroupId(), query.getUId()) ;
			return (node == null) ? ListUtil.EMPTY : ListUtil.create(node) ;
		} else {
			List<Node> result = ListUtil.newList() ;
			for(String wname : repository.getWorkspaceNames()){
				result.addAll(repository.getWorkspace(wname).find(query.getQuery()).toList(PageBean.ALL)) ;
			}
			return result;
		}
	}
	

	public String getCurrentWorkspaceName() {
		return workspace.getName();
	}

	public NodeResult remove(Node node) {
		if (node == getRoot()) return NodeResult.create(null) ;
		return repository.getWorkspace(node.getWorkspaceName()).remove(node);
	}

	Node createChild(Node parent, String name) {
		final NodeImpl newNode = NodeImpl.create(workspace.getName(), NodeObject.create(), parent.getPath(), name);
		getReferenceManager().addChildReference(parent, name, newNode);

		return newNode;
	}

	public Collection<Node> getModified() {
		return modified.values();
	}

	public void clear() {
		modified.clear();
	}

	
	
	public void logout() {
		getCurrent().clear() ;
		getThreadLocal().remove() ;
	}

	
	
	public Workspace getCurrentWorkspace() {
		return workspace;
	}
	
	void dropDB() {
		repository.getDB().dropDatabase() ;
	}
	
	public Node getRoot() {
		return root;
	}

	public ReferenceQuery createRefQuery(){
		return ReferenceQuery.create(repository.getReferenceManager());
	}
	     
	public Node addReference(Node src, String relType, Node target) {
		return getReferenceManager().addReference(src, relType, target);
	}


	public SessionQuery createQuery() {
		return SessionQuery.create(this);
	}

	SessionQuery createQuery(PropertyQuery definedQuery) {
		return SessionQuery.create(this, definedQuery);
	}

	Repository getRepositorys() {
		return repository;
	}

	void setAttribute(String key, Object value) {
		attributes.put(key, value) ;
	}
	
	public <T> T getAttribute(String key, Class<T> T) {
		Object value = attributes.get(key) ;
		if (T.isInstance(value)) return (T)value ;
		return null;
	}

	void setLastResult(NodeResult result) {
		setAttribute(NodeResult.class.getCanonicalName(), result) ;
	}
	
}
