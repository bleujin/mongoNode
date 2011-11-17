package net.ion.radon.repository;

import java.util.Collections;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.ICursor;

public class TestNode extends TestBaseRepository{

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Node node = session.newNode() ;
		node.put("name", "root") ;
		
		node.createChild("bleujin").put("color", "white").put("age", 3).setAradonId("dev.aradon.c", 5);
		node.createChild("hero").put("color", "red").put("age", 1).setAradonId("dev.aradon.c", 7) ;
		node.createChild("jin").put("color", "red").put("age", 2).setAradonId("dev.aradon.c", 10) ;
		node.createChild("jin").put("color", "black").put("age", 4).setAradonId("dev.aradon.c", 15) ;
		
		
		session.commit() ;
	}

	
	public void testFind() throws Exception {
		Debug.debug(session.createQuery().eq("color", "red").find(PageBean.ALL) ) ;
	}
	
	public void testPredefinedIndexCount() throws Exception {
		Debug.debug(session.getCurrentWorkspace().getIndexInfo()) ;
	
		assertEquals(2, session.getCurrentWorkspace().getIndexInfo().size()) ;
	}
	
	public void testAradonId() throws Exception {
		NodeCursor nc =  session.createQuery().aradonGroup("dev.aradon").find();
		assertEquals(4, nc.count());
	}
	
	public void testGroupPath() throws Exception {
		NodeCursor nc =  createQuery().aradonGroup("dev").find();
		assertEquals(4, nc.count());
	}

	public void testGroupAndId() throws Exception {
		NodeCursor nc =  createQuery().aradonGroupId("dev", 7).find();
		assertEquals(1, nc.count());
	}
	

	public void testAradonIdSort() throws Exception {
		NodeCursor nc =  session.createQuery().aradonGroup("dev.aradon").find();
		// nc.debugPrint(PageBean.ALL) ;
		assertEquals("black", nc.next().getString("color")) ;
	}
	
	public void testfindGroupUid() throws Exception {
		NodeCursor nc = createQuery().aradonGroupId("dev",15).find();
		
		assertEquals("black", nc.next().getString("color")) ;
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
		Collections.sort(list, PropertyComparator.newAscending("color").descending("age")) ;
		assertEquals(2, list.get(1).get("age")) ;
	}

	
	public void testListCompare() throws Exception {
		ICursor nc =  session.createQuery().eq("name", "root").findOne().getChild();
		List<Node> list = nc.toList(PageBean.ALL, PropertyComparator.newAscending("color").descending("age")) ;
		assertEquals(2, list.get(1).get("age")) ;
		
		ICursor nc2 =  session.createQuery().eq("name", "root").findOne().getChild();
		List<Node> newList = nc2.toList(PageBean.ALL, PropertyComparator.newAscending("color").ascending("age")) ;
		assertEquals(1, newList.get(1).get("age")) ;
	}


	
	public void testByteSave() throws Exception {
		Node node = session.newNode() ;
		
		node.put("name", "bleujin") ;
		node.putEncrypt("password", "mypwd") ;
		
		session.commit() ;

		Node find = createQuery().eq("name", "bleujin").findOne() ;
		
		assertFalse(find.isMatchEncrypted("password", "other")) ;
		assertTrue(find.isMatchEncrypted("password", "mypwd")) ;
	}
}
