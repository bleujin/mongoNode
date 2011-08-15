package net.ion.radon.repository;

import java.util.List;

import net.ion.radon.core.PageBean;

public class TestSort extends TestBaseRepository{

	
	public void testSort() throws Exception {
		initNode();
		
		PropertyQuery q1 = PropertyQuery.create("name", "heeya");
		PropertyQuery q2 = PropertyQuery.create("name", "bleujin");
		List<Node> result = createQuery().or(q1, q2).eq("color", 1).ascending("Name").find().toList(PageBean.ALL);   
		
		assertEquals("bleujin" + "blue", result.get(0).getString("name") + result.get(0).getString("color")) ;
		assertEquals("bleujin" + "red", result.get(1).getString("name") + result.get(1).getString("color")) ;
		assertEquals("heeya" + "black", result.get(2).getString("name") + result.get(2).getString("color")) ;
		assertEquals("heeya" + "white", result.get(3).getString("name") + result.get(3).getString("color")) ;
	}

	public void testSort2() throws Exception {
		initNode() ;

		NodeCursor nc = session.createQuery().eq("name", "bleujin").ascending("name").descending("age").find() ;
		nc.debugPrint(PageBean.ALL) ;
		
		session.createQuery().ascending("age").find().debugPrint(PageBean.ALL) ;
		session.createQuery().lt("age", 10).find().debugPrint(PageBean.ALL) ;
		
	}

	private void initNode() {
		session.newNode().put("name", "bleujin").put("color", "red").put("age", 11);
		session.newNode().put("name", "heeya").put("color", "black").put("age", 9);
		session.newNode().put("name", "heeya").put("color", "white").put("age", 10);
		session.newNode().put("name", "bleujin").put("color", "blue").put("age", 8);
		
		session.commit();
	}
}
