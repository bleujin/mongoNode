package net.ion.radon.repository.innode;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.PropertyQuery;

public class TestInListNodeQuery extends TestBaseInListQuery{

	public void testPushInNode() throws Exception {
		Node node = session.newNode() ;
		node.put("name", "bleujin") ;
		session.commit() ;
		
		node.inlist("people").push(MapUtil.create("color", "red")) ;
		node.inlist("people").push(MapUtil.create("color", "bleu")) ;

		assertEquals(0, session.createQuery().findOne().inlist("people").createQuery().find().size()) ;
		session.commit() ;
		assertEquals(2, session.createQuery().findOne().inlist("people").createQuery().find().size()) ;
	}
	
	
	public void testPull() throws Exception {
		createNodes() ;
		assertEquals(2, session.createQuery().lte("oindex", 1).inlist("people").pull(PropertyQuery.create().eq("index", 0)).getRowCount());

		session.createQuery().eq("oindex", 2).findOne() ;
	}
	
	
	public void testPush() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.newNode().put("name", "hero");
		session.commit() ;
		
		session.createQuery().eq("name", "bleujin").inlist("greeting").push(MapUtil.create("eng", "hello")) ;
		session.createQuery().eq("name", "bleujin").inlist("greeting").push(MapUtil.create("kor", "¾È³ç")) ;
		
		assertEquals(2, session.createQuery().eq("name", "bleujin").findOne().inlist("greeting").createQuery().find().size()) ;
		assertEquals(0, session.createQuery().eq("name", "hero").findOne().inlist("greeting").createQuery().find().size()) ;
	}
	
	
	public void testMultiPush() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.newNode().put("name", "hero");
		session.commit() ;
		
		session.createQuery().inlist("greeting").push(MapUtil.create("eng", "hello")) ;
		
		assertEquals(1, session.createQuery().eq("name", "bleujin").findOne().inlist("greeting").createQuery().find().size()) ;
		assertEquals(1, session.createQuery().eq("name", "hero").findOne().inlist("greeting").createQuery().find().size()) ;
		Debug.debug(1, session.createQuery().eq("name", "hero").findOne().inlist("greeting").createQuery().findOne().get("eng")) ;
	}
	
	
	public void testCaseInSensitive() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.newNode().put("name", "hero");
		session.commit() ;
		
		session.createQuery().inlist("Greeting").push(MapUtil.create("ENG", "hello")) ;

		assertEquals(1, session.createQuery().eq("name", "bleujin").findOne().inlist("greeting").createQuery().find().size()) ;
		assertEquals(1, session.createQuery().eq("name", "bleujin").findOne().inlist("Greeting").createQuery().find().size()) ;
		Debug.debug(session.createQuery().eq("name", "bleujin").findOne().inlist("Greeting").createQuery().findOne()) ;
		assertEquals("hello", session.createQuery().eq("name", "bleujin").findOne().inlist("Greeting").createQuery().findOne().getString("Eng")) ;
	}
	
	
	public void testDepth() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.commit() ;
		session.createQuery().inlist("person").push(makeSampleJSON()) ;
		
		session.createQuery().find().debugPrint(PageBean.ALL) ;
		Node node = session.createQuery().findOne() ;
		assertEquals("bleujin", node.inlist("person").createQuery().findOne().get("name")) ;
		assertEquals("seoul", node.inlist("person").createQuery().findOne().get("address.city")) ;
		
	}
	
	public void testInListUpdate() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.commit() ;
		NodeResult result = session.createQuery().eq("name", "bleujin").update(MapUtil.chainMap().put("working", true)) ;
		
		Debug.line(result, result.getRowCount(), result.getErrorMessage()) ;
		
		session.createQuery().find().debugPrint(PageBean.ALL) ;
		
	}
	
	
	
}
