package net.ion.radon.repository;

import java.lang.reflect.InvocationTargetException;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.HashFunction;
import net.ion.radon.repository.orm.AbstractORM;
import net.ion.radon.repository.orm.BeanCursor;
import net.ion.radon.repository.orm.IDMethod;

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
		Node node = getWorkspace().findOne(session, idRow.getAradonQuery(), Columns.ALL);
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
		NodeResult result = getWorkspace().setMerge(session, idRow.aradonId(), p.getNodeObject().toPropertyMap(session.getRoot()));
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
		return BeanCursor.create(getWorkspace().find(session, query.aradonGroup(groupId()), Columns.ALL), this);
	}

	
	
	public int remove(Object keyPropValue) {
		return getWorkspace().remove(session, getIDRow(keyPropValue).aradonId()).getRowCount();
	}

	public int remove(PropertyQuery query) {
		NodeResult nr = getWorkspace().remove(session, query.aradonGroup(groupId()));
		return nr.getRowCount();
	}
	
	public int removeAll() {
		NodeResult nr = getWorkspace().remove(session, PropertyQuery.createByAradon(groupId()));
		return nr.getRowCount();
	}


	private Workspace getWorkspace() {
		return session.getWorkspace(idm.workspaceName());
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
