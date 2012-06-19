package net.ion.radon.repository.orm;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Columns;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeConstants;
import net.ion.radon.repository.NodeObject;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;
import net.ion.radon.repository.SessionQueryImpl;

import org.apache.commons.beanutils.ConstructorUtils;

public class BeanQuery<T extends NodeORM> {

	private GenericManager<T> gm ;
	private final SessionQuery squery;
	private BeanQuery(Session session, GenericManager<T> gm) {
		this.squery = SessionQueryImpl.create(session, gm.getIDMethod().workspaceName()).aradonGroup(gm.getIDMethod().groupId()) ;
		this.gm = gm ;
	}

	public static <T extends NodeORM> BeanQuery<T> create(Session session, GenericManager<T> gm) {
		return new BeanQuery<T>(session, gm);
	}

	public BeanQuery<T> and(PropertyQuery query) {
		squery.and(query);
		return this ;
	}

	public T findOne() {
		return toBean(squery.findOne());
	}

	public T findOne(Object keyValue){
		return toBean(squery.and(getIDRow(keyValue).getAradonQuery()).findOne()) ;
	}
	
	private IDRow getIDRow(Object propValue) {
		return IDRow.create(getIDMethod().groupId(), propValue);
	}

	
	private T toBean(Node node) throws RepositoryException {
		try {
			if (node == null)
				return null;
			
			IDMethod idm = gm.getIDMethod() ;
			NodeORM result = idm.managerClz().cast(ConstructorUtils.invokeConstructor(idm.managerClz(), new Object[] { node.getAradonId().getUid() }));
			return (T) result.load(node);
		} catch (NoSuchMethodException e) {
			throw RepositoryException.throwIt(e);
		} catch (IllegalAccessException e) {
			throw RepositoryException.throwIt(e);
		} catch (InvocationTargetException e) {
			throw RepositoryException.throwIt(e);
		} catch (InstantiationException e) {
			throw RepositoryException.throwIt(e);
		}
	}
	
	public int remove(Object keyValue){
		return squery.and(getIDRow(keyValue).getAradonQuery()).remove() ;
	}

	public BeanCursor<T> find() {
		return BeanCursor.create(squery.find(), gm.getIDMethod(), gm);
	}
	
	

	public List<T> toList(PageBean page) {
		List<T> result = ListUtil.newList() ;
		
		List<Node> nodes = squery.find(page) ;
		for (Node node : nodes) {
			result.add(toBean(node)) ;
		}
		return result;
	}
	
	IDMethod getIDMethod(){
		return gm.getIDMethod() ;
	}

	

	public BeanCursor<T> find(Columns columns) throws RepositoryException{
		return BeanCursor.create(squery.find(columns), gm.getIDMethod(), gm);
	}


	public T findById(Object keyValue) {
		IDRow idRow = getIDRow(keyValue);
		
		
		
		return and(idRow.getAradonQuery()).findOne();
	}

	public T findOne(Columns columns) {
		return toBean(squery.findOne(columns));
	}
	
	public boolean exist(){
		return find().count() > 0;
	}

	
	
	
	public BeanQuery<T> aradonGroup(String groupId){
		squery.aradonGroup(groupId) ;
		return this ;
	}

	public BeanQuery<T> aradonGroupId(String groupId, Object uId){
		squery.aradonGroupId(groupId, uId) ;
		return this ;
	}

	public BeanQuery<T> eq(String key, Object value) {
		squery.eq(key, value);
		return this;
	}

	public BeanQuery<T> in(String key, Object[] objects) {
		squery.in(key, objects);
		return this ;
	}
	
	public BeanQuery<T> nin(String key, Object[] objects){
		squery.nin(key, objects);
		return this;
	}
	public BeanQuery<T> and(PropertyQuery... conds) {
		squery.and(conds) ;
		return this ;
	}

	public BeanQuery<T> or(PropertyQuery... conds) {
		squery.or(conds) ;
		return this ;
	}
	
	public BeanQuery<T> ne(String key, String value) {
		squery.ne(key, value);
		return this ;
	}

	public BeanQuery<T> between(String key, Object open, Object close ){
		squery.between(key, open, close);
		return this;
	}
	
	public BeanQuery<T> where(String where ){
		squery.where(where);
		return this;
	}
	
	public BeanQuery<T> gte(String key, Object value) { // key >= val
		squery.gte(key, value) ;
		return this ;
	}
	public BeanQuery<T> lte(String key, Object value) { // key <= val
		squery.lte(key, value) ;
		return this ;
	}

	public BeanQuery<T> eleMatch(String key, PropertyQuery eleQuery) {
		squery.eleMatch(key, eleQuery) ;
		return this ;
	}

	
	public BeanQuery<T> isExist(String key) {
		squery.isExist(key);
		return this ;
	}
	
	public BeanQuery<T> isNotExist(String key) {
		squery.isNotExist(key) ;
		return this ;
	}


	public BeanQuery<T> gt(String key, Object value) {
		squery.gt(key, value) ;
		return this;
	}

	public BeanQuery<T> lt(String key, Object value) {
		squery.lt(key, value) ;
		return this;
	}
	

	public BeanQuery<T> to(Node target, String relType) {
		PropertyQuery idquery = PropertyQuery.create(NodeConstants.RELATION + "." + relType, target.selfRef());
		PropertyQuery aidquery = PropertyQuery.create(NodeConstants.RELATION + "." + relType, NodeObject.create().put("_ref", target.getWorkspaceName())) ;
		Debug.line(idquery, aidquery) ;
		this.or(idquery, aidquery) ;
		return this;
	}


	public List<T> find(PageBean page) throws RepositoryException {
		return find().toList(page);
	}


	public BeanQuery<T> ascending(String... propIds) {
		squery.ascending(propIds) ;
		return this ;
	}
	
	public BeanQuery<T> descending(String... propIds) {
		squery.descending(propIds) ;
		return this ;
	}
	
	public BeanQuery<T> regEx(String key, String regValue) {
		squery.regEx(key, regValue) ;
		return this;
	}
	
	public BeanQuery<T> id(String oid){
		squery.id(oid) ;
		return this ;
	}
	
		
	public int count() {
		return find().count();
	}

	public int remove(){
		return squery.remove() ;
	}

	NodeResult merge(Map<String, ?> modValues) {
		return squery.merge(modValues) ;
	}
	
	public String toString() {
		return squery.toString();
	}


	
}
