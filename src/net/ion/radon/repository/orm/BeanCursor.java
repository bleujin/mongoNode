package net.ion.radon.repository.orm;

import java.util.List;

import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;

public class BeanCursor<T extends AbstractORM> {

	private NodeCursor nc;
	private AbstractManager<T> man;

	private BeanCursor(NodeCursor nc, AbstractManager<T> man) {
		this.nc = nc;
		this.man = man;
	}

	public static <T extends AbstractORM> BeanCursor<T> create(NodeCursor nc, AbstractManager<T> abstractManager) {
		return new BeanCursor<T>(nc, abstractManager);
	}

	public boolean hasNext() {
		return nc.hasNext();
	}

	public T next() {
		Node node = nc.next();
		return man.toBean(node);
	}

	public int count() {
		return nc.count();
	}

	public BeanCursor<T> ascending(String... propIds) {
		nc.ascending(propIds) ;
		return this;
	}

	public BeanCursor<T> descending(String... propIds) {
		nc.descending(propIds) ;
		return this;
	}

	public List<T> toList(PageBean page) {
		IDMethod idm = man.getClass().getAnnotation(IDMethod.class);
		return nc.toList(page, idm.managerClz()) ;
	}

}
