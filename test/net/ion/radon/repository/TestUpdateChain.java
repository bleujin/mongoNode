package net.ion.radon.repository;

import org.apache.commons.collections.Closure;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;

public class TestUpdateChain extends TestBaseRepository{

	public void testUpdateChain() throws Exception {
		session.newNode().put("name", "bleujin").put("viewcount", 3).put("address", "seoul") ;
		session.newNode().put("name", "hero").put("viewcount", 3).put("address", "inchon") ;
		session.commit() ;
		
		NodeResult nr = session.createQuery().updateChain().put("address", "busan").put("age", 20).update() ;
		assertEquals(2, nr.getRowCount()) ;
		
		NodeCursor nc = session.createQuery().find() ;
		nc.each(PageBean.ALL, new Closure() {
			public void execute(Object _node) {
				Node node = (Node) _node ;
				assertEquals("busan", node.get("address")) ;
				assertEquals(20, node.get("age")) ;
			}
		}) ;
	}
	
	public void testInc() throws Exception {
		session.newNode().put("name", "bleujin").put("viewcount", 4).put("addresss", "seoul") ;
		session.commit() ;
		
		NodeResult nr = session.createQuery().updateChain().put("Address", "busan").put("age", 20).inc("viewcount", 2).update() ;
		assertEquals(1, nr.getRowCount()) ;
		
		Node found = session.createQuery().findOne() ;
		assertEquals("busan", found.get("address")) ;
		assertEquals(20, found.get("age")) ;
		assertEquals(6, found.get("viewcount")) ;
		
	}
	
	public void testIncWhenNotFound() throws Exception {
		session.newNode().put("name", "bleujin").put("address", "seoul") ;
		session.commit() ;
		
		NodeResult nr = session.createQuery().updateChain().put("address", "busan").put("age", 20).inc("ViewCount", 2).update() ;
		assertEquals(1, nr.getRowCount()) ;
		
		Node found = session.createQuery().findOne() ;
		assertEquals("busan", found.get("address")) ;
		assertEquals(20, found.get("age")) ;
		assertEquals(2, found.get("viewcount")) ;
	}

	
	public void testInner() throws Exception {
		session.newNode().put("name", "bleujin").inner("address").put("city", "seoul").put("juso", "bun") ;
		session.commit() ;
		
		session.createQuery().updateChain().put("address.city", "busan").update() ;
		Node found = session.createQuery().findOne() ;
		assertEquals("busan", found.get("address.city")) ;
	}
	
	

	public void testPush() throws Exception {
		session.newNode().put("name", "bleujin").append("city", "seoul").put("juso", "bun") ;
		session.commit() ;
		
		NodeResult nr = session.createQuery().updateChain().push("city", "busan").update() ;
		assertEquals(1, nr.getRowCount()) ;
		
		Node found = session.createQuery().findOne() ;
		assertEquals("seoul", found.getString("city")) ;
		assertEquals(2, ((InListNode)found.get("city")).size()) ;
		assertEquals("seoul", ((InListNode)found.get("city")).get(0)) ;
		assertEquals("busan", ((InListNode)found.get("city")).get(1)) ;
	}
	
	public void testInList() throws Exception {
		session.newNode().put("name", "bleujin").put("address", "seoul").put("view", 2).inlist("friend").push(MapUtil.chainKeyMap().put("name", "hero").put("age", 20)) ;
		session.commit() ;
		
		session.createQuery().updateChain().put("address", "sungnam").push("city", "busan").inc("view", 2)
			.inlist("friend", MapUtil.chainKeyMap().put("name", "iihi").put("age", 25))
			.inlist("friend", MapUtil.chainKeyMap().put("name", "pm1200").put("age", 25))
			.inlist("friend", MapUtil.chainKeyMap().put("name", "novision").put("age", 30)).update() ;
		
		Node found = session.createQuery().findOne() ;
		Debug.line(found) ;
		
		assertEquals(4, found.get("view")) ;
		assertEquals("sungnam", found.get("address")) ;
		assertEquals("bleujin", found.get("name")) ;
		assertEquals("busan", found.getString("city")) ;
		assertEquals("busan", ((InListNode)found.get("city")).get(0)) ;
		InListNode friend = found.inlist("friend");
		
		assertEquals(4, friend.size()) ;
		assertEquals("hero", ((InNode)friend.get(0)).getString("name")) ;
		assertEquals("iihi", ((InNode)friend.get(1)).getString("name")) ;
		assertEquals("pm1200", ((InNode)friend.get(2)).getString("name")) ;
		
		
	}
	
	public void testInListWhenNotExist() throws Exception {
		session.newNode().put("name", "bleujin").put("address", "seoul").put("view", 2) ;
		session.commit() ;
		
		session.createQuery().updateChain().put("address", "sungnam").push("city", "busan").inc("view", 2)
			.inlist("friend", MapUtil.chainKeyMap().put("name", "iihi").put("age", 25))
			.inlist("friend", MapUtil.chainKeyMap().put("name", "pm1200").put("age", 25))
			.inlist("friend", MapUtil.chainKeyMap().put("name", "novision").put("age", 30)).update() ;
		
		Node found = session.createQuery().findOne() ;
		Debug.line(found) ;
		
		assertEquals(4, found.get("view")) ;
		assertEquals("sungnam", found.get("address")) ;
		assertEquals("bleujin", found.get("name")) ;
		assertEquals("busan", found.getString("city")) ;
		assertEquals("busan", ((InListNode)found.get("city")).get(0)) ;
		InListNode friend = found.inlist("friend");
		
		assertEquals(3, friend.size()) ;
		assertEquals("iihi", ((InNode)friend.get(0)).getString("name")) ;
		assertEquals("pm1200", ((InNode)friend.get(1)).getString("name")) ;
	}
	
	
	public void testInlistPull() throws Exception {
		session.newNode().put("name", "bleujin").put("address", "seoul").put("view", 2)
			.inlist("friend").
				push(MapUtil.chainKeyMap().put("name", "jin").put("age", 25)).
				push(MapUtil.chainKeyMap().put("name", "hero").put("age", 20)).
				push(MapUtil.chainKeyMap().put("name", "novision").put("age", 20));
		
		session.commit() ;
		
		session.createQuery().updateChain().put("address", "busan").removeInlist("friend", PropertyQuery.create().lte("age", 20)).update() ;
		
		Node found = session.createQuery().findOne() ;
		assertEquals("busan", found.getString("address")) ;
		assertEquals(1, ((InListNode)found.get("friend")).size()) ;
	}
	
	public void testInlistPullWhenNotExist() throws Exception {
		session.newNode().put("name", "bleujin").put("address", "seoul").put("view", 2);
		
		session.commit() ;
		
		session.createQuery().updateChain().put("address", "busan").removeInlist("friend", PropertyQuery.create().lte("age", 20)).update() ;
		Node found = session.createQuery().findOne() ;
		assertEquals("busan", found.getString("address")) ;
	}
	
	
	public void testMerge() throws Exception {
		session.createQuery().aradonGroupId("emp", "bleujin") .updateChain().put("address", "busan").removeInlist("friend", PropertyQuery.create().lte("age", 20)).merge() ;
		Node found = session.createQuery().findOne() ;
		assertEquals("busan", found.getString("address")) ;
		assertEquals("emp", found.getAradonId().getGroup()) ;
		assertEquals("bleujin", found.getAradonId().getUid()) ;
	}
}
