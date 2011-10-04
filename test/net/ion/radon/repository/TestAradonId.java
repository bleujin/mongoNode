package net.ion.radon.repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mongodb.DBObject;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.radon.repository.myapi.ICursor;

public class TestAradonId extends TestBaseRepository{

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Node node = session.newNode() ;
		node.put("name", "root") ;
		
		node.createChild("bleujin").put("color", "white").put("age", 3).setAradonId("dev.aradon.c", 5);
		node.createChild("hero").put("color", "red").put("age", 1).setAradonId("dev.aradon.c", 7) ;
		node.createChild("jin").put("color", "red").put("age", 2).setAradonId("dev.aradon.c", 10) ;
		node.createChild("jini").put("color", "black").put("age", 4).setAradonId("dev.aradon.c", 15) ;
		
		
		session.commit() ;
	}
	
	public void testNewNode() throws Exception {
		Node node = session.newNode().put("name", "bleujin") ;
		assertEquals(node.getIdentifier(), node.getAradonId().getUid()) ;
		
		node.setAradonId("my", 1234) ;
		assertEquals(1234, node.getAradonId().getUid()) ;
		
		session.commit() ;
		
		assertEquals("bleujin", session.createQuery().aradonGroupId("my", 1234).findOne().get("name")) ;
	}

	
	public void testFind() throws Exception {
		Debug.debug(session.createQuery().eq("color", "red").find(PageBean.ALL) ) ;
	}
	
	
	public void testPredefinedIndexCount() throws Exception {
		Debug.debug(session.getCurrentWorkspace().getIndexInfo()) ;
	
		assertEquals(3, session.getCurrentWorkspace().getIndexInfo().size()) ;
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
	
	
	
}
