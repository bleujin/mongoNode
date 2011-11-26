package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ID;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.ChainMap;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.ics.ActionQuery;
import net.ion.radon.repository.mr.ReduceFormat;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

public class SessionQuery implements Serializable{

	private static final long serialVersionUID = -3597031257688988594L;
	private transient Session session ;
	private String targetWName ;
	private PropertyQuery inner ;
	private PropertyFamily sort = PropertyFamily.create() ;

	private SessionQuery(Session session, String targetWName, PropertyQuery inner){
		this.session = session ;
		this.targetWName = targetWName ;
		this.inner = inner ;
	}
	
	public static SessionQuery create(Session session) {
		return new SessionQuery(session, session.getCurrentWorkspaceName(), PropertyQuery.create());
	}
	
	public static SessionQuery create(Session session, PropertyQuery definedQuery) {
		return new SessionQuery(session, session.getCurrentWorkspaceName(), definedQuery); 
	}
	
	public static SessionQuery create(Session session, String wname) {
		return new SessionQuery(session, wname, PropertyQuery.create()); 
	}

	
	
	public NodeCursor find() throws RepositoryException {
		return getWorkspace().find(session, inner, Columns.ALL).sort(sort);
	}

	public NodeCursor find(Columns columns) throws RepositoryException{
		return getWorkspace().find(session, inner, columns).sort(sort);
	}

	public Node findOne() throws RepositoryException {
		return getWorkspace().findOne(session, inner, Columns.ALL);
	}

	public Node findOne(Columns columns) {
		return getWorkspace().findOne(session, inner, columns);
	}

	
	public boolean existNode(){
		return find().count() > 0;
	}

	public int remove(){
		NodeResult result = getWorkspace().remove(session, inner) ;
		return result.getRowCount();
	}

	public SessionQuery aradonGroup(String groupId){
		inner.aradonGroup(groupId) ;
		return this ;
	}

	public SessionQuery aradonGroupId(String groupId, Object uId){
		inner.aradonId(groupId, uId) ;
		return this ;
	}

	public SessionQuery path(String path){
		String newPath = (path != null && path.startsWith("/")) ? path : "/" + path ;
		inner.path(newPath) ;
		return this ;
	}
	

	public SessionQuery eq(String key, Object value) {
		inner.put(key, value);
		return this;
	}

	public SessionQuery in(String key, Object[] objects) {
		inner.in(key, objects);
		return this ;
	}
	
	public SessionQuery nin(String key, Object[] objects){
		inner.nin(key, objects);
		return this;
	}
	public SessionQuery and(PropertyQuery... conds) {
		for (PropertyQuery cond : conds) {
			for (Entry<String, ? extends Object> c : cond.toMap().entrySet()) {
				inner.put(c.getKey(), c.getValue()) ;
			}
		}
//		inner.and(conds) ;
		return this ;
	}

	public SessionQuery or(PropertyQuery... conds) {
		inner.or(conds) ;
		return this ;
	}
	
	public SessionQuery ne(String key, String value) {
		inner.put(key,  new BasicDBObject("$ne", value));
		return this ;
	}

	public SessionQuery between(String key, Object open, Object close ){
		inner.between(key, open, close);
		return this;
	}
	
	public SessionQuery where(String where ){
		inner.where(where);
		return this;
	}
	
	public SessionQuery gte(String key, Object value) { // key >= val
		inner.gte(key, value) ;
		return this ;
	}
	public SessionQuery lte(String key, Object value) { // key <= val
		inner.lte(key, value) ;
		return this ;
	}

	public SessionQuery eleMatch(String key, PropertyQuery eleQuery) {
		inner.eleMatch(key, eleQuery) ;
		return this ;
	}

	
	public SessionQuery isExist(String key) {
		inner.isExist(key);
		return this ;
	}
	
	public SessionQuery isNotExist(String key) {
		inner.isNotExist(key) ;
		return this ;
	}


	public SessionQuery gt(String key, Object value) {
		inner.gt(key, value) ;
		return this;
	}

	public SessionQuery lt(String key, Object value) {
		inner.lt(key, value) ;
		return this;
	}
	

	public SessionQuery to(Node target, String relType) {
		PropertyQuery idquery = PropertyQuery.create(NodeConstants.RELATION + "." + relType, target.selfRef());
		PropertyQuery aidquery = PropertyQuery.create(NodeConstants.RELATION + "." + relType, NodeObject.create().put("_ref", target.getWorkspaceName())) ;
		Debug.line(idquery, aidquery) ;
		this.or(idquery, aidquery) ;
		return this;
	}


	public List<Node> find(PageBean page) throws RepositoryException {
		return find().toList(page);
	}


	public SessionQuery ascending(String... propIds) {
		for(String propId : propIds)
			sort.put(propId, 1) ;
		return this ;
	}
	
	public SessionQuery descending(String... propIds) {
		for(String propId : propIds)
			sort.put(propId, -1) ;
		return this ;
	}
	
	public String toString() {
		return inner.toString();
	}
	
	public SessionQuery startPathInclude(String path) {
		this.or(PropertyQuery.create().put(NodeConstants.PATH, path), PropertyQuery.create().gt(NodeConstants.PATH, path + "/")) ;
		return this ;
	}

	public SessionQuery regEx(String key, String regValue) {
		inner.put(key,  new BasicDBObject("$regex", regValue));
		return this;
	}
	
	public SessionQuery id(String oid){
		inner.put(ID, new ObjectId(oid)) ;
		return this ;
	}
	
	public SessionQuery aquery(String str) {
		ActionQuery.create(str).merge(inner) ;
		return this ;
	}


	public int count() {
		return find().count();
	}

	// map에 없는 key의 property들은 지워짐
	public boolean overwriteOne(Map<String, ?> map) {
		NodeResult result = getWorkspace().findAndOverwrite(session, inner, map);
		return result != NodeResult.NULL;
	}
	
	// map에 있는 값들만 set, map에 없는 key의 property들은 남아 있음. 
	public boolean updateOne(Map<String, ?> map) {
		NodeResult result = getWorkspace().findAndUpdate(session, inner, map) ;
		return result.getRowCount() > 0;
	}

	public PropertyQuery getQuery(){
		return inner ;
	}

	public NodeResult update(ChainMap modValues){
		return update(modValues.toMap()) ;
	}

	public NodeResult update(Map<String, ?> modValues){
		return getWorkspace().update(session, inner, modValues, false);
	}

	public NodeResult merge(ChainMap modValues) {
		return merge(modValues.toMap());
	}
	
	public NodeResult merge(Map<String, ?> modValues) {
		return getWorkspace().update(session, inner, modValues, true);
	}

	
	// upset = true, 즉 query에 해당하는 row가 없으면 새로 만든다. 
	public NodeResult increase(String propId){
		return increase(propId, 1);
	}
	
	// upset = true, 즉 query에 해당하는 row가 없으면 새로 만든다. 
	public NodeResult increase(String propId, int incvalue){
		return getWorkspace().inc(session, inner, StringUtil.lowerCase(propId), incvalue);
	}
	
	public Node findOneInDB() {
		
		for(String wname : session.getWorkspaceNames()){
			Node node = session.getWorkspace(wname).findOne(session, getQuery(), Columns.ALL);
			if (node != null) return node ;
		}
		return null;
	}

	public InListQueryNode inlist(String field) {
		return InListQueryNode.create(field, session, this) ;
	}
	
	public NodeCursor format(ReduceFormat format) {
		return ProxyCursor.format(session, inner, format, getWorkspace()) ;
	}
	
	public NodeCursor mapreduce(String mapFunction, String reduceFunction, String finalFunction) {
		return ProxyCursor.create(session, inner, mapFunction, reduceFunction, finalFunction, getWorkspace());
	}

	public NodeCursor mapreduce(String mapFunction, String reduceFunction, String finalFunction, CommandOption options) {
		return ProxyCursor.create(session, inner, mapFunction, reduceFunction, finalFunction, getWorkspace(), options);
	}

	public Object apply(String mapFunction, String reduceFunction, String finalFunction, CommandOption options, ApplyHander handler) {
		NodeCursor nc = ProxyCursor.create(session, inner, mapFunction, reduceFunction, finalFunction, getWorkspace(), options) ;
		
		Object result = handler.handle(nc);
		return result ;
	}
	

	
	public NodeCursor group(IPropertyFamily keys, IPropertyFamily initial, String reduce) {
		return ProxyCursor.group(session, inner, keys, initial, reduce, getWorkspace()) ;
	}

	private Workspace getWorkspace(){
		return session.getWorkspace(targetWName);
	}

	public UpdateChain updateChain() {
		return new UpdateChain(session, targetWName, inner);
	}








}
