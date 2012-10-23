package net.ion.radon.repository;

import java.util.Collection;

import net.ion.radon.repository.collection.CollectionFactory;

public interface Session {

	public Session changeWorkspace(String wname);

	public Session changeWorkspace(String wname, WorkspaceOption options);

	public ISequence getSequence(String prefix, String id);

	public int commit();

	public void dropWorkspace();

	public Node newNode();

	public Node newNode(String name);

	public String getCurrentWorkspaceName();

	public NodeResult remove(Node node);

	public Node createChild(Node parent, String name);

	public Collection<Node> getModified();

	public void clear();

	public void logout();

	public Workspace getCurrentWorkspace();

	public Node getRoot();

	public SessionQuery createQuery() ; 
	
	public <T> T getAttribute(String key, Class<T> T);

	public TempNode tempNode();
	
	public SessionQuery createQuery(PropertyQuery definedQuery) ;

	public NodeResult merge(String idOrPath, TempNode tnode)  ;
	
	public NodeResult merge(MergeQuery query, TempNode tnode) ;

	public Node mergeNode(MergeQuery mergeQuery, String... props) ;

	public Workspace getWorkspace(String wname) ;

	public Workspace getWorkspace(String outputCollection, WorkspaceOption none);

	public void setAttribute(String key, Object value);
	
	public String[] getWorkspaceNames() ;
	
	public void notify(Node target, NodeEvent event);

	public SessionQuery createQuery(String wname);
	// public NodeResult merge(AradonId aid, Node node);

	public SessionQuery createQuery(String wname, WorkspaceOption option);

	public CollectionFactory newCollectionFactory(String groupId);

}
