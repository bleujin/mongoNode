package net.ion.radon.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.AradonQuery;
import net.sf.json.JSONObject;

public class Session {

	private Repository repository;
	private Workspace workspace;
	private static ThreadLocal<Session> CURRENT = new ThreadLocal<Session>();

	private Map<String, Node> modified = MapUtil.newMap();
	private final RootNode root;
	private Map<String, Object> attributes = MapUtil.newCaseInsensitiveMap() ;
	
	private Session(Repository repository, String wname) {
		this.repository = repository;
		this.workspace = repository.getWorkspace(wname);
		this.root = new RootNode(this) ; 
	}

	static synchronized Session create(Repository repository, String wname) {
		Session session = CURRENT.get();
		if (session == null) {
			session = new Session(repository, wname);
			CURRENT.set(session);
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
		Session session = CURRENT.get();
		
		if (session == null){
			throw new IllegalStateException("not logined");
		}

		return session;
	}

	public int commit() {
		int index = modified.size();
		
		Node[] targets = modified.values().toArray(new Node[0]);
		for (Node node : targets) {
			NodeResult nodeResult = repository.getWorkspace(node.getWorkspaceName()).save(node);
			setAttribute(NodeResult.class.getCanonicalName(), nodeResult) ;
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

	public Node newNode(String name) {
		return workspace.newNode(name);
	}

	public Node newNode(PropertyFamily props) {
		Node node = newNode() ;
		node.putAll(props) ;
		return node;
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
		CURRENT.remove() ;
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

	
	public List<Node> group(PropertyQuery keys, PropertyQuery conds, PropertyQuery initial, String reduce) {
		return workspace.group(keys, conds, initial, reduce);
	}

	public Node mergePath(String path, JSONObject jso) {
		return workspace.merge(path, jso) ;
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
