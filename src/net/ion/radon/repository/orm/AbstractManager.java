package net.ion.radon.repository.orm;

import java.lang.reflect.InvocationTargetException;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.HashFunction;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeConstants;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeImpl;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;
import net.ion.radon.repository.Workspace;

import org.apache.commons.beanutils.ConstructorUtils;

public abstract class AbstractManager<T extends AbstractORM> {

	private Session session;
	private IDMethod idm;

	public AbstractManager(Session session) {
		this.session = session;
		this.idm = getClass().getAnnotation(IDMethod.class);
	}

	public T findById(Object keyPropValue) {
		IDRow idRow = getIDRow(keyPropValue);
		Node node = createQuery().and(idRow.getAradonQuery()).findOne();
		return toBean(node);
	}

	
	public T toBean(Node node) throws RepositoryException {
		try {
			AbstractORM result = idm.managerClz().cast(ConstructorUtils.invokeConstructor(idm.managerClz(), new Object[0]));
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

	public int save(T p) {
		IDRow idRow = getIDRow(p);
		NodeResult result = createQuery().and(idRow.aradonId()).merge(p.getNodeObject().toPropertyMap(session.getRoot()));
		return result.getRowCount();
	}

	public Node toNode(T p) {
		IDRow idRow = getIDRow(p);
		return NodeImpl.load(session, idRow.getAradonQuery(), idm.workspaceName(), p.getNodeObject());
	}

	protected Session getSession() {
		return session;
	}

	private IDRow getIDRow(Object propValue) {
		return IDRow.create(groupId(), propValue);
	}

	protected IDRow getIDRow(T p) {
		return IDRow.create(groupId(), p.get(idm.keyPropId()));
	}

	public BeanCursor<T> find(PropertyQuery query) {
		NodeCursor nc = createQuery().and(query.aradonGroup(groupId())).find() ;
		return BeanCursor.create(nc, this);
	}

	
	
	public int remove(Object keyPropValue) {
		return  createQuery().and(getIDRow(keyPropValue).aradonId()).remove() ;
	}

	public int remove(PropertyQuery query) {
		return createQuery().and(query.aradonGroup(groupId())).remove() ;
	}
	
	public int removeAll() {
		return createQuery().aradonGroup(groupId()).remove();
	}


	protected Workspace getWorkspace() {
		return session.getWorkspace(idm.workspaceName());
	}
	
	protected SessionQuery createQuery(){
		return session.createQuery(idm.workspaceName()) ;
	}

	public String groupId() {
		return idm.groupId();
	}

}

class IDRow {

	private final String groupId;
	private final Object uId;

	private IDRow(String groupId, Object uId) {
		this.groupId = groupId;
		this.uId = uId;
	}

	public final static IDRow create(String groupId, Object uId) {
		return new IDRow(groupId, uId);
	}

	public Object getValue() {
		return uId;
	}

	public String getGroup() {
		return groupId;
	}

	public PropertyQuery getAradonQuery() {
		return PropertyQuery.createByAradon(getGroup(), uId);
	}

	PropertyQuery aradonId() {
		return PropertyQuery.create().eq(NodeConstants.ARADON_GROUP, new String[] { getGroup() }).eq(NodeConstants.ARADON_UID, uId).eq(NodeConstants.ARADON_GHASH, HashFunction.hashGeneral(getGroup()));
	}
}
