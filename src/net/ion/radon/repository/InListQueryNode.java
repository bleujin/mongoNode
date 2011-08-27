package net.ion.radon.repository;

import java.util.Map;

import com.mongodb.BasicDBObject;

public class InListQueryNode {

	// private NodeObject listquery = NodeObject.create() ;
	
	private String field ;
	private Session session ;
	private PropertyQuery outquery ;
	
	private InListQueryNode(String field, Session session, PropertyQuery outquery) {
		this.field = field.toLowerCase() ;
		this.session = session ;
		this.outquery = outquery ;
	}

	static InListQueryNode load(String field, Session session, SessionQuery squery, NodeImpl parent) {
		InListQueryNode result = new InListQueryNode(field, session, squery.getQuery());
		return result;
	}
	
	static InListQueryNode create(String field, Session session, PropertyQuery outquery) {
		return new InListQueryNode(field, session, outquery);
	}

	public NodeResult pull() {
		return pull(PropertyQuery.EMPTY) ;
	}

	public NodeResult pull(PropertyQuery query) {
		BasicDBObject dbo = new BasicDBObject() ;
		dbo.put(field, query.getDBObject()) ;
		
		return session.getCurrentWorkspace().pull(outquery, dbo) ;
	}

	public NodeResult push(Map<String, ?> values) {
		BasicDBObject dbo = new BasicDBObject() ;
		dbo.put(field, NodeObject.load(values).getDBObject()) ;
		
		return session.getCurrentWorkspace().push(outquery, dbo) ;
	}
}
