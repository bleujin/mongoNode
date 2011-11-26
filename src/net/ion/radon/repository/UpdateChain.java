package net.ion.radon.repository;

import net.ion.framework.util.ChainMap;
import net.ion.framework.util.Debug;
import net.ion.radon.repository.innode.InListNodeImpl;

public class UpdateChain {

	private NodeObject no ;
	
	private Session session;
	private String targetWName;
	private PropertyQuery query;
	
	UpdateChain(Session session, String targetWName, PropertyQuery query){
		this.session = session ;
		this.targetWName = targetWName ;
		this.query = query ;
		this.no = NodeObject.create();
	}
	
	public UpdateChain put(String key, Object val) {
		getInner("$set").put(key, val) ;
		
		return this;
	}
	
	public UpdateChain inc(String key, int val) {
		getInner("$inc").put(key, val) ;
		return this;
	}

	public UpdateChain inlist(String key, ChainMap<String, Object> values) {
		InListNode inlist = getInner("$pushAll").inlist(key);
		inlist.push(values.toMap()) ;
		return this;
	}
	
	public UpdateChain push(String key, Object val) {
		getInner("$push").put(key, val) ;
		return this;
	}
	
	public NodeResult update() {
		return session.getWorkspace(targetWName).updateInner(session, query, no.getDBObject(), false) ;
	}
	
	private InNode getInner(String cmd){
		return no.inner(cmd, session.getRoot()) ;
	}

	public UpdateChain removeInlist(String key, PropertyQuery query) {
		// @Todo : add interface ?
		((InListNodeImpl)getInner("$pull").inlist(key)).pull(query.toMap()) ;
		return this;
	}

	public NodeResult merge() {
		return session.getWorkspace(targetWName).updateInner(session, query, no.getDBObject(), true) ;
	}

}


