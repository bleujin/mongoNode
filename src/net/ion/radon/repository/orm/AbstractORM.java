package net.ion.radon.repository.orm;

import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeObject;

public abstract class AbstractORM implements ORMObject {

	private static final long serialVersionUID = 6966530917552174294L;
	private NodeObject no = NodeObject.create() ;
	
	public AbstractORM(){} ;
	
	public void put(String key, Object value){
		no.put(key, value) ;
	}
	
	public Object get(String key) {
		return no.get(key) ;
	}

	protected int getAsInt(String key) {
		return NumberUtil.toInt(no.getString(key), 0) ;
	}


	
	public String getString(String key) {
		return ObjectUtil.toString(no.get(key)) ;
	}

	public NodeObject getNodeObject(){
		return no ;
	}

	public AbstractORM load(Node node) {
		if (node == null) return null ;
		
		this.no = NodeObject.load(node.getDBObject()) ;
		return this;
	}
	
}