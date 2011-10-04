package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ID;

import java.util.Map;

import net.ion.framework.util.ChainMap;
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

	public InListNode push(ChainMap<String, ?> values) {
		return push(values.toMap()) ;
	}
	public InListNode push(Map<String, ?> values) {
		InNode inode = (InNode) parent.get(this.field);
		if (inode == null){
			inode = InNodeImpl.create(NodeObject.create(), field, parent) ;
		}
		
		DBObject dbo = inode.getDBObject() ;
		
		if (dbo instanceof BasicBSONList) {
			((BasicBSONList) dbo).add(NodeObject.load(values).getDBObject()) ;
			parent.put(this.field, dbo) ;
		} else if (dbo.keySet().size() == 0) {
			BasicDBList list = new BasicDBList() ;
			list.add(NodeObject.load(values).getDBObject());
			dbo = list ;
			parent.put(this.field, dbo);
		} else {
			throw new IllegalStateException("mismathc type : must be array type");
		}
		return this ;
	}

	public InListNode insertBefore(String target, Map<String, Object> values) {
		InNode inode = (InNode) parent.get(this.field);
		if (inode == null) {
			inode = InNodeImpl.create(NodeObject.create(), field, parent);
		}
		DBObject dbo = inode.getDBObject();
		
		if (dbo instanceof BasicBSONList) {
			int idx = 0;
			BasicDBList dblist = (BasicDBList) dbo;
			for (int i = 0; i < dblist.size(); i++) {
				DBObject obj = (DBObject) dblist.get(i);
				if (target.equals(obj.get(ID))) {
					idx = i;
					break;
				}
			}
			dblist.add(idx, NodeObject.load(values).getDBObject()) ;
			dbo = dblist;
			parent.put(this.field, dbo) ;
		} else if (dbo.keySet().size() == 0) {
			BasicDBList list = new BasicDBList() ;
			list.add(NodeObject.load(values).getDBObject());
			dbo = list ;
			parent.put(this.field, dbo);
		} else {
			throw new IllegalStateException("mismathc type : must be array type");
		}
		return this ;
	}

	public InListNode insertAfter(String target, Map<String, Object> values) {
		InNode inode = (InNode) parent.get(this.field);
		if (inode == null) {
			inode = InNodeImpl.create(NodeObject.create(), field, parent);
		}
		DBObject dbo = inode.getDBObject();
		
		if (dbo instanceof BasicBSONList) {
			int idx = 0;
			BasicDBList dblist = (BasicDBList) dbo;
			for (int i = 0; i < dblist.size(); i++) {
				DBObject obj = (DBObject) dblist.get(i);
				if (target.equals(obj.get(ID))) {
					idx = i + 1;
					break;
				}
			}
			dblist.add(idx, NodeObject.load(values).getDBObject()) ;
			dbo = dblist;
			parent.put(this.field, dbo) ;
		} else if (dbo.keySet().size() == 0) {
			BasicDBList list = new BasicDBList() ;
			list.add(NodeObject.load(values).getDBObject());
			dbo = list ;
			parent.put(this.field, dbo);
		} else {
			throw new IllegalStateException("mismathc type : must be array type");
		}
		return this ;
	}
	
	public InListNode insertFirst(Map<String, Object> values) {
		InNode inode = (InNode) parent.get(this.field);
		if (inode == null) {
			inode = InNodeImpl.create(NodeObject.create(), field, parent);
		}
		DBObject dbo = inode.getDBObject();
		
		if (dbo instanceof BasicBSONList) {
			((BasicDBList) dbo).add(0, NodeObject.load(values).getDBObject()) ;
			parent.put(this.field, dbo) ;
		} else if (dbo.keySet().size() == 0) {
			BasicDBList list = new BasicDBList() ;
			list.add(NodeObject.load(values).getDBObject());
			dbo = list ;
			parent.put(this.field, dbo);
		} else {
			throw new IllegalStateException("mismathc type : must be array type");
		}
		return this ;
	}
	
	public InListNode insertLast(Map<String, Object> values) {
		return this.push(values) ;
	}

	public InListNode update(String id, Map<String, Object> values) {
		InNode inode = (InNode) parent.get(this.field);
		if (inode == null) {
			inode = InNodeImpl.create(NodeObject.create(), field, parent);
		}
		DBObject dbo = inode.getDBObject();
		
		if (dbo instanceof BasicBSONList) {
			BasicDBList dblist = (BasicDBList) dbo;
			DBObject obj = null;
			int idx = 0;
			for (int i = 0; i < dblist.size(); i++) {
				obj = (DBObject) dblist.get(i);
				if (id.equals(obj.get(ID))) {
					idx = i;
					obj.putAll(values);
					break;
				}
			}
			dblist.set(idx, obj) ;
			dbo = dblist;
			parent.put(this.field, dbo) ;
		} else {
			throw new IllegalStateException("mismathc type : must be array type");
		}
		return this;
	}
}
