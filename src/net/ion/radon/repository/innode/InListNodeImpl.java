package net.ion.radon.repository.innode;

import java.util.Collections;
import java.util.Map;

import net.ion.framework.util.ChainMap;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.repository.INode;
import net.ion.radon.repository.InListNode;
import net.ion.radon.repository.InListQuery;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeObject;

import org.bson.types.BasicBSONList;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;


public class InListNodeImpl implements InListNode{
	
	private static final long serialVersionUID = 8183954909102079835L;
	private BasicDBList list ;
	private String field ;
	private INode parent ;
	
	private InListNodeImpl(BasicDBList list, String field, INode parent) {
		this.list = (list == null) ? new BasicDBList() : list ;
		this.field = field ;
		this.parent = parent ;
	}

	public static InListNode load(BasicDBList list, String field, INode parent) {
		return new InListNodeImpl(list, field, parent);
	}

	public InListQuery createQuery() {
		return InListQuery.create(NodeObject.load(list), this.field, parent) ;
		// return ((InNodeImpl)parent.get(this.field)).inListQuery() ;
	}

	public InListNode push(ChainMap values) {
		return push(values.toMap()) ;
	}
	
	public InListNode push(Map<String, ? extends Object> values) {
		if ((parent instanceof Node) && ! ((Node)parent).isNew()) {
			throw new IllegalStateException("only use on new state");
		}

		DBObject dbo = list;
		
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

	public String toString(){
		return list.toString() ;
	}

	
	public Object get(int index){
		Object result = list.get(index);
		if (result instanceof DBObject){
			return InNodeImpl.create((DBObject)result, String.valueOf(index), parent) ;
		}
		return result ;
	}
	
	public Map<String, ? extends Object> toMap() {
		return Collections.unmodifiableMap(NodeObject.load(list).toMap());
	}

	public DBObject getDBObject() {
		return list;
	}

}
