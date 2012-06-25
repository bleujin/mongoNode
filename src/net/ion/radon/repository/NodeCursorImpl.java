package net.ion.radon.repository;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.Closure;
import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.impl.util.DebugPrinter;
import net.ion.radon.repository.orm.NodeORM;

import org.apache.commons.beanutils.ConstructorUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceOutput;

public class NodeCursorImpl implements NodeCursor {

	private Session session;
	private String workspaceName;
	private DBCursor cursor;
	private final PropertyQuery iquery;
	private int skip = 0;
	private int limit = 1000000;

	protected NodeCursorImpl(Session session, PropertyQuery iquery, String workspaceName, DBCursor cursor) {
		this.session = session;
		this.workspaceName = workspaceName;
		this.cursor = cursor;
		this.iquery = iquery;
	}

	static NodeCursorImpl create(Session session, PropertyQuery iquery, String workspaceName, DBCursor cursor) {
		return new NodeCursorImpl(session, iquery, workspaceName, cursor);
	}

	public boolean hasNext() {
		return cursor.hasNext();
	}

	public Node next() {
		return NodeImpl.load(session, iquery, workspaceName, cursor.next());
	}

	public int count() {
		return cursor.count();
	}

	public NodeCursor batchSize(int n) {
		cursor.batchSize(n);
		return this;
	}

	public NodeCursor skip(int n) {
		if (n > 0) {
			cursor.skip(n);
			this.skip = n;
		}
		return this;
	}

	public NodeCursor limit(int n) {
		cursor.limit(n);
		this.limit = n;
		return this;
	}

	public NodeCursor sort(PropertyFamily family) {
		cursor.sort(family.getDBObject());
		return this;
	}

	public NodeCursor ascending(String... propIds) {
		PropertyFamily pf = PropertyFamily.create();
		for (String propId : propIds) {
			pf.put(propId, 1);
		}
		return sort(pf);
	}

	public NodeCursor descending(String... propIds) {
		PropertyFamily pf = PropertyFamily.create();
		for (String propId : propIds) {
			pf.put(propId, -1);
		}
		return sort(pf);
	}

	// public int size() {
	// return cursor.size();
	// }

	public List<Node> toList(PageBean page) {
		this.skip(this.skip + page.getSkipScreenCount()).limit(Math.min(page.getMaxScreenCount() + 1, this.limit));
		return toList(page.getPageIndexOnScreen() * page.getListNum(), page.getListNum());
	}

	private List<Node> toList(int skip, int limit) {
		while (skip-- > 0) {
			if (cursor.hasNext()) {
				cursor.next();
			} else {
				return ListUtil.EMPTY;
			}
		}

		List<Node> result = ListUtil.newList();
		while (limit-- > 0 && cursor.hasNext()) {
			result.add(next());
		}

		return result;
	}

	public List<Map<String, ? extends Object>> toMapList(PageBean page) {
		List<Node> list = toList(page);

		List<Map<String, ?>> result = ListUtil.newList();
		for (Node node : list) {
			result.add(node.toMap());
		}
		return result;
	}

	public List<Map<String, ? extends Object>> toPropertiesList(PageBean page) {
		List<Node> list = toList(page);

		List<Map<String, ?>> result = ListUtil.newList();
		for (Node node : list) {
			result.add(node.toPropertyMap());
		}

		return result;
	}

	public NodeScreen screen(PageBean page) {
		List<Node> pageNode = this.toList(page);
		return NodeScreen.create(count(), pageNode, page);
	}

	public void debugPrint(PageBean page) {
		each(page, new DebugPrinter());
	}

	public void each(PageBean page, Closure closure) {
		CollectionUtil.each(toList(page), closure);
	}

	public Explain explain() {
		return Explain.load(cursor.explain());
	}

	public PropertyQuery getQuery() {
		return PropertyQuery.load(NodeObject.load(cursor.getQuery()));
	}

	public List<Node> toList(PageBean page, PropertyComparator comparator) {
		List<Node> nodes = toList(page);
		Collections.sort(nodes, comparator);
		return nodes;
	}

	public <T> List<T> toList(PageBean page, Class<? extends NodeORM> clz) {
		try {

			List<T> result = ListUtil.newList();

			for (Node node : toList(page)) {
				NodeORM obj = clz.cast(ConstructorUtils.invokeConstructor(clz, new Object[0]));
				result.add((T) obj.load(node));
			}

			return result;
		} catch (IllegalAccessException e) {
			throw RepositoryException.throwIt(e);
		} catch (NoSuchMethodException e) {
			throw RepositoryException.throwIt(e);
		} catch (InvocationTargetException e) {
			throw RepositoryException.throwIt(e);
		} catch (InstantiationException e) {
			throw RepositoryException.throwIt(e);
		}
	}

	public <T> List<T> toList(Class<T> clz, PageBean page) {
		List<T> result = ListUtil.newList();
		for (Node node : toList(page)) {
			result.add(JsonParser.fromMap(node.toPropertyMap()).getAsObject(clz));
		}
		return result;
	}

	protected String getWorkspaceName() {
		return workspaceName;
	}

	protected DBObject nextIternal() {
		return cursor.next();
	}

}

class ApplyCursor implements NodeCursor {

	private Session session;
	private String workspaceName;
	private MapReduceOutput output;
	private Iterator<DBObject> iterator;
	private final PropertyQuery iquery;

	protected ApplyCursor(Session session, PropertyQuery iquery, String workspaceName, MapReduceOutput output) {
		this.session = session;
		this.workspaceName = workspaceName;
		this.output = output;
		this.iterator = output.results().iterator();
		this.iquery = iquery;
	}

	static ApplyCursor create(Session session, PropertyQuery iquery, MapReduceOutput out) {
		String workspaceName = out.getOutputCollection() == null ? null : out.getOutputCollection().getName();

		return new ApplyCursor(session, iquery, workspaceName, out);
	}

	public Node next() {
		DBObject dbo = iterator.next();
		return NodeImpl.load(session, iquery, workspaceName, (DBObject) dbo.get("value"));
	}

	public int count() {
		return -1;
	}

	public void debugPrint(PageBean page) {
		each(page, new DebugPrinter());
	}

	public NodeCursor ascending(String... propIds) {
		throw new IllegalStateException("already created : illegal state");
	}

	public NodeCursor descending(String... propIds) {
		throw new IllegalStateException("already created : illegal state");
	}

	public void each(PageBean page, Closure closure) {
		CollectionUtil.each(toList(page), closure);
	}

	public Explain explain() {
		DBObject dbo = new BasicDBObject();
		dbo.put("timeMillis", output.getRaw().get("timeMillis"));
		dbo.put("timing", output.getRaw().get("timing"));
		dbo.put("counts", output.getRaw().get("counts"));

		return Explain.load(dbo);
	}

	public PropertyQuery getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public NodeCursor limit(int n) {
		; // already applied
		return this;
	}

	public NodeScreen screen(PageBean page) {
		List<Node> pageNode = this.toList(page);
		return NodeScreen.create(pageNode.size(), pageNode, page);
	}

	public NodeCursor skip(int n) {
		; // already applied
		return this;
	}

	public NodeCursor sort(PropertyFamily family) {
		throw new IllegalStateException("already created : illegal state");
	}

	public List<Node> toList(PageBean page) {
		this.skip(page.getSkipScreenCount()).limit(page.getMaxScreenCount() + 1);
		return toList(page.getPageIndexOnScreen() * page.getListNum(), page.getListNum());
	}

	private List<Node> toList(int skip, int limit) {
		while (skip-- > 0) {
			if (iterator.hasNext()) {
				next();
			} else {
				return ListUtil.EMPTY;
			}
		}

		List<Node> result = new ArrayList<Node>();
		while (limit-- > 0 && hasNext()) {
			result.add(next());
		}
		return result;
	}

	public List<Node> toList(PageBean page, PropertyComparator comparator) {
		List<Node> nodes = toList(page);
		Collections.sort(nodes, comparator);
		return nodes;
	}

	public <T> List<T> toList(PageBean page, Class<? extends NodeORM> clz) {
		try {
			List<T> result = ListUtil.newList();
			for (Node node : toList(page)) {
				NodeORM obj = clz.cast(ConstructorUtils.invokeConstructor(clz, new Object[0]));
				result.add((T) obj.load(node));
			}

			return result;
		} catch (IllegalAccessException e) {
			throw RepositoryException.throwIt(e);
		} catch (NoSuchMethodException e) {
			throw RepositoryException.throwIt(e);
		} catch (InvocationTargetException e) {
			throw RepositoryException.throwIt(e);
		} catch (InstantiationException e) {
			throw RepositoryException.throwIt(e);
		}
	}

	public List<Map<String, ? extends Object>> toMapList(PageBean page) {
		List<Node> list = toList(page);

		List<Map<String, ?>> result = ListUtil.newList();
		for (Node node : list) {
			result.add(node.toMap());
		}
		return result;
	}

	public List<Map<String, ? extends Object>> toPropertiesList(PageBean page) {
		List<Node> list = toList(page);

		List<Map<String, ?>> result = ListUtil.newList();
		for (Node node : list) {
			result.add(node.toPropertyMap());
		}

		return result;
	}

	public <T> List<T> toList(Class<T> clz, PageBean page) {
		List<T> result = ListUtil.newList();
		for (Node node : toList(page)) {
			result.add(JsonParser.fromMap(node.toPropertyMap()).getAsObject(clz));
		}
		return result;
	}

}