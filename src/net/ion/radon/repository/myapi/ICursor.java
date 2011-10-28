package net.ion.radon.repository.myapi;

import java.util.List;
import java.util.Map;

import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.PropertyComparator;

import org.apache.commons.collections.Closure;

public interface ICursor {

	public boolean hasNext()  ;

	public Node next() ;

	public int count() ;

	public ICursor skip(int n) ;

	public ICursor limit(int n) ;

	public List<Map<String, ? extends Object>> toMapList(PageBean page) ;	

	public List<Map<String, ? extends Object>> toPropertiesList(PageBean page) ;	

	public NodeScreen screen(PageBean page) ;
	
	public void debugPrint(PageBean page) ;

	public void each(PageBean page, Closure closure) ;
	
	public List<Node> toList(PageBean page);

	public List<Node> toList(PageBean page,  PropertyComparator comparator);

}
