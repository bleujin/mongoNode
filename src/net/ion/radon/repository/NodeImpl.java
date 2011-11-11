package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ARADON;
import static net.ion.radon.repository.NodeConstants.CREATED;
import static net.ion.radon.repository.NodeConstants.ID;
import static net.ion.radon.repository.NodeConstants.LASTMODIFIED;
import static net.ion.radon.repository.NodeConstants.NAME;
import static net.ion.radon.repository.NodeConstants.OWNER;
import static net.ion.radon.repository.NodeConstants.PATH;
import static net.ion.radon.repository.NodeConstants.TIMEZONE;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.db.procedure.IStringObject;
import net.ion.framework.util.ChainMap;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.innode.InListNodeImpl;
import net.ion.radon.repository.innode.NormalInNode;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.radon.repository.relation.IRelation;
import net.ion.radon.repository.util.CipherUtil;
import net.ion.radon.repository.util.MyNumberUtil;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class NodeImpl implements Node {

	private static final long serialVersionUID = -9085850570829905483L;
	private transient Session session ;
	private NodeObject nobject;
	private String workspaceName;

	private transient final PropertyQuery query ;
	
	private NodeImpl(Session session, PropertyQuery query, String workspaceName, NodeObject no, String parentPath, String name) {
		this.session = session ;
		this.workspaceName = workspaceName;
		this.setNodeMetaInfo(no, parentPath, name);
		this.query = query;
	}

	static NodeImpl create(String workspaceName, NodeObject no, String parentPath, String name) {
		Session currentSession = Session.getCurrent();
		if (currentSession.createQuery().existByPath(makePath(parentPath, name)))
			RepositoryException.throwIt("duplicate path : " + makePath(parentPath, name));
		if (!isSmallAlphaNumUnderBarComma(name))
			throw new IllegalArgumentException(name + " must be smallAlphaNumUnderBarComma");

		final NodeImpl result = new NodeImpl(currentSession, PropertyQuery.EMPTY, workspaceName, no, parentPath, name);
		result.setName(parentPath, name) ;
		result.setAradonId("__empty", result.getIdentifier()) ;
		result.notify(NodeEvent.CREATE);
		return result;
	}

	static NodeImpl load(PropertyQuery query, String workspaceName, NodeObject no) {
		final String path = no.getString(PATH);
		String parentPath = StringUtil.substringBeforeLast(path, "/");
		if (StringUtil.isBlank(parentPath))
			parentPath = "/";

		NodeImpl loadedNode = new NodeImpl(Session.getCurrent(), query, workspaceName, no, parentPath, no.getString(NAME));

		return loadedNode;
	}

	static NodeImpl load(PropertyQuery query, String workspaceName, DBObject dbo) {
		if (dbo == null) return null ;
		return load(query, workspaceName, NodeObject.load(dbo));
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

	// prop has '.' or '#' 
	public Serializable get(String propId) {
		if (propId.startsWith("$")){ // aid
			return getAradonIdExpression(propId) ;
		}
		
		String propExpr = transRegular(propId) ;
		if (propId.startsWith("#")){   // relation
			return getRelationExpression(propExpr) ;
		} else if (propId.startsWith("!")){ // path
			return getPathExpression(propExpr);
		} 		
		return nobject.get(propExpr, this) ;
	}

	private Serializable getAradonIdExpression(String propExpr) {
		String aidExpr = transNumRegular(StringUtil.substringBetween(propExpr, "$", ".")) ;
		String remain = transRegular(StringUtil.substringAfter(propExpr, ".")) ;
		
		String wsName = getWorkspaceName() ;
		if (StringUtil.countMatches(aidExpr, ":") == 2) {
			wsName = StringUtil.substringBefore(aidExpr, ":") ;
			aidExpr = StringUtil.substringAfter(aidExpr, ":") ;
		}
		String groupId = StringUtil.substringBefore(aidExpr, ":") ;
		String uidExpr = StringUtil.substringAfter(aidExpr, ":") ;
		Object uid = (uidExpr != null && uidExpr.startsWith("#")) ? Integer.parseInt(uidExpr.substring(1)) : uidExpr ;
		
		Node targetNode = getQuery().findOne(getSession(), wsName, getQuery(), PropertyQuery.createByAradon(groupId, uid)) ;
		return targetNode == null ? null : targetNode.get(remain) ;
	}

	private Serializable getPathExpression(String propExpr) {

		String path = StringUtil.substringBetween(propExpr, "!", ".") ;
		String remain = StringUtil.substringAfter(propExpr, ".") ;
		
		String wsName = getWorkspaceName() ;
		if (path.contains(":")) {
			wsName = StringUtil.substringBefore(path, ":") ;
			path = StringUtil.substringAfter(path, ":") ;
		}
		Node targetNode = getQuery().findOne(getSession(), wsName, getQuery(), PropertyQuery.createByPath(path)) ;
		return targetNode == null ? null : targetNode.get(remain) ;
	}
	
	private Serializable getRelationExpression(String propExpr){
		
		String relType = StringUtil.substringBetween(propExpr, "#", ".") ;
		String remain = StringUtil.substringAfter(propExpr, ".") ;
		
		Node targetNode = relation(relType).fetch(0);
		return targetNode == null ? null : targetNode.get(remain) ;
	}

	private static Pattern p = Pattern.compile("\\{[a-zA-Z][a-zA-Z0-9_.]*\\}");
	private String transRegular(String key) {
		if (! key.contains("{")) return key ;
		
		Matcher m = p.matcher(key);

		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			Serializable value = get(StringUtil.substringBetween(m.group(), "{", "}"));
			m.appendReplacement(sb, ObjectUtil.toString(value));
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	private String transNumRegular(String key) {
		if (! key.contains("{")) return key ;
		
		Matcher m = p.matcher(key);

		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			Serializable value = get(StringUtil.substringBetween(m.group(), "{", "}"));
			m.appendReplacement(sb, ((value instanceof Integer) ? "#" : "") + ObjectUtil.toString(value));
		}
		m.appendTail(sb);
		return sb.toString();
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


		putProperty(PropertyId.reserved(ARADON), AradonId.create(groupid, uid).toNodeObject());
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

	public NodeCursor getChild() {
		return session.createQuery().eq(NodeConstants.RELATION + "." + NodeConstants.PARENT, this.selfRef()).find();
	}

	public Node getChild(String name) {
		return getSession().createQuery().findByPath(getPath() + "/" + name);
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

	public Node getParent() {
		Node parent = relation(NodeConstants.PARENT).fetch(0);
		
		
		return (parent == null) ? getSession().getRoot() : parent ;
		
//		final ReferenceTaragetCursor rc = getSession().createRefQuery().parent(this).find();
//		if (rc.size() == 0)
//			return getSession().getRoot();
//		return rc.next();
	}

	public AradonId getAradonId() {
		return AradonId.create((InNode) get(ARADON));
	}

	public boolean hasProperty(String key) {
		return nobject.containsField(key);
	}


	public NormalInNode inner(String name) {
		return (NormalInNode)nobject.inner(name, this) ;
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

	public Node toRelation(String relType, NodeRef aref) {
		inner(NodeConstants.RELATION).inlist(relType).push(aref.toMap());
		return this ;
	}

	public IRelation relation(String relType) {
		InListNode refList = inner(NodeConstants.RELATION).inlist(relType);
		return NodeRelation.load(this, refList, relType) ;
	}

	public PropertyQuery getQuery() {
		return query;
	}

	public NodeRef selfRef() {
		return NodeRef.create(this);
	}
}

