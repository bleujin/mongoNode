package net.ion.radon.repository;

import java.util.List;
import java.util.Map;

import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.ICursor;
import net.ion.radon.repository.orm.AbstractORM;

import org.apache.commons.collections.Closure;

public interface NodeCursor extends ICursor {

	public boolean hasNext()  ;

	public Node next()  ;

	public int count()  ;

	public NodeCursor skip(int n)  ;

	public NodeCursor limit(int n)  ;

	public NodeCursor ascending(String... propIds) ;

	public NodeCursor descending(String... propIds) ;

	public List<Node> toList(PageBean page) ;

	public List<Map<String, ? extends Object>> toMapList(PageBean page) ;

	public List<Map<String, ? extends Object>> toPropertiesList(PageBean page) ;

	public NodeScreen screen(PageBean page) ;
	
	public void debugPrint(PageBean page) ;
	
	public void each(PageBean page, Closure closure) ;

	public Explain explain() ;

	public PropertyQuery getQuery() ;

	public List<Node> toList(PageBean page, PropertyComparator<Node> comparator) ;

	public <T> List<T> toList(PageBean page, Class<? extends AbstractORM> clz) ;

}
