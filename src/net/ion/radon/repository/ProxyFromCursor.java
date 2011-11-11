package net.ion.radon.repository;

import java.util.List;
import java.util.Map;

import javax.swing.plaf.ListUI;

import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.mr.ReduceFormat;
import net.ion.radon.repository.orm.AbstractORM;
import net.ion.radon.repository.relation.IRelation;

import org.apache.commons.collections.Closure;

public class ProxyFromCursor implements NodeCursor{

	private NodeObject sort = NodeObject.create() ;
	private int limit = 0 ;
	private int skip = 0 ;

	private NodeCursor real ;
	private Node parent ;
	private final IRelation relation ;
	private ProxyFromCursor(Node parent, IRelation rel){
		this.parent = parent ;
		this.relation = rel ;
	} 
	
	public static NodeCursor create(Node parent, IRelation rel) {
		return new ProxyFromCursor(parent, rel);
	}
	
	public NodeCursor ascending(String... props) {
		for (String prop : props) {
			sort.put(prop, 1) ;
		}
		return this;
	}

	public NodeCursor descending(String... props) {
		for (String prop : props) {
			sort.put(prop, -1) ;
		}
		return this;
	}

	public PropertyQuery getQuery() {
		return relation.getQuery();
	}

	public NodeCursor limit(int limit) {
		this.limit = limit ;
		return this;
	}

	public NodeCursor skip(int skip) {
		this.skip = skip ;
		return this;
	}

	private static Integer ONE = new Integer(1) ;
	public NodeCursor sort(PropertyFamily family) {
		Map map = family.getDBObject().toMap() ;
		for (Object key : map.keySet()) {
			Object value = map.get(key) ;
			if (ONE.equals(value)){
				ascending(key.toString()) ;
			} else {
				descending(key.toString()) ;
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

	public <T> List<T> toList(PageBean page, Class<? extends AbstractORM> clz) {
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
			int maxTo = limit <= 0 ? relation.getRelation().size() : Math.min(relation.getRelation().size(), limit) ;
			List<Node> result = ListUtil.newList() ;
			for (int i = skip; i < maxTo ; i++) {
				result.add(relation.fetch(i)) ;
			}
			real = NodeListCursor.create(parent.getSession(), relation.getQuery(), result) ;
		} 
		return real ;
	}

}
