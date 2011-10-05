package net.ion.radon.repository;

import java.util.Map;

import net.ion.framework.util.ChainMap;

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

	public NodeResult pull(IPropertyFamily query) {
		BasicDBObject dbo = new BasicDBObject() ;
		dbo.put(field, query.getDBObject()) ;
		
		return session.getCurrentWorkspace().pull(outquery, dbo) ;
	}

	public NodeResult push(ChainMap values) {
		return push(values.toMap()) ;
	}
	
	public NodeResult push(Map<String, ?> values) {
		BasicDBObject dbo = new BasicDBObject() ;
		dbo.put(field, NodeObject.load(values).getDBObject()) ;
		
		return session.getCurrentWorkspace().push(outquery, dbo) ;
	}

	public NodeResult update(IPropertyFamily query, ChainMap cmap) {
		return update(query, cmap.toMap()) ;
	}
	public NodeResult update(IPropertyFamily query, Map<String, Object> map) {
		NodeResult result = this.pull(query);
		if (result.getRowCount() == 0) {
			return NodeResult.NULL;
		}
		return this.push(map);
	}

	public NodeCursor findElement(PropertyQuery eleQuery) {
		return session.createQuery(outquery).eleMatch(field, eleQuery).find() ; 
	}

}
