package net.ion.radon.repository;

import net.ion.framework.util.MapUtil;

public class TestTemporaryNode extends TestBaseRepository {

	public void testCreateTempNode() throws Exception {
		TempNode tnode = session.tempNode().put("name", "bleujin") ;
		int count = session.commit() ; // not saved ;
		
		assertEquals(0, count) ;
	}
	
	
	public void testSetGet() throws Exception {
		TempNode tnode = session.tempNode().put("name", "bleujin") ;
		tnode.inner("address").put("city", "seoul").put("gibun", 3) ;
		
		assertEquals("seoul", tnode.get("address.city")) ;
	}
	
	
	public void testAppend() throws Exception {
		TempNode tnode = session.tempNode().append("color", "red").append("color", "red").append("address", "seoul") ;
		
		assertEquals(true, tnode.get("color") instanceof InListNode) ;
		assertEquals(true, tnode.get("address") instanceof InListNode) ;
		
		assertEquals(true, tnode.inlist("color").get(0) instanceof String) ;
		assertEquals("red", tnode.inlist("color").get(0)) ;
		
	}
	
	public void testMerge() throws Exception {
		TempNode tnode = session.tempNode().put("name", "bleujin") ;
		tnode.inner("address").put("city", "seoul").put("gibun", 3) ;
		
		NodeResult nr = session.merge(MergeQuery.createByAradon("emp", "bleujin"), tnode) ;
		
		assertEquals(1, nr.getRowCount()) ;
		assertEquals("bleujin", session.createQuery().findOne().getString("name")) ;
	}
	
	
	public void testGetParent() throws Exception {
		Node newNode = session.newNode().put("name", "bleujin").inner("address").put("city", "seoul").put("gibun", 3).getParent() ;
		TempNode tnode = session.tempNode().put("name", "bleujin").inner("address").put("city", "seoul").put("gibun", 3).getParent() ;
		
		session.clear() ;
	}
	
	public void testMergeId() throws Exception {
		String id = session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").getIdentifier() ;
		session.commit() ;
		
		session.merge(id, session.tempNode().put("name", "hero").inner("address").put("city", "seoul").put("gibun", 3).getParent() ) ;
		
		Node found = session.createQuery().findOne() ;
		
		assertEquals("emp", found.getAradonId().getGroup()) ;
		assertEquals("bleujin", found.getAradonId().getUid()) ;
		assertEquals("seoul", found.getString("address.city")) ;
		assertEquals("hero", found.getString("name")) ;
	}
	
	
	
	public void testMergeAradonId() throws Exception {
		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin") ;
		session.commit() ;
		
		TempNode tnode = session.tempNode().put("name", "hero").inner("address").put("city", "seoul").put("gibun", 3).getParent() ;
		NodeResult nr = session.merge(MergeQuery.createByAradon("emp", "bleujin"), tnode) ;
		
		assertEquals(1, nr.getRowCount()) ;
		assertEquals(1, session.createQuery().count()) ;
		assertEquals("hero", session.createQuery().findOne().getString("name")) ;
		assertEquals("seoul", session.createQuery().findOne().getString("address.city")) ;
		assertEquals("emp", session.createQuery().findOne().getAradonId().getGroup()) ;
		assertEquals("bleujin", session.createQuery().findOne().getAradonId().getUid()) ;
		
	}
	
	public void testMergePath() throws Exception {
		Node bleujin = session.newNode("bleujin") ;
		bleujin.put("id", "bleujin") ;
		bleujin.put("greeting", "hello") ;
		bleujin.put("loc", "seoul") ;

		session.commit() ;
		
		session.merge(MergeQuery.createByPath("/bleujin"), session.tempNode().put("age", 20).put("name", "hello").put("greeting", "hi!").inner("address").put("city", "seoul").getParent()) ;
		
		assertEquals(1, session.createQuery().count()) ;
		assertEquals(20, session.createQuery().path("/bleujin").findOne().getAsInt("age")) ;
		assertEquals("bleujin", session.createQuery().path("/bleujin").findOne().getString("id")) ;
		assertEquals("hello", session.createQuery().path("/bleujin").findOne().getString("name")) ;
		assertEquals("hi!", session.createQuery().path("/bleujin").findOne().getString("greeting")) ;
		assertEquals("seoul", session.createQuery().path("/bleujin").findOne().getString("address.city")) ;
		
	}
	
	public void testMergtPath2() throws Exception {
		session.merge("/emp/bleujin", session.tempNode().put("age", 20).put("name", "hello").inner("address").put("city", "seoul").getParent()) ;
		
		
		Node found = session.createQuery().path("/emp/bleujin").findOne() ;
		assertEquals("/emp/bleujin", found.getPath()) ;
		assertEquals("seoul", found.getString("address.city")) ;
		assertEquals("/emp/bleujin", session.createQuery().id(found.getIdentifier()).findOne().getPath()) ;
		
		assertEquals(true, found.getParent() == session.getRoot()) ;
	}
	
	
	public void testInListOverwrite() throws Exception {
		Node node = session.newNode("hello").put("name", "hello") ;
		node.inlist("child").push(MapUtil.chainMap().put("name", "hi")) ;
		
		session.commit() ;
		
		TempNode tnode = session.tempNode().put("name", "bleujin").put("age", 20) ;
		tnode.inlist("child").push(MapUtil.chainMap().put("address", "seoul")) ;
		NodeResult nr = session.merge("/hello", tnode) ;

		assertEquals(1, nr.getRowCount()) ;
		Node found = session.createQuery().findOne() ;
		
		InNode in = (InNode) found.inlist("child").get(0) ;
		assertEquals("seoul", in.getString("address")) ;
		
	}
	
	
}
