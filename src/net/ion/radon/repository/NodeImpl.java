package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ARADON;
import static net.ion.radon.repository.NodeConstants.CREATED;
import static net.ion.radon.repository.NodeConstants.GHASH;
import static net.ion.radon.repository.NodeConstants.GROUP;
import static net.ion.radon.repository.NodeConstants.ID;
import static net.ion.radon.repository.NodeConstants.LASTMODIFIED;
import static net.ion.radon.repository.NodeConstants.NAME;
import static net.ion.radon.repository.NodeConstants.OWNER;
import static net.ion.radon.repository.NodeConstants.PATH;
import static net.ion.radon.repository.NodeConstants.TIMEZONE;
import static net.ion.radon.repository.NodeConstants.UID;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.db.procedure.IStringObject;
import net.ion.framework.util.ChainMap;
import net.ion.framework.util.HashFunction;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.innode.InListNodeImpl;
import net.ion.radon.repository.innode.NormalInNode;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.radon.repository.util.CipherUtil;
import net.ion.radon.repository.util.MyNumberUtil;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class NodeImpl implements Node {

	private static final long serialVersionUID = -9085850570829905483L;
	private transient Session session ;
	private NodeObject nobject;
	private String workspaceName;

	private NodeImpl(Session session, String workspaceName, NodeObject no, String parentPath, String name) {
		this.session = session ;
		this.workspaceName = workspaceName;
		this.setNodeMetaInfo(no, parentPath, name);
	}

	static NodeImpl create(String workspaceName, NodeObject no, String parentPath, String name) {
		Session currentSession = Session.getCurrent();
		if (currentSession.createQuery().existByPath(makePath(parentPath, name)))
			RepositoryException.throwIt("duplicate path : " + makePath(parentPath, name));
		if (!isSmallAlphaNumUnderBarComma(name))
			throw new IllegalArgumentException(name + " must be smallAlphaNumUnderBarComma");

		final NodeImpl result = new NodeImpl(currentSession, workspaceName, no, parentPath, name);
		result.setName(parentPath, name) ;
		result.setAradonId("__empty", result.getIdentifier()) ;
		result.notify(NodeEvent.CREATE);
		return result;
	}

	static NodeImpl load(String workspaceName, NodeObject no) {
		final String path = no.getString(PATH);
		String parentPath = StringUtil.substringBeforeLast(path, "/");
		if (StringUtil.isBlank(parentPath))
			parentPath = "/";

		NodeImpl loadedNode = new NodeImpl(Session.getCurrent(), workspaceName, no, parentPath, no.getString(NAME));

		return loadedNode;
	}

	static NodeImpl load(String workspaceName, DBObject dbo) {
		if (dbo == null) return null ;
		return load(workspaceName, NodeObject.load(dbo));
	}

	private static boolean isSmallAlphaNumUnderBarComma(String str) {

		if (str == null || str.length() == 0) {
			return false;
		}
		char[] chars = str.toCharArray();
		for (char ch : chars) {
			if ((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '.') {
				continue;
			} else
				return false;
		}

		return true;
	}

	public void notify(NodeEvent event) {
		if (workspaceName == null || StringUtil.isBlank(workspaceName)) return ;
		getSession().notify(this, event);
	}


	public Session getSession() {
		if (session != null){
			return session ;
		} else {
			return Session.getCurrent() ;
		}
	}

	// @TODO if prop has '.' 
	public Serializable get(String propId) {
		return nobject.get(propId, this) ;
	}

	public Serializable get(String propId, int index) {
		return nobject.get(propId, index, this);
	}
	
	public int getAsInt(String propId) {
		return MyNumberUtil.getAsInt(get(propId)) ;
	}

	public DBObject getDBObject() {
		return nobject.getDBObject();
	}

	public synchronized Node putEncrypt(String key, String value) throws RepositoryException {
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

	public synchronized Node put(String key, Object val) {
		return putProperty(PropertyId.create(key), (val != null && val instanceof IStringObject) ? ((IStringObject)val).getString() : val );
	}

	private synchronized Node putProperty(PropertyId propId, Object val) {
		nobject.putProperty(propId, val);
		notify(NodeEvent.UPDATE);
		return this;
	}

	public synchronized Node append(String key, Object val) {
		nobject.appendProperty(PropertyId.create(key), val);
		notify(NodeEvent.UPDATE);
		return this;
	}

	private void setName(String parentPath, String name) {
		putProperty(PropertyId.reserved(PATH), makePath(parentPath, name));
		putProperty(PropertyId.reserved(NAME), name);
	}

	private void setNodeMetaInfo(NodeObject _dbo, String parentPath, String name) {

		this.nobject = _dbo;

		if (_dbo.get(ID) == null || (!(_dbo.get(ID) instanceof ObjectId))) {
			putProperty(PropertyId.reserved(ID), new ObjectId());
		}

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

	public Node setAradonId(String groupid, Object uid) {
		if (existAradonId(groupid, uid))
			throw RepositoryException.throwIt("duplicated groupId/uid : " + groupid + "/" + uid);

		NodeObject inner = NodeObject.create();

		inner.put(GROUP, makeGroups(groupid));
		inner.put(GHASH, HashFunction.hashGeneral(groupid));
		inner.put(UID, uid);

		putProperty(PropertyId.reserved(ARADON), inner);
		return this;
	}

	public Map toMap() {
		return nobject.toMap(this) ;
	}

	public Map<String, ? extends Object> toPropertyMap() {
		return nobject.toPropertyMap(this );
	}

	public Map<String, ? extends Object> toPropertyMap(NodeColumns cols) {
		return nobject.toPropertyMap(cols, this) ;
	}

	public String toString() {
		return toPropertyMap().toString();
	}

	public String getIdentifier() {
		return getDBObject().get(ID).toString();
	}

	public String getWorkspaceName() {
		return workspaceName;
	}

	public Object getId() {
		return getDBObject().get(ID);
	}

	private static String makePath(String parentPath, String name) {
		return "/" + StringUtil.join(StringUtil.split(parentPath + "/" + name, "/"), "/");
	}

	public String getName() {
		return StringUtil.toString(getDBObject().get(NAME));
	}

	private boolean existAradonId(String groupid, Object uid) {
		return getSession().createQuery().aradonGroupId(groupid, uid).findOne() != null;
	}

	private String[] makeGroups(String groupid) {
		String[] groups = StringUtil.split(groupid, ".");

		String[] result = new String[groups.length];
		for (int i = 0; i < groups.length; i++) {
			result[i] = StringUtil.join(groups, ".", 0, i + 1);
		}
		return result;
	}

	// child
	public Node createChild(String name) {
		return getSession().createChild(this, name);
	}

	public String getPath() {
		return getString(PATH);
	}

	public String getString(String key) {
		return StringUtil.toString(get(key));
	}

	public ReferenceTaragetCursor getChild() {
		return getSession().createRefQuery().child(this).find();
	}

	public ReferenceTaragetCursor getChild(String name) {
		return getSession().createRefQuery().child(this, name).find();
	}

	public List<Node> removeChild(String nameOrId) {
		return getSession().getReferenceManager().removeChildNode(this, nameOrId);
	}
	
	public List<Node> removeChild() {
		return getSession().getReferenceManager().removeChildNodes(this);
	}

	public List<Node> removeDescendant() {
		return getSession().getReferenceManager().removeDescendant(this);
	}

	void clearProp(boolean fire) {
		Map<String, ?> prop = toPropertyMap();
		for (String key : prop.keySet()) {
			if (NodeUtil.isReservedProperty(key))
				continue;
			getDBObject().removeField(key);
		}

		if (fire)
			notify(NodeEvent.UPDATE);
	}

	public void clearProp() {
		clearProp(true);
	}

	public ReferenceObject toRef() {
		ReferenceObject result = new ReferenceObject(workspaceName, getId());
		result.put("orderNo", get("orderNo"));

		return result;
	}

	public Node getParent() {
		final ReferenceTaragetCursor rc = getSession().createRefQuery().parent(this).find();
		if (rc.size() == 0)
			return getSession().getRoot();
		return rc.next();
	}

	public AradonId getAradonId() {
		return AradonId.create((InNode) get(ARADON));
	}

	public boolean hasProperty(String key) {
		return nobject.containsField(key);
	}

	public boolean addReference(String relType, AradonQuery query) {

		if (getAradonId() == AradonId.EMPTY)
			throw RepositoryException.throwIt("this operation not permitted. since this node has not aradon id : ");

		List<Node> nodes = getSession().findAllWorkspace(query) ;
		for (Node node : nodes) {
			getSession().addReference(this, relType, node);
		}
		return nodes.size() > 0;
	}

	public ReferenceTaragetCursor getReferencedNodes(String aradonGroup) {
		return getSession().createRefQuery().to(this, ":aradon:" + aradonGroup).find();
	}

	public int removeReference(String refType, AradonQuery query) {
		List<Node> nodes = getSession().findAllWorkspace(query) ;
		
		for (Node node : nodes) {
			getSession().createRefQuery().from(this, refType, node).remove() ;
		}
		
		return nodes.size();
	}

	public NormalInNode inner(String name) {
		return (NormalInNode)nobject.inner(name, this) ;
	}
	
	public boolean setReference(String refType, AradonQuery preQuery, AradonQuery newQuery) {
		removeReference(refType, preQuery);
		return addReference(refType, newQuery);
	}

	public long getLastModified() {
		final String str = StringUtil.toString(get(LASTMODIFIED));
		return NumberUtil.isNumber(str) ? Long.parseLong(str) : 0L;
	}

	public InListNode inlist(String name) {
		Object result = nobject.get(name);
		return inlist(name, result);
	}

	private InListNode inlist(String name, Object result) {
		return InListNodeImpl.load((BasicDBList)result, name, this);
	}
	
	
	
	public boolean isModified(){
		return session.getModified().contains(this) ;
	}

	public boolean isNew() {
		return getLastModified() == 0L;
	}
}

