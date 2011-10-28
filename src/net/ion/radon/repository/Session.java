package net.ion.radon.repository;

import java.util.Collection;
import java.util.List;

import net.ion.radon.repository.myapi.AradonQuery;
import net.sf.json.JSONObject;

public abstract class Session {

	private static ThreadLocal<Session> CURRENT = new ThreadLocal<Session>();

	public abstract Session changeWorkspace(String wname);

	public abstract ReferenceManager getReferenceManager();

	public abstract ISequence getSequence(String prefix, String id);

	public static Session getCurrent() {
		Session session = CURRENT.get();

		if (session == null) {
			throw new IllegalStateException("not logined");
		}

		return session;
	}

	protected static ThreadLocal<Session> getThreadLocal() {
		return CURRENT;
	}

	public abstract int commit();

	public abstract NodeResult getLastResultInfo();

	abstract void notify(Node target, NodeEvent event);

	public abstract void dropWorkspace();

	public abstract Node newNode();

	public abstract Node newNode(String name);

	abstract List<Node> findAllWorkspace(AradonQuery query);

	public abstract String getCurrentWorkspaceName();

	public abstract NodeResult remove(Node node);

	abstract Node createChild(Node parent, String name);

	public abstract Collection<Node> getModified();

	public abstract void clear();

	public abstract void logout();

	public abstract Workspace getCurrentWorkspace();

	public abstract Node getRoot();

	public abstract ReferenceQuery createRefQuery();

	public Node addReference(Node src, String relType, Node target) {
		return getReferenceManager().addReference(src, relType, target);
	}

	public SessionQuery createQuery() {
		return SessionQuery.create(this);
	}

	SessionQuery createQuery(PropertyQuery definedQuery) {
		return SessionQuery.create(this, definedQuery);
	}

	abstract Repository getRepositorys();

	abstract void setAttribute(String key, Object value);

	public abstract <T> T getAttribute(String key, Class<T> T);

	void setLastResult(NodeResult result) {
		setAttribute(NodeResult.class.getCanonicalName(), result);
	}

	abstract void dropDB();

	public abstract TempNode tempNode();

	public NodeResult merge(String idOrPath, TempNode tnode) {
		if (idOrPath == null) throw new IllegalArgumentException("query must be path or id") ;
		return idOrPath.startsWith("/") ? merge(MergeQuery.createByPath(idOrPath), tnode) : merge(MergeQuery.createById(idOrPath), tnode) ;
	}
	
	public NodeResult merge(MergeQuery query, TempNode tnode) {
		return getCurrentWorkspace().merge(query, tnode);
	}

	public abstract Node mergeNode(MergeQuery mergeQuery) ;

}
