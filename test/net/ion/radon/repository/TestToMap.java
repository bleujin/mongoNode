package net.ion.radon.repository;

import com.mongodb.DBObject;

import net.ion.framework.util.ChainMap;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.NumberUtil;

public class TestToMap extends TestBaseRepository{

	public void testInnerClass() throws Exception {
		INode node = session.newNode().put("name", "bleujin").append("color", 128).append("color", 134).inner("address").put("city", "seoul").getParent() ;

		for (Object value :  node.toMap().values()) {
			if (value instanceof DBObject) {
				fail() ;
			}
		} ;

		for (Object value :  node.toPropertyMap().values()) {
			if (value instanceof DBObject) {
				fail() ;
			}
		} ;
	}
	
	public void testTempInner() throws Exception {
		INode node = session.tempNode().put("name", "bleujin").append("color", 128).append("color", 134).inner("address").put("city", "seoul").getParent() ;
		for (Object value :  node.toMap().values()) {
			if (value instanceof DBObject) {
				fail() ;
			}
		} ;

		for (Object value :  node.toPropertyMap().values()) {
			if (value instanceof DBObject) {
				fail() ;
			}
		} ;
	}
	
	public void testGet() throws Exception {
		INode node = session.tempNode().put("name", "bleujin").append("color", 128).append("color", 134).inner("address").put("city", "seoul").getParent() ;
		
		assertEquals(134, node.get("color", 1));
		assertEquals("seoul", node.get("address.city"));
	}
	
	
	public void testInList() throws Exception {
		TempNode node = session.tempNode();
		InListNode in = node.put("name", "bleujin").append("color", 128).append("color", 134).inlist("people") ;
		for (int i = 0; i < 5 ; i++) {
			in.push(MapUtil.chainMap().put("index", 1).put("name", "hero")) ;
		}
		Debug.line(node.get("people", 1), node.get("people", 1).getClass()); 
		
		
		
	}
	
}
