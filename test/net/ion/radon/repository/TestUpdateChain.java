package net.ion.radon.repository;

import java.util.List;

import org.apache.commons.collections.Closure;

import net.ion.framework.util.ChainMap;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
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
	
	public void testInList2() throws Exception {
		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin");
		session.newNode().setAradonId("emp", "hero").put("name", "hero");
		session.commit() ;
		
		session.createQuery().aradonGroupId("emp", "bleujin").updateChain().put("address", "seoul")
			.inlist("memos", MapUtil.chainKeyMap().put("subject", "ics").put("content", "ics6")).update() ;
		
		assertEquals(2, session.createQuery().find().count()) ;
		assertEquals(1, session.createQuery().aradonGroupId("emp", "bleujin").findOne().inlist("memos").size()) ;
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
	
	
	public void testRemoveInlist() throws Exception {
		createSampleNode();
		
		session.createQuery().updateChain().put("address", "busan").removeInlist("friend", PropertyQuery.create().lte("age", 20)).update() ;
		
		Node found = session.createQuery().findOne() ;
		assertEquals("busan", found.getString("address")) ;
		assertEquals(1, ((InListNode)found.get("friend")).size()) ;
	}

	private void createSampleNode() {
		session.newNode().put("name", "bleujin").put("address", "seoul").put("view", 2)
			.inlist("friend").
				push(MapUtil.chainKeyMap().put("name", "jin").put("age", 25).put("city", "busan")).
				push(MapUtil.chainKeyMap().put("name", "hero").put("age", 20).put("city", "busan")).
				push(MapUtil.chainKeyMap().put("name", "novision").put("age", 20).put("city", "busan"));
		
		session.commit() ;
	}
	
	public void xtestUpdateInList() throws Exception {
		createSampleNode() ;
		//session.createQuery().updateChain().put("address", "busan").updateInlist("friend", PropertyQuery.create().lte("age", 20), MapUtil.chainKeyMap().put("city", "seoul")).update() ;
		
		Node node = session.createQuery().eq("name", "bleujin").findOne() ;
		updateInlist(node);

		List<InNode> innodes = node.inlist("friend").createQuery().find() ;
		assertEquals(3, innodes.size()) ;
		assertEquals("busan", innodes.get(0).getString("city")) ;
		assertEquals("seoul", innodes.get(1).getString("city")) ;
		assertEquals("seoul", innodes.get(2).getString("city")) ;
		
		
		
	}

	private void updateInlist(Node node) {
		final PropertyQuery inlistQuery = PropertyQuery.create().lte("age", 20);
		final String inlistKey = "friend";

		List<InNode> innodes = node.inlist(inlistKey).createQuery().addFilter(inlistQuery).find() ;
		
		UpdateChain updateChain = session.createQuery(node.getWorkspaceName()).id(node.getIdentifier()).updateChain();
		updateChain.removeInlist(inlistKey, inlistQuery).update() ;

		updateChain = session.createQuery(node.getWorkspaceName()).id(node.getIdentifier()).updateChain();
		for (InNode innode : innodes) {
			innode.put("city", "seoul") ;
			updateChain.inlist(inlistKey, MapUtil.chainKeyMap().put(innode.toMap())) ;
		}
		updateChain.update() ;
	}
	
	public void testInlistPullWhenNotExist() throws Exception {
		session.newNode().put("name", "bleujin").put("address", "seoul").put("view", 2);
		
		session.commit() ;
		
		session.createQuery().updateChain().put("address", "busan").removeInlist("friend", PropertyQuery.create().lte("age", 20)).update() ;
		Node found = session.createQuery().findOne() ;
		assertEquals("busan", found.getString("address")) ;
	}
	
	
	public void testMerge() throws Exception {
		NodeResult nr = session.createQuery().aradonGroupId("emp", "bleujin").updateChain().put("address", "busan").put("age", 20).merge() ;
		
		session.createQuery().find().debugPrint(PageBean.ALL) ;
		
		Node found = session.createQuery().findOne() ;
		assertEquals("busan", found.getString("address")) ;
		assertEquals("emp", found.getAradonId().getGroup()) ;
		assertEquals("bleujin", found.getAradonId().getUid()) ;
		
		assertEquals(false, found.isNew()) ;
	}
	
	public void testUpdate() throws Exception {
		session.newNode().put("ename", "bleujin").put("index", 1) ;
		session.commit() ;
		for (int i : ListUtil.rangeNum(5)) {
			session.createQuery().eq("ename", "bleujin").updateChain().put("index", i).update() ;
		}
		Node found = session.createQuery().findOne() ;
		assertEquals(4, found.getAsInt("index")) ;
	}

	public void testUpdateMerge() throws Exception {
		session.newNode().put("ename", "bleujin").put("index", 1) ;
		session.commit() ;
		for (int i : ListUtil.rangeNum(5)) {
			session.createQuery().eq("ename", "bleujin").updateChain().put("index", i).merge() ;
		}
		Node found = session.createQuery().findOne() ;
		assertEquals(4, found.getAsInt("index")) ;
	}


	public void testMerge2() throws Exception {
		for (int i : ListUtil.rangeNum(5)) {
			session.createQuery().eq("emp", i).updateChain().put("address", "busan").put("index", i).put("d" + i, "..").merge() ;
		}
		session.createQuery().find().debugPrint(PageBean.ALL) ;
		assertEquals(5, session.createQuery().find().count()) ;
	}
	
}



