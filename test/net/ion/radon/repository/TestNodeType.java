package net.ion.radon.repository;

import java.util.List;
import java.util.Map;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;

import org.apache.commons.collections.Closure;

public class TestNodeType extends TestBaseRepository{

	
	public void testRestrict() throws Exception {
		session.newNode().put("first", "first").put("second", "second").inner("location").put("x", 1).put("y", 2);
		session.commit() ;
		
		NodeCursor nc = session.createQuery().find() ;
		
		
		MyNodeType clo = new MyNodeType();
		nc.each(PageBean.ALL, clo) ;
		Debug.debug(clo.getNodeListMap() ) ;;
	}
}


class MyNodeType implements Closure {

	private List<Map<String, Object>> result = ListUtil.newList() ;

	public void execute(Object _node) {
		Node node = (Node)_node ;
		result.add(MapUtil.<String, Object>chainMap().put("first", node.getString("first")).toMap()) ; 
	}

	public List<Map<String, Object>> getNodeListMap(){
		return result ;
	}
	
}
