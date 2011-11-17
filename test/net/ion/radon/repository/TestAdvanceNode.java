package net.ion.radon.repository;

import java.util.Collections;
import java.util.List;

import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.ICursor;

public class TestAdvanceNode extends TestBaseRepository{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createSample() ;
	}
	
	public void testCompare() throws Exception {
		ICursor nc =  session.createQuery().eq("name", "root").findOne().getChild();
		// nc.toList(PageBean.ALL, PropertyFamily.create("color", -1).put("name", 1)) ;
		
		List<Node> list = nc.toList(PageBean.ALL) ;
		
		Collections.sort(list, PropertyComparator.newAscending("color")) ;
		assertEquals("black", list.get(0).getString("color")) ;
		Collections.sort(list, PropertyComparator.newDescending("color")) ;
		assertEquals("white", list.get(0).getString("color")) ;
	}

	public void testTwoCompare() throws Exception {
		ICursor nc =  session.createQuery().eq("name", "root").findOne().getChild();
		// nc.toList(PageBean.ALL, PropertyFamily.create("color", -1).put("name", 1)) ;
		
		List<Node> list = nc.toList(PageBean.ALL) ;
		
		Collections.sort(list, PropertyComparator.newAscending("color").ascending("age")) ;
		assertEquals(1, list.get(1).get("age")) ;
	}
	
	public void testListCompare() throws Exception {
		ICursor nc =  session.createQuery().eq("name", "root").findOne().getChild();
		List<Node> list = nc.toList(PageBean.ALL, PropertyComparator.newAscending("color").descending("age")) ;
		assertEquals(1, list.get(1).get("age")) ;
		
		ICursor nc2 =  session.createQuery().eq("name", "root").findOne().getChild();
		List<Node> newList = nc2.toList(PageBean.ALL, PropertyComparator.newAscending("color").ascending("age")) ;
		assertEquals(1, newList.get(1).get("age")) ;
	}

	
	public void testInListCompare() throws Exception {
		Node node = session.newNode().put("name", "bleujin") ;
		node.inlist("comfr").push(MapUtil.chainMap().put("name", "novision").put("age", 20))
			.push(MapUtil.chainMap().put("name", "iihi").put("age", 30)) 
			.push(MapUtil.chainMap().put("name", "pm1200").put("age", 40));
		
		List<InNode> ilist = node.inlist("comfr").createQuery().find() ;
		Collections.sort(ilist, PropertyComparator.newDescending("age")) ;
		
		
		assertEquals("pm1200", ilist.get(0).getString("name")) ;
		assertEquals("novision", ilist.get(2).getString("name")) ;
	}
	
	
	
	
	
	public void testEncrypt() throws Exception {
		session.newNode().put("id", "bleujin").putEncrypt("password", "mypwd") ;
		session.commit() ;

		Node find = createQuery().eq("id", "bleujin").findOne() ;
		assertFalse(find.isMatchEncrypted("password", "other")) ;
		assertTrue(find.isMatchEncrypted("password", "mypwd")) ;
	}
	
	
	
	public void testIncrease() throws Exception {
		createQuery().eq("color", "red").increase("age")  ;
		assertEquals(2, createQuery().eq("color", "red").findOne().get("age")) ;
		
		createQuery().eq("color", "red").increase("age", 2)  ;
		assertEquals(4, createQuery().eq("color", "red").findOne().get("age")) ;
	}
	
	
	
	private void createSample() {
		Node root = session.newNode().put("name", "root") ;
		session.commit() ;
		
		root.createChild("red").put("color", "red").put("age", 1) ;
		root.createChild("black").put("color", "black").put("age", 2) ;
		root.createChild("white").put("color", "white").put("age", 4) ;
		
		session.commit() ;
	}

}
