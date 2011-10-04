package net.ion.radon.repository;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.ConstructorUtils;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.Debug;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeImpl;
import net.ion.radon.repository.NodeObject;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.radon.repository.orm.AbstractORM;
import net.ion.radon.repository.orm.IDMethod;
import net.ion.radon.repository.orm.People;

public abstract class AbstractManager<T extends AbstractORM> {

	private String wsname;
	private Session session;

	public void init(Session session, String wsname) {
		this.session = session;
		this.wsname = wsname;
	}

	public T findById(T p) {
		try {
			IDRow<T> idRow = getIDRow(p);
			
			Node node = session.createQuery().findOneInDB(idRow.getAradonQuery().getGroupId(), idRow.getAradonQuery().getUId());

			Class<? extends AbstractORM> clz = p.getClass();
			AbstractORM result = clz.cast(ConstructorUtils.invokeConstructor(clz, new Object[0]));

			return (T) result.load(node);
		} catch (IllegalAccessException e) {
			throw RepositoryException.throwIt(e) ;
		} catch (NoSuchMethodException e) {
			throw RepositoryException.throwIt(e) ;
		} catch (InvocationTargetException e) {
			throw RepositoryException.throwIt(e) ;
		} catch (InstantiationException e) {
			throw RepositoryException.throwIt(e) ;
		}
	}

	public boolean save(T p) {
		IDRow<T> idRow = getIDRow(p);

		Node foundNode = session.createQuery().findOneInDB(idRow.getAradonQuery().getGroupId(), idRow.getAradonQuery().getUId());
		if (foundNode == null) {
			foundNode = NodeImpl.create(wsname, p.getNodeObject(), idRow.getGroupNm(), p.getString(idRow.getKey()));
			foundNode.setAradonId(idRow.getAradonQuery().getGroupId(), idRow.getAradonQuery().getUId());
		}

		return session.createQuery().eq(idRow.getKey(), idRow.getValue()).updateOne(foundNode.toPropertyMap()) ;
	}

	public Node toNode(T p) {
		return NodeImpl.load(wsname, p.getNodeObject());
	}

	protected Session getSession() {
		return session;
	}

	protected IDRow<T> getIDRow(T p) {
		try {
			IDRow<T> idrow = null;
			Method[] methods = p.getClass().getDeclaredMethods();
			for (Method method : methods) {
				IDMethod idm = method.getAnnotation(IDMethod.class);
				if (idm != null) {
					idrow = IDRow.create(p, idm.nodeName(), method.invoke(p, new Object[0]));
				}
			}

			if (idrow == null) {
				throw new IllegalArgumentException(p + " has not id object");
			}

			return idrow;
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e.getCause());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getCause());
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e.getCause());
		}
	}

	public SessionQuery createQuery() {
		return session.createQuery() ; 
	}

}

class IDRow<T extends AbstractORM> {

	private final T obj;
	private final String key;
	private final Object value;
	private final AradonQuery query;

	private IDRow(T obj, String key, Object value) {
		this.obj = obj;
		this.key = key;
		this.value = value;
		this.query = AradonQuery.newByGroupId(obj.getClass().getSimpleName(), value);
	}

	public final static <T extends AbstractORM> IDRow<T> create(T obj, String key, Object value) {
		return new IDRow<T>(obj, key, value);
	}

	public String getGroupNm() {
		return obj.getClass().getSimpleName();
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public AradonQuery getAradonQuery() {
		return query;
	}

}
