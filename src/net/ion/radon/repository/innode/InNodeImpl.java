package net.ion.radon.repository.innode;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.ChainMap;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.INode;
import net.ion.radon.repository.InListQuery;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeEvent;
import net.ion.radon.repository.NodeObject;
import net.ion.radon.repository.util.CipherUtil;
import net.ion.radon.repository.util.MyNumberUtil;

import com.mongodb.DBObject;

public abstract class InNodeImpl implements InNode {

	private static final long serialVersionUID = 7569283280889592633L;
	private NodeObject nobject;
	private String pname;
	private INode parent;

	protected InNodeImpl(DBObject dbo, String pname, INode parent) {
		this.nobject = (dbo == null) ? NodeObject.create() : NodeObject.load(dbo);
		this.pname = pname;
		this.parent = parent;
	}

	public static InNode create(DBObject inner, String pname, INode parent) {
		return (parent instanceof Node) ? new NormalInNodeImpl(inner, pname, parent) : new TempInNodeImpl(inner, pname, parent);
	}

	public InNode inner(String name) {
		if (hasProperty(name) && get(name) instanceof InNode) {
			return (InNode) get(name);
		}
		NodeObject inner = NodeObject.create();
		put(name, inner);
		return InNodeImpl.create(inner.getDBObject(), name, parent);
	}

	public InNode append(String key, Object val) {
		nobject.appendProperty(NodeObject.createPropId(key), val);
		parent.notify(NodeEvent.UPDATE);
		return this;
	}

	public InNode putEncrypt(String key, String value) throws RepositoryException {
		return put(key, CipherUtil.recursiveEncrypt(value));
	}

	public InNode put(String key, Object val) {
		nobject.putProperty(NodeObject.createPropId(key), val);
		notify(NodeEvent.UPDATE);
		return this;
	}

	public void notify(NodeEvent event) {
		parent.notify(event);
	}

	public void putAll(Map<String, ? extends Object> props) {
		for (Entry<String, ?> prop : props.entrySet()) {
			this.put(prop.getKey(), prop.getValue());
		}
	}

	public void putAll(ChainMap<String, ? extends Object> chainMap) {
		putAll(chainMap.toMap());
	}

	public void clearProp() {
		Map<String, ? extends Object> prop = toMap();
		for (String key : prop.keySet()) {
			getDBObject().removeField(key);
		}

		notify(NodeEvent.UPDATE);
	}

	public Serializable get(String propId) {
		return (Serializable) nobject.get(propId.toLowerCase());
	}

	public Serializable get(String propId, int index) {
		Object result = nobject.get(propId);
		if (result instanceof List) {
			return (Serializable) ((List) result).get(index);
		} else if (index == 0) {
			return (Serializable) result;
		} else {
			throw new IllegalArgumentException("element is not array");
		}
	}

	public int getAsInt(String propId) {
		return MyNumberUtil.getAsInt(get(propId)) ;
	}

	public DBObject getDBObject() {
		return nobject.getDBObject();
	}

	public String getString(String key) {
		return StringUtil.toString(get(key));
	}

	public boolean hasProperty(String key) {
		return nobject.containsField(key);
	}

	public boolean isMatchEncrypted(String key, String value) {
		byte[] encrypted = (byte[]) get(key);
		return CipherUtil.isMatch(value, encrypted);
	}

	public Map<String, ? extends Object> toMap() {
		return Collections.unmodifiableMap(nobject.toMap());
	}

	public Map<String, ? extends Object> toPropertyMap() {
		return toMap();
	}

	public Map<String, ? extends Object> toPropertyMap(NodeColumns cols) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (String key : nobject.toMap().keySet()) {
			result.put(key, get(key));
		}
		return Collections.unmodifiableMap(result);
	}

	public String toString() {
		return toMap().toString();
	}

	public InListQuery inListQuery() {
		return InListQuery.create(nobject, pname, parent);
	}
	
	public INode getParent(){
		return parent ;
	}
	
	public int hashCode(){
		return nobject.getDBObject().hashCode() ;
	}
	
	public boolean equals(Object _that){
		if (_that instanceof InNodeImpl){
			InNodeImpl that = (InNodeImpl)_that ;
			return nobject.getDBObject().equals(that.getDBObject()) ;
		} else return false ;
	}
}









