package net.ion.radon.repository;

import com.mongodb.DBRef;

public class Reference {

	private Repository repository ;
	private Node refNode ;
	private Reference(Repository repository, Node refNode) {
		this.repository = repository ;
		this.refNode = refNode ;
	}

	public static Reference create(Repository repository, Node refNode) {
		return new Reference(repository, refNode);
	}

	private ReferenceObject getTarget() {
		return new ReferenceObject((InNode)refNode.get(ReferenceManager.TARGET));
	}
	
	public Node getTargetNode(){
		ReferenceObject refObject = getTarget() ;
		DBRef target = new DBRef(repository.getDB(), refObject.getWorkspaceName(), refObject.getId());
		return NodeImpl.load(repository.getWorkspace(target.getRef()).getName(), NodeObject.load(target.fetch())) ;
	}
	
	private ReferenceObject getSource() {
		return new ReferenceObject((InNode)refNode.get(ReferenceManager.SRC));
	}
	
	public String getType(){
		return (String)refNode.get(ReferenceManager.TYPE);
	}
	
	public Node getSourceNode(){
		ReferenceObject refObject = getSource() ;
		DBRef source = new DBRef(repository.getDB(), refObject.getWorkspaceName(), refObject.getId());
		return NodeImpl.load(repository.getWorkspace(source.getRef()).getName(), NodeObject.load(source.fetch())) ;
	}

	public ReferenceNode getReferenceNode(){
		return ReferenceNode.create(refNode) ;
	}
	
	
}
