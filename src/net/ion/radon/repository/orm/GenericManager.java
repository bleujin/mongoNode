package net.ion.radon.repository.orm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.ion.framework.util.Debug;
import net.ion.framework.util.HashFunction;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeConstants;
import net.ion.radon.repository.NodeImpl;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.Workspace;
import net.ion.radon.repository.orm.bean.Employee;

public class GenericManager<T extends NodeORM> {

	private Session session;
	private IDMethod idm;

	@SuppressWarnings("unchecked")
	protected GenericManager(Session session) {
		this.session = session;
		this.idm = findGenericIDMethod(getClass());
	}

	public T loadInstance(Object uid) throws IllegalArgumentException {
		try {
			Constructor<T>[] cons = idm.managerClz().getDeclaredConstructors();
			for (Constructor<T> con : cons) {
				if (con.getParameterTypes().length == 1) {
					if (!con.isAccessible())
						con.setAccessible(true);
					T newInstance = con.newInstance(uid);
					newInstance.mergeNode(session, idm, uid);
					return newInstance;
				}
			}
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e.getCause());
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e.getCause());
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e.getCause());
		}

		throw new IllegalArgumentException("not found constructure[uid]");
	}

	private static <T> IDMethod findGenericIDMethod(Class<T> myClz) {
		ParameterizedType genericSuperclass = (ParameterizedType) myClz.getGenericSuperclass();
		Type type = genericSuperclass.getActualTypeArguments()[0];
		Class<T> clz = null;
		if (type instanceof ParameterizedType) {
			clz = (Class<T>) ((ParameterizedType) type).getRawType();
		} else {
			clz = (Class<T>) type;
		}

		return clz.getAnnotation(IDMethod.class);
	}

	GenericManager(Session session, IDMethod idm) {
		this.session = session;
		this.idm = idm;
	}

	public final static <T extends NodeORM> GenericManager<T> create(Session session, Class<T> clz) {
		return new GenericManager<T>(session, clz.getAnnotation(IDMethod.class));
	}

	public T findById(Object keyPropValue) {
		return createQuery().findById(keyPropValue);
	}

//	public int save(T p) {
//		return p.save().getRowCount();
//	}

	public BeanQuery<T> createQuery() {
		return BeanQuery.create(session, this);
	}

	public String groupId() {
		return idm.groupId();
	}

	protected Session getSession() {
		return session;
	}

	public IDMethod getIDMethod() {
		return idm;
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
