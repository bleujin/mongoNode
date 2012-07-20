package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.CREATED;
import static net.ion.radon.repository.NodeConstants.OWNER;
import static net.ion.radon.repository.NodeConstants.TIMEZONE;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import net.ion.framework.util.ChainMap;

import org.bson.types.ObjectId;

public class UpdateChain {

	private NodeObject values ;
	
	private Session session;
	private String targetWorkspace;
	private PropertyQuery query;
	
	UpdateChain(Session session, String targetWorkspace, PropertyQuery query){
		this.session = session ;
		this.targetWorkspace = targetWorkspace ;
		this.query = query ;
		this.values = NodeObject.create();
	}
	
	public UpdateChain put(String key, Object val) {
		getInner("$set").put(key, val) ;
		
		return this;
	}

	
	public UpdateChain unset(String key) {
		getInner("$unset").put(key, 1) ;
		
		return this;
	}

	private UpdateChain put(PropertyId key, Object val) {
		getInner("$set").put(key, val) ;
		
		return this;
	}
	
	public String toString(){
		return String.format("session:%s, targetwname:%s, query:%s, inner:%s", session, targetWorkspace, query, getInner("$set")) ;
	}

	public UpdateChain inc(String key, int val) {
		getInner("$inc").put(key, val) ;
		return this;
	}

	public UpdateChain inlist(String key, ChainMap<String, ? extends Object> values) {
		return inlist(key, values.toMap());
	}
	
	public UpdateChain inlist(String key, Map<String, ? extends Object> map) {
		getInner("$pushAll").inlist(key).push(map) ;
		return this;
	}
	
	public UpdateChain push(String key, Object val) {
		getInner("$push").put(key, val) ;
		return this;
	}
	
	public NodeResult update() {
		put(NodeConstants.LASTMODIFIED, GregorianCalendar.getInstance().getTimeInMillis());
		Workspace workspace = session.getWorkspace(targetWorkspace);
		return workspace.updateInner(session, query, values.getDBObject(), false) ;
	}
	
	private InNode getInner(String cmd){
		return values.inner(cmd, session.getRoot()) ;
	}

	public UpdateChain removeInlist(String inlistKey, PropertyQuery inlistQuery) {
		getInner("$pull").inlist(inlistKey).pull(inlistQuery.toMap()) ;
		return this;
	}

	
	public NodeResult merge() {
		ObjectId oid = new ObjectId() ;
		if (!query.getDBObject().containsField(NodeConstants.ARADON_GROUP)){
			put(PropertyId.reserved(NodeConstants.ARADON_UID), MergeQuery.EMPTY_GROUP) ;
		} 
		if (!query.getDBObject().containsField(NodeConstants.ARADON_UID)){
			put(PropertyId.reserved(NodeConstants.ARADON_UID), oid.toString()) ;
		}
		if (!query.getDBObject().containsField(NodeConstants.PATH)){
			put(NodeConstants.PATH, "/" + oid) ;
		}

		if (query.getDBObject().get(CREATED) == null) {
			Calendar c = GregorianCalendar.getInstance();
			put(PropertyId.reserved(CREATED), c.getTimeInMillis());
			put(PropertyId.reserved(TIMEZONE), TimeZone.getDefault().toString());
		}

		if (query.getDBObject().get(OWNER) == null) {
			put(PropertyId.reserved(OWNER), "_unknown");
		}
		put(NodeConstants.LASTMODIFIED, GregorianCalendar.getInstance().getTimeInMillis());
		return session.getWorkspace(targetWorkspace).updateInner(session, query, values.getDBObject(), true) ;
	}


}


