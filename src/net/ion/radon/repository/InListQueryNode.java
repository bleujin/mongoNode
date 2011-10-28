package net.ion.radon.repository;

import java.util.Map;

import net.ion.framework.util.ChainMap;
import net.ion.radon.repository.innode.InListFilterQuery;

import com.mongodb.BasicDBObject;

public class InListQueryNode {

	// private NodeObject listquery = NodeObject.create() ;
	
	private String field ;
	private Session session ;
	private SessionQuery squery ;
	
	private InListQueryNode(String field, Session session, SessionQuery squery) {
		this.field = field.toLowerCase() ;
		this.session = session ;
		this.squery = squery ;
	}

	static InListQueryNode load(String field, Session session, SessionQuery squery, NodeImpl parent) {
		InListQueryNode result = new InListQueryNode(field, session, squery);
		return result;
	}
	
	static InListQueryNode create(String field, Session session, SessionQuery squery) {
		return new InListQueryNode(field, session, squery);
	}

	public NodeResult pull() {
		return pull(PropertyQuery.EMPTY) ;
	}

	public NodeResult pull(IPropertyFamily query) {
		BasicDBObject dbo = new BasicDBObject() ;
		dbo.put(field, query.getDBObject()) ;
		
		return session.getCurrentWorkspace().pull(squery.getQuery(), dbo) ;
	}

	public NodeResult push(ChainMap values) {
		return push(values.toMap()) ;
	}
	
	public NodeResult push(Map<String, ?> values) {
		BasicDBObject dbo = new BasicDBObject() ;
		dbo.put(field, NodeObject.load(values).getDBObject()) ;
		
		return session.getCurrentWorkspace().push(squery.getQuery(), dbo) ;
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
		return squery.eleMatch(field, eleQuery).find() ; 
	}

	public InListFilterQuery filter(String filterFn) {
		return InListFilterQuery.create(this.field, this.squery, this.session, filterFn);
	}

}
