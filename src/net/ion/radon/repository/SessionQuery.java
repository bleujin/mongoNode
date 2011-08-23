package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ID;

import java.util.List;
import java.util.Map;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.AradonQuery;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

public class SessionQuery {

	private Session session ;
	private Workspace workspace;
	
	private PropertyQuery inner = PropertyQuery.create();
	private PropertyFamily sort = PropertyFamily.create() ;
	private Map<String, Object> modValues = MapUtil.newMap() ;

	private SessionQuery(Session session){
		this.session = session ;
		this.workspace = session.getCurrentWorkspace();
	}
	
	public static SessionQuery create(Session session) {
		return new SessionQuery(session);
	}
	
	public NodeCursor findByAradonId(String groupId, Object uid){
		inner.put(AradonQuery.newByGroupId(groupId, uid)) ;
		return find() ;
	}
	
	public Node findByPath(String path) {
		String newPath = (path != null && path.startsWith("/")) ? path : "/" + path ;
		if ("/".equals(newPath)) return session.getRoot() ;
		for (Node node : session.getModified()) {
			if (newPath.equals(node.getPath())) {
				return node;
			}
		}

		return workspace.findByPath(newPath);
	}
	
	boolean existByPath(String path) {
		Node target = null ;
		String newPath = (path != null && path.startsWith("/")) ? path : "/" + path ;
		if ("/".equals(newPath)) target = session.getRoot() ;
		target = workspace.findByPath(newPath);

		return target != null;
	}
	
	
	public NodeCursor find() throws RepositoryException {
		return workspace.find(inner).sort(sort);
	}
	
	public Node findOne() throws RepositoryException {
		return workspace.findOne(inner);
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
		inner.gte(key, open);
		inner.lte(key, close);
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
	
	@Override
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
		NodeResult result = workspace.findAndOverwrite(inner, map);
		session.setLastResult(result) ;
		return result != NodeResult.NULL;
	}
	
	public boolean updateOne(Map<String, ?> map) {
		NodeResult result = workspace.findAndUpdate(inner, map) ;
		session.setLastResult(result) ;
		return result != NodeResult.NULL;
	}

	public PropertyQuery getQuery(){
		return inner ;
	}

	
	public SessionQuery put(String path, Object value) {
		modValues.put(path, value) ;
		return this;
	}

	public NodeResult update(){
		NodeResult nodeResult = workspace.update(inner, modValues);
		session.setLastResult(nodeResult) ;
		return nodeResult ;
	}
	
	public Node findOneAtRepository(String groupid, Object uid) {
		
		for(String wname : session.getRepositorys().getWorkspaceNames()){
			Node node = session.getRepositorys().getWorkspace(wname).findOne(AradonQuery.newByGroupId(groupid, uid).getQuery());
			if (node != null) return node ;
		}
		return null;
	}

	public InListQueryNode inlist(String field) {
		return InListQueryNode.create(field, session, this.inner) ;
	}


}
