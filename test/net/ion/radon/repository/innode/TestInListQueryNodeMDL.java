package net.ion.radon.repository.innode;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.InListNode;
import net.ion.radon.repository.InNode;
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
	
	
	public void testPullAll() throws Exception {
		createNodes() ;
		Node before = session.createQuery().lte("oindex", 1).findOne() ;
		assertEquals(5, before.inlist("people").createQuery().find().size()) ;
		NodeResult nr = session.createQuery().lte("oindex", 1).inlist("people").pull() ;
		
		Node after = session.createQuery().lte("oindex", 1).findOne() ;
		assertEquals(0, after.inlist("people").createQuery().find().size()) ;
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
		node.inlist("hobby").push(MapUtil.chainKeyMap().put("name", "promodel").toMap()) ;
		node.inlist("hobby").push(MapUtil.chainKeyMap().put("name", "navy").toMap()) ;
		
		session.commit() ;
		
		NodeResult result = session.createQuery().eq("name", "bleujin").inlist("hobby").update(PropertyQuery.create("name", "navy"), MapUtil.chainMap().put("model", "navy promodel").put("type", "navy")) ;
		assertEquals(1, result.getRowCount()) ;
		assertEquals(2, session.createQuery().findOne().inlist("hobby").createQuery().find().size()) ;
		
		
		InListNode listnode = session.createQuery().findOne().inlist("hobby") ;
		assertEquals("promodel", ((InNode)listnode.get(0)).getString("name")) ;
		assertEquals("navy", ((InNode)listnode.get(1)).getString("type")) ;
		assertEquals("navy promodel", ((InNode)listnode.get(1)).getString("model")) ;
//		assertEquals("navy", ((InNode)listnode.get(1)).getString("name")) ;
		
		session.createQuery().find().debugPrint(PageBean.ALL) ;
	}

	
	
	
	
}
