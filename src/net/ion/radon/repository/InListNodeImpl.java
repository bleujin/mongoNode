package net.ion.radon.repository;

import java.util.Map;

import net.ion.framework.util.ObjectUtil;

import org.bson.types.BasicBSONList;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class InListNodeImpl implements InListNode{
	
	private NodeObject listquery = NodeObject.create() ;
	
	private String field ;
	private Session session ;
	private PropertyQuery outquery ;
	private NodeImpl parent ;
	
	private InListNodeImpl(String field, Session session, PropertyQuery outquery) {
		this.field = field ;
		this.session = session ;
		this.outquery = outquery ;
	}

	static InListNode load(String field, Session session, SessionQuery squery, NodeImpl parent) {
		InListNodeImpl result = new InListNodeImpl(field, session, squery.getQuery());
		result.parent = parent ;
		return result;
	}

	public NodeResult pull() {
		BasicDBObject dbo = new BasicDBObject() ;
		dbo.put(field, this.listquery.toMap()) ;
		
		return session.getCurrentWorkspace().pull(outquery, dbo) ;
	}

	public InListQuery createQuery() {
		Object inobj = parent.get(this.field);
		InNode inode = (InNode) ObjectUtil.coalesce(inobj, InNodeImpl.create(NodeObject.BLANK_INNODE, field, parent)) ;
		
		return InListQuery.create(NodeObject.load((inode).getDBObject()), this.field, parent) ;
		// return ((InNodeImpl)parent.get(this.field)).inListQuery() ;
	}

	public void push(Map<String, ?> values) {
		InNode inode = (InNode) parent.get(this.field);
		if (inode == null){
			inode = InNodeImpl.create(NodeObject.create(), field, parent) ;
		}
		
		DBObject dbo = inode.getDBObject() ;
		
		if (dbo instanceof BasicBSONList) {
			((BasicBSONList) dbo).add(new BasicDBObject(values)) ;
			parent.put(this.field, dbo) ;
		} else if (dbo.keySet().size() == 0) {
			BasicDBList list = new BasicDBList() ;
			list.add(new BasicDBObject(values));
			dbo = list ;
			parent.put(this.field, dbo);
		} else {
			throw new IllegalStateException("mismathc type : must be array type");
		}
		
		// parent.inner(this.field).putAll(values) ;
//		parent.append(this.field, new BasicDBObject(values)) ;
	}


}
