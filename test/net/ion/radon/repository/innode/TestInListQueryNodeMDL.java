package net.ion.radon.repository.innode;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.PropertyQuery;

public class TestInListQueryNodeMDL extends TestBaseInListQuery{

	public void testPushDefault() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.commit() ;
		
		session.createQuery().eq("name", "bleujin").inlist("people").push(MapUtil.create("color", "red")) ;
		session.createQuery().eq("name", "bleujin").inlist("people").push(MapUtil.create("color", "bleu")) ;

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
	

	public void testInListUpdate() throws Exception {
		Node node = session.newNode().put("name", "bleujin") ;
		node.inlist("hobby").push(MapUtil.chainMap().put("name", "promodel")) ;
		node.inlist("hobby").push(MapUtil.chainMap().put("name", "navy")) ;
		
		session.commit() ;
		
		NodeResult result = session.createQuery().eq("name", "bleujin").inlist("hobby").update(PropertyQuery.create("name", "navy"), MapUtil.chainMap().put("name", "navy promodel").put("type", "navy")) ;
		assertEquals(1, result.getRowCount()) ;
		assertEquals(2, session.createQuery().findOne().inlist("hobby").createQuery().find().size()) ;
		
		session.createQuery().find().debugPrint(PageBean.ALL) ;
	}

	
	
	
	
}
