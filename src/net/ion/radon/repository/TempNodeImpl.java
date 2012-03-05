package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.CREATED;
import static net.ion.radon.repository.NodeConstants.ID;
import static net.ion.radon.repository.NodeConstants.OWNER;
import static net.ion.radon.repository.NodeConstants.TIMEZONE;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.db.procedure.IStringObject;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ChainMap;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.innode.TempInNode;
import net.ion.radon.repository.util.CipherUtil;
import net.ion.radon.repository.util.MyNumberUtil;

import com.mongodb.DBObject;

public class TempNodeImpl implements TempNode {

	private static final long serialVersionUID = -9085850570829905483L;
	private final transient Session session;
	private NodeObject nobject;

	private TempNodeImpl(Session session, NodeObject no) {
		this.session = session;
		this.setNodeMetaInfo(no);
	}

	static TempNodeImpl create(Session session, NodeObject no) {
		final TempNodeImpl result = new TempNodeImpl(session, no);

		return result;
	}

	public Session getSession() {
		return session;
	}

	// @TODO if prop has '.'
	public Serializable get(String propId) {
		return nobject.get(propId, this);
	}

	public TempInNode inner(String name) {
		return (TempInNode) nobject.inner(name, this);
	}

	public InListNode inlist(String name) {
		return nobject.inlist(name, this);
	}

	public Serializable get(String propId, int index) {
		return nobject.get(propId, index, this);
	}

	public int getAsInt(String propId) {
		return MyNumberUtil.getAsInt(get(propId));
	}

	public DBObject getDBObject() {
		return nobject.getDBObject();
	}

	public synchronized TempNode putEncrypt(String key, String value) throws RepositoryException {
		return put(key, CipherUtil.recursiveEncrypt(value));
	}

	public boolean isMatchEncrypted(String key, String value) {
		byte[] encrypted = (byte[]) get(key);
		return CipherUtil.isMatch(value, encrypted);
	}

	public void putAll(Map<String, ? extends Object> props) {
		for (Entry<String, ?> prop : props.entrySet()) {
			this.put(prop.getKey(), prop.getValue());
		}
	}

	public void putAll(ChainMap<String, ? extends Object> props) {
		putAll(props.toMap());
	}

	public synchronized TempNode put(String key, Object val) {
		return putProperty(PropertyId.create(key), (val != null && val instanceof IStringObject) ? ((IStringObject) val).getString() : val);
	}

	public TempNode putProperty(PropertyId propId, Object val) {
		nobject.putProperty(propId, val);
		return this;
	}

	public TempNode append(String key, Object val) {
		nobject.appendProperty(PropertyId.create(key), val);
		return this;
	}

	private void setNodeMetaInfo(NodeObject _dbo) {

		this.nobject = _dbo;

		// ObjectId newId = new ObjectId() ;
		// if (_dbo.get(ID) == null || (!(_dbo.get(ID) instanceof ObjectId))) {
		// putProperty(PropertyId.reserved(ID), newId);
		// }
		//
		if (_dbo.get(CREATED) == null) {
			setCreated(GregorianCalendar.getInstance());
		}

		if (_dbo.get(OWNER) == null) {
			setOwner("_unknown");
		}
	}

	private void setCreated(Calendar c) {
		putProperty(PropertyId.reserved(CREATED), c.getTimeInMillis());
		putProperty(PropertyId.reserved(TIMEZONE), TimeZone.getDefault().toString());
	}

	private void setOwner(String owner) {
		putProperty(PropertyId.reserved(OWNER), owner);
	}

	public Map toMap() {
		return nobject.toMap(this);
	}

	public Map<String, ? extends Object> toPropertyMap() {
		return nobject.toPropertyMap(this);
	}

	public Map<String, ? extends Object> toPropertyMap(NodeColumns cols) {
		return nobject.toPropertyMap(cols, this);
	}

	public String toString() {
		return toPropertyMap().toString();
	}

	public Object getId() {
		return getDBObject().get(ID);
	}

	public String getString(String key) {
		return StringUtil.toString(get(key));
	}

	public void clearProp() {
		Map<String, ?> prop = toPropertyMap();
		for (String key : prop.keySet()) {
			if (NodeUtil.isReservedProperty(key))
				continue;
			getDBObject().removeField(key);
		}
	}

	public boolean hasProperty(String key) {
		return nobject.containsField(key);
	}

	public void notify(NodeEvent nevent) {
		; // ignore
	}

	public String toJsonString() {
		return toPropertyMap().toString();
	}

}
