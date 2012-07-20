package net.ion.radon.repository;

import net.ion.framework.util.ObjectId;

public class WorkspaceQueue {
	
	private Session session ;
	private WorkspaceQueue(Session session) {
		this.session = session ;
	}
	
	public TempNode createElement(){
		return session.tempNode() ;
	}

	public NodeResult offer(TempNode tnode){
		return session.merge(MergeQuery.createByAradon("queue", new ObjectId().toString()), tnode) ;
	}

	static WorkspaceQueue create(Session session) {
		return new WorkspaceQueue(session);
	}

	
	// Retrieves and removes the head of this queue, or null if this queue is empty.
	public Node poll() {
		return poll(PropertyQuery.create()) ;
	}

	public Node poll(PropertyQuery query) {
		return session.getCurrentWorkspace().poll(session, query) ;
	}
}
