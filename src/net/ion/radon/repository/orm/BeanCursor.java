package net.ion.radon.repository.orm;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.impl.util.DebugPrinter;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;

public class BeanCursor<T extends NodeORM> {

	private NodeCursor nc;
	private IDMethod idm;
	private GenericManager<T> aman;

	private BeanCursor(NodeCursor nc, IDMethod idm, GenericManager<T> aman) {
		this.nc = nc;
		this.idm = idm;
		this.aman = aman;
	}

	public static <T extends NodeORM> BeanCursor<T> create(NodeCursor nc, IDMethod idm, GenericManager<T> aman) {
		return new BeanCursor<T>(nc, idm, aman);
	}

	public boolean hasNext() {
		return nc.hasNext();
	}

	public T next() {
		Node node = nc.next();
		return toBean(node);
	}

	public int count() {
		return nc.count();
	}

	public BeanCursor<T> ascending(String... propIds) {
		nc.ascending(propIds);
		return this;
	}

	public BeanCursor<T> descending(String... propIds) {
		nc.descending(propIds);
		return this;
	}

	private T toBean(Node node) throws RepositoryException {
		try {
			if (node == null)
				return null;

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

	public List<T> toList(PageBean page) {
		Class<? extends NodeORM> objClz = idm.managerClz();
		List<T> result = ListUtil.newList();

		for (Node node : nc.toList(page)) {
			result.add(toBean(node));
		}

		return result;
	}

	public void each(PageBean page, Closure closure) {
		CollectionUtils.forAllDo(toList(page), closure);
	}

	public void debugPrint(PageBean page) {
		each(page, new DebugPrinter()) ;
	}

}
