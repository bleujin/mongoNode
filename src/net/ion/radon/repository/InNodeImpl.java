package net.ion.radon.repository;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.BasicBSONList;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.util.CipherUtil;
import net.ion.radon.repository.util.JSONUtil;
import net.sf.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class InNodeImpl implements InNode {

	private static final long serialVersionUID = 7569283280889592633L;
	private NodeObject nobject;
	private String pname;
	private Node parent;

	private InNodeImpl(NodeObject nobject, String pname, Node parent) {
		this.nobject = nobject;
		this.pname = pname;
		this.parent = parent;
	}

	static InNode create(NodeObject inner, String pname, Node parent) {
		return new InNodeImpl(inner, pname, parent);
	}

	public InNode inner(String name) {
		if (hasProperty(name) && get(name) instanceof InNode) {
			return (InNode) get(name);
		}
		NodeObject inner = NodeObject.create();
		put(name, inner);
		return InNodeImpl.create(inner, name, parent);
	}

	public InNode append(String key, Object val) {
		nobject.appendProperty(PropertyId.create(key), val);
		parent.getSession().notify(parent, NodeEvent.UPDATE);
		return this;
	}

	public InNode putEncrypt(String key, String value) throws RepositoryException {
		return put(key, CipherUtil.recursiveEncrypt(value));
	}

	public InNode put(String key, Object val) {
		nobject.putProperty(PropertyId.create(key), val);
		notify(NodeEvent.UPDATE);
		return this;
	}

	private void notify(NodeEvent event) {
		parent.getSession().notify(parent, event);
	}

	public void putAll(Map<String, ? extends Object> props) {
		for (Entry<String, ?> prop : props.entrySet()) {
			this.put(prop.getKey(), prop.getValue());
		}
	}

	public void putAll(IPropertyFamily props) {
		putAll(props.getDBObject().toMap());
	}

	public void clearProp() {
		Map<String, ? extends Object> prop = toMap();
		for (String key : prop.keySet()) {
			getDBObject().removeField(key);
		}

		notify(NodeEvent.UPDATE);
	}

	public Serializable get(String propId) {
		return (Serializable) nobject.get(propId);
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
		final String str = getString(propId);
		return NumberUtil.isNumber(str) ? Integer.parseInt(str) : 0;
	}

	public DBObject getDBObject() {
		return nobject.getDBObject();
	}

	public Node getParent() {
		return parent;
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
		for (Entry<String, ? extends Object> entry : toMap().entrySet()) {
			result.put(entry.getKey(), entry.getValue());
		}
		return Collections.unmodifiableMap(result);
	}

	public String toString() {
		return toMap().toString();
	}

	// public String getPath() {
	// throw new UnsupportedOperationException() ;
	// }

	public InNode append(JSONObject json) {
		DBObject dbo = nobject.getDBObject();
		if (dbo instanceof BasicBSONList) {
			((BasicBSONList) dbo).add(NodeObject.load(JSONUtil.toDBObject(json)).getDBObject()) ;
		} else if (dbo.keySet().size() == 0) {
			BasicDBList list = new BasicDBList() ;
			list.add(NodeObject.load(JSONUtil.toDBObject(json)).getDBObject());
			this.nobject = NodeObject.load(list) ;
			parent.put(this.pname, this.nobject);
		} else {
			throw new IllegalStateException("mismathc type : must be array type");
		}
		notify(NodeEvent.UPDATE);
		return this;
	}
	
	public InQuery createQuery() {
		return InQuery.create(nobject, pname, parent);
	}
}
