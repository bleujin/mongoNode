package net.ion.radon.repository;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.impl.util.DebugPrinter;
import net.ion.radon.repository.mr.ReduceFormat;
import net.ion.radon.repository.orm.NodeORM;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;

public class ProxyCursor implements NodeCursor{

	private NodeCursor real ;
	
	private final Session session ;
	private final PropertyQuery inner ;
	private final String mapFunction ;
	private final String reduceFunction;
	private final String finalFunction;
	private final Workspace workspace;
	private final CommandOption options ;
	
	private ProxyCursor(Session session, PropertyQuery inner, String mapFunction, String reduceFunction, String finalFunction, Workspace workspace, CommandOption options){
		this.session = session ;
		this.inner = inner ;
		this.mapFunction = mapFunction ;
		this.reduceFunction = reduceFunction ;
		this.finalFunction = finalFunction ;
		this.workspace = workspace ;
		this.options = options ;
	} 
	
	
	public static NodeCursor create(Session session, PropertyQuery inner, String mapFunction, String reduceFunction, String finalFunction, Workspace workspace) {
		return create(session, inner, mapFunction, reduceFunction, finalFunction, workspace, CommandOption.create());
	}
	
	public static NodeCursor create(Session session, PropertyQuery inner, String mapFunction, String reduceFunction, String finalFunction, Workspace workspace, CommandOption options) {
		return new ProxyCursor(session, inner, mapFunction, reduceFunction, finalFunction, workspace, options);
	}
	
	public static NodeCursor format(Session session, PropertyQuery inner, ReduceFormat format, Workspace workspace) {
		return create(session, inner, format.getMap(), format.getReduce(), format.getFinalize(), workspace, CommandOption.create());
	}
	
	public static NodeCursor group(Session session, PropertyQuery inner, IPropertyFamily keys, IPropertyFamily initial, String reduce, Workspace workspace) {
		return NodeListCursor.create(session, inner, workspace.group(session, keys, inner, initial, reduce)) ;
	}
	
	public NodeCursor ascending(String... propIds) {
		options.ascending(propIds) ;
		return this;
	}

	public NodeCursor descending(String... propIds) {
		options.descending(propIds) ;
		return this;
	}

	public PropertyQuery getQuery() {
		return inner;
	}

	public NodeCursor limit(int n) {
		options.setLimit(n) ;
		return this;
	}

	public NodeCursor skip(int n) {
		options.setSkip(n) ;
		return this;
	}

	private static Integer ONE = new Integer(1) ;
	public NodeCursor sort(PropertyFamily family) {
		Map map = family.getDBObject().toMap() ;
		
		for (Object key : map.keySet()) {
			Object value = map.get(key) ;
			if (ONE.equals(value)){
				options.ascending(key.toString()) ;
			} else {
				options.descending(key.toString()) ;
			}
		}
		
		return this;
	}

	
	
	
	
	
	
	
	
	public int count() {
		return createReal().count();
	}

	public void debugPrint(PageBean page) {
		createReal().debugPrint(page) ;
	}

	public void each(PageBean page, Closure closure) {
		createReal().each(page, closure) ;
	}

	public Explain explain() {
		return createReal().explain();
	}


	public boolean hasNext() {
		return createReal().hasNext();
	}


	public Node next() {
		return createReal().next();
	}

	public NodeScreen screen(PageBean page) {
		return createReal().screen(page);
	}


	public List<Node> toList(PageBean page) {
		return createReal().toList(page);
	}

	public List<Node> toList(PageBean page, PropertyComparator comparator) {
		return createReal().toList(page, comparator);
	}

	public <T> List<T> toList(PageBean page, Class<? extends NodeORM> clz) {
		return createReal().toList(page, clz);
	}

	public List<Map<String, ? extends Object>> toMapList(PageBean page) {
		return createReal().toMapList(page);
	}

	public List<Map<String, ? extends Object>> toPropertiesList(PageBean page) {
		return createReal().toPropertiesList(page);
	}

	private synchronized NodeCursor createReal(){
		if (real == null){
			real = workspace.mapreduce(session, mapFunction, reduceFunction, finalFunction, options, inner);
		} 
		return real ;
	}

	public <T> List<T> toList(Class<T> clz, PageBean page) {
		return createReal().toList(clz, page);
	}


}


class NodeListCursor implements NodeCursor{

	private List<Node> datas ;
	private int fromindex ;
	private int toindex ;
	private PropertyFamily sort ;
	private Explain exp ;
	private PropertyQuery query ;
	
	private int current = 0 ;
	
	private NodeListCursor(Session session, PropertyQuery query, List<Node> datas) {
		this.datas = datas ;
		this.fromindex = 0 ;
		this.toindex = datas.size() ;
		this.sort = PropertyFamily.create() ;
		this.exp = session.getAttribute(Explain.class.getCanonicalName(), Explain.class) ;
		this.query = query ;
	}
	
	
	private List<Node> subList(PageBean page){
		Collections.sort(datas, PropertyComparator.create(sort)) ;
		
		return page.subList(datas.subList(fromindex, toindex)) ;
	}

	public static NodeCursor create(Session session, PropertyQuery query, List<Node> datas) {
		return new NodeListCursor(session, query, datas);
	}


	public NodeCursor ascending(String... propIds) {
		for (String prop : propIds) {
			sort.put(prop, 1) ;
		}
		return this;
	}

	public NodeCursor descending(String... propIds) {
		for (String prop : propIds) {
			sort.put(prop, -1) ;
		}
		return this;
	}
	
	public int count() {
		return subList(PageBean.ALL).size();
	}

	public NodeCursor skip(int skip) {
		this.fromindex = skip ;
		this.current = skip ;
		return this;
	}

	public void debugPrint(PageBean page) {
		each(page, new DebugPrinter());
	}

	public NodeCursor limit(int limit) {
		this.toindex = this.fromindex + limit ;
		return this ;
	}

	public void each(PageBean page, Closure closure) {
		CollectionUtils.forAllDo(toList(page), closure);
	}

	
	
	
	
	
	
	public Explain explain() {
		return exp;
	}

	public PropertyQuery getQuery() {
		return query;
	}

	public List<Node> toList(PageBean page) {
		return subList(page);
	}
	
	public NodeCursor sort(PropertyFamily pf) {
		for (Entry<String, ? extends Object> entry : pf.toMap().entrySet()) {
			sort.put(entry.getKey(), entry.getValue()) ;
		}
		return this;
	}
	
	
	
	
	public boolean hasNext() {
		return current < toindex;
	}


	public Node next() {
		return datas.get(current++);
	}

	public NodeScreen screen(PageBean page) {
		List<Node> pageNode = this.toList(page);
		return NodeScreen.create(count(), pageNode, page);
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

		List<Map<String, ?>> result = new ArrayList<Map<String, ?>>();
		for (Node node : list) {
			result.add(node.toMap());
		}
		return result;
	}

	public List<Map<String, ? extends Object>> toPropertiesList(PageBean page) {
		List<Node> list = toList(page);

		List<Map<String, ?>> result = new ArrayList<Map<String, ?>>();
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