package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ID;
import static net.ion.radon.repository.NodeConstants.PATH;

import java.util.List;
import java.util.Map;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.ChainMap;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.AradonQuery;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

public class SessionQuery {

	private Session session ;
	private Workspace workspace;
	
	private PropertyQuery inner ;
	private PropertyFamily sort = PropertyFamily.create() ;

	private SessionQuery(Session session, PropertyQuery inner){
		this.session = session ;
		this.workspace = session.getCurrentWorkspace();
		this.inner = inner ;
	}
	
	public static SessionQuery create(Session session) {
		return new SessionQuery(session, PropertyQuery.create());
	}
	
	static SessionQuery create(Session session, PropertyQuery definedQuery) {
		return new SessionQuery(session, definedQuery); 
	}

	
	public Node findByAradonId(String groupId, Object uid){
		inner.put(AradonQuery.newByGroupId(groupId, uid)) ;
		return findOne() ;
	}
	
	public Node findByPath(String path) {
		String newPath = (path != null && path.startsWith("/")) ? path : "/" + path ;
		if ("/".equals(newPath)) return session.getRoot() ;
		for (Node node : session.getModified()) {
			if (newPath.equals(node.getPath())) {
				return node;
			}
		}
		
		return workspace.findOne(PropertyQuery.create(PATH, newPath), Columns.ALL);
	}
	
	boolean existByPath(String path) {
		String newPath = (path != null && path.startsWith("/")) ? path : "/" + path ;
		if ("/".equals(newPath)) return true ;
		return workspace.find(PropertyQuery.create(PATH, newPath), Columns.append().add(NodeConstants.NAME)).count() > 0;
	}
	
	
	public NodeCursor find() throws RepositoryException {
		return workspace.find(inner, Columns.ALL).sort(sort);
	}

	public NodeCursor find(Columns columns) throws RepositoryException{
		return workspace.find(inner, columns).sort(sort);
	}



	
	public Node findOne() throws RepositoryException {
		return workspace.findOne(inner, Columns.ALL);
	}

	public Node findOne(Columns exclude) {
		return workspace.findOne(inner, exclude);
	}

	
	public boolean existNode(){
		return find().count() > 0;
	}

	public int remove(){
		NodeResult result = workspace.removeQuery(inner) ;
		session.setLastResult(result) ;
		return result.getRowCount();
	}

	public SessionQuery aradonGroup(String groupid){
		inner.put(AradonQuery.newByGroup(groupid)) ;
		return this ;
	}

	public SessionQuery aradonGroupId(String groupid, Object uid){
		inner.put(AradonQuery.newByGroupId(groupid, uid)) ;
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
	public SessionQuery and(IPropertyFamily... conds) {
		inner.and(conds) ;
		return this ;
	}

	public SessionQuery or(IPropertyFamily... conds) {
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
	
	public Object group(String... keys) {
		inner.group(keys);
		return this;
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

	public int count() {
		return find().count();
	}

	public boolean overwriteOne(Map<String, ?> map) {
		NodeResult result = updateLastResult(workspace.findAndOverwrite(inner, map));
		return result != NodeResult.NULL;
	}
	
	public boolean updateOne(Map<String, ?> map) {
		NodeResult result = updateLastResult(workspace.findAndUpdate(inner, map)) ;
		return result.getRowCount() > 0;
	}

	public PropertyQuery getQuery(){
		return inner ;
	}

	public NodeResult update(ChainMap modValues){
		return update(modValues.toMap()) ;
	}

	public NodeResult update(Map<String, ?> modValues){
		return updateLastResult(workspace.set(inner, modValues));
	}
	
	private NodeResult updateLastResult(NodeResult result) {
		session.setLastResult(result) ;
		return result;
	}

	public NodeResult increase(String propId){
		return increase(propId, 1);
	}
	
	public NodeResult increase(String propId, int incvalue){
		return updateLastResult(workspace.inc(inner, StringUtil.lowerCase(propId), incvalue));
	}
	
	
	public Node findOneInDB(String groupid, Object uid) {
		
		for(String wname : session.getRepositorys().getWorkspaceNames()){
			Node node = session.getRepositorys().getWorkspace(wname).findOne(AradonQuery.newByGroupId(groupid, uid).getQuery(), Columns.ALL);
			if (node != null) return node ;
		}
		return null;
	}

	public InListQueryNode inlist(String field) {
		return InListQueryNode.create(field, session, this.inner) ;
	}




}
