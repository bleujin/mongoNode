package net.ion.radon.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;

public class LocalSession implements Session {

	private Repository repository;
	private String currentWsName;
	private WorkspaceOption option = WorkspaceOption.EMPTY ;
	private final RootNode root;
	private Map<String, Node> modified = MapUtil.newMap();
	private Map<String, Object> attributes = MapUtil.newCaseInsensitiveMap();

	private LocalSession(Repository repository, String wname) {
		this.repository = repository;
		this.root = new RootNode(this);
		this.currentWsName = wname;
	}

//	private static ThreadLocal<Session> CURRENT = new ThreadLocal<Session>();
//
//	private static ThreadLocal<Session> getThreadLocal() {
//		return CURRENT;
//	}

	static synchronized Session create(Repository repository, String wname) {
		return new LocalSession(repository, wname);
	}

	public Session changeWorkspace(String wname) {
		this.currentWsName = wname ;
		return this;
	}
	
	public Session changeWorkspace(String wname, WorkspaceOption option){
		this.currentWsName = wname ;
		this.option = option ;
		return this;
	}


	
	public ISequence getSequence(String prefix, String id) {
		return Sequence.createOrLoad(this, prefix, id);
	}

	public int commit() {
		int count = getCurrentWorkspace().mergeNodes(this, modified) ;
		this.clear();
		return count;
	}

	public NodeResult getLastResultInfo() {
		return getAttribute(NodeResult.class.getCanonicalName(), NodeResult.class);
	}

	public void notify(Node target, NodeEvent event) {
		modified.put(target.getIdentifier(), target);
	}

	public void dropWorkspace() {
		getCurrentWorkspace().drop();
	}

	public Node newNode() {
		return getCurrentWorkspace().newNode(this);
	}

	public TempNode tempNode() {
		return TempNodeImpl.create(this, NodeObject.create());
	}

	public Node mergeNode(MergeQuery mergeQuery, String... props) {

		Node found = SessionQuery.create(this, mergeQuery.getQuery()).findOne(Columns.append().add(Columns.MetaColumns).add(props));
		if (found == null) {
			Node newNode = newNode();
			Map<String, ? extends Object> queryMap = mergeQuery.data();
			for (Entry<String, ? extends Object> entry : queryMap.entrySet()) {
				newNode.getDBObject().put(entry.getKey().toLowerCase(), entry.getValue());
			}
			return newNode;
		} else {

			NodeObject metaInfo = NodeObject.create();
			for (String metakey : Columns.MetaColumns) {
				metaInfo.putProperty(PropertyId.reserved(metakey), (Object) found.get(metakey));
			}
			for (String pkey : props) { // set
				metaInfo.putProperty(PropertyId.create(pkey), (Object) found.get(pkey));
			}
			return NodeImpl.load(this, PropertyQuery.load(mergeQuery), getCurrentWorkspaceName(), metaInfo);
		}
	}

	public Node newNode(String name) {
		return getCurrentWorkspace().newNode(this, name);
	}

	public String getCurrentWorkspaceName() {
		return getCurrentWorkspace().getName();
	}

	public NodeResult remove(Node node) {
		if (node == getRoot())
			return NodeResult.NULL;
		return getWorkspace(node.getWorkspaceName()).remove(this, PropertyQuery.createByAradon(node.getAradonId().getGroup(), node.getAradonId().getUid()));
	}

	public Node createChild(Node parent, String name) {
		final NodeImpl newNode = NodeImpl.create(this, getCurrentWorkspaceName(), NodeObject.create(), parent.getPath(), name);
		newNode.toRelation(NodeConstants.PARENT, parent.selfRef());

		return newNode;
	}

	public Collection<Node> getModified() {
		return modified.values();
	}

	public void clear() {
		modified.clear();
	}

	public void logout() {
		modified.clear() ;
	}

	public Workspace getCurrentWorkspace() {
		return repository.getWorkspace(currentWsName, option);
	}

	public Node getRoot() {
		return root;
	}

	public SessionQuery createQuery(PropertyQuery definedQuery) {
		return SessionQuery.create(this, definedQuery);
	}
	
	public SessionQuery createQuery(String wname) {
		return SessionQuery.create(this, wname);
	}
	
	public SessionQuery createQuery(String wname, WorkspaceOption option) {
		return SessionQuery.create(this, wname, option);
	}
	


	public NodeResult merge(String idOrPath, TempNode tnode) {
		if (idOrPath == null) throw new IllegalArgumentException("query must be path or id") ;
		return idOrPath.startsWith("/") ? merge(MergeQuery.createByPath(idOrPath), tnode) : merge(MergeQuery.createById(idOrPath), tnode) ;
	}
	
	public NodeResult merge(MergeQuery query, TempNode tnode) {
		return getCurrentWorkspace().merge(this, query, tnode);
	}
	
	public SessionQuery createQuery() {
		return SessionQuery.create(this);
	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	public <T> T getAttribute(String key, Class<T> T) {
		Object value = attributes.get(key);
		if (T.isInstance(value))
			return (T) value;
		return null;
	}

	void setLastResult(NodeResult result) {
		setAttribute(NodeResult.class.getCanonicalName(), result);
	}

	public Workspace getWorkspace(String wname) {
		return repository.getWorkspace(wname, option);
	}

	public Workspace getWorkspace(String wname, WorkspaceOption myoptions) {
		return repository.getWorkspace(wname, myoptions);
	}

	public String[] getWorkspaceNames() {
		return repository.getWorkspaceNames().toArray(new String[0]) ;
	}
	
	
}
