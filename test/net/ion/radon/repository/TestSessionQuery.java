package net.ion.radon.repository;

import net.ion.framework.util.ChainMap;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;

public class TestSessionQuery extends TestBaseRepository{
	
	
	private String wname = "happy" ;
	public void setUp() throws Exception {
		super.setUp() ;
		session.changeWorkspace(wname) ;
		session.dropWorkspace() ;
		
	}
	
	public void testSessionName() throws Exception {
		assertEquals(wname, session.getCurrentWorkspaceName()) ;
	}
	
	public void testWorkspace() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.commit() ;
		
		assertEquals(1, session.createQuery().find().count()) ;
		
		session.changeWorkspace(WORKSPACE_NAME) ;
		assertEquals(0, session.createQuery().find().count()) ;
		
		
		assertEquals(1, session.createQuery(wname).find().count()) ;
		assertEquals(WORKSPACE_NAME, session.getCurrentWorkspaceName()) ;
		
	}
	
	
	public void testQueryAnd() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.newNode().put("name", "hero") ;
		session.commit() ;
		
		session.changeWorkspace(WORKSPACE_NAME) ;
		PropertyQuery query = PropertyQuery.create().eq("name", "bleujin") ;
		assertEquals(1, session.createQuery(wname).and(query).find().count()) ; 
		assertEquals(2, session.createQuery(wname).find().count()) ;
	}
	
	
	public void testCompositeUpdate() throws Exception {
		session.newNode().put("name", "bleujin").put("viewcount", 1).put("addresss", "seoul") ;
		session.commit() ;
		
		session.createQuery().eq("name", "bleujin").update(MapUtil.chainKeyMap().put("address", "busan").put("age", 20)) ;
		
		Node found = session.createQuery().findOne() ;
		
		assertEquals(1, found.get("viewcount")) ;
		assertEquals("busan", found.get("address")) ;
		assertEquals(20, found.get("age")) ;
	}
	
	public void testMerge() throws Exception {
		session.createQuery().aradonGroupId("emp", "bleujin").merge(MapUtil.chainMap().put("viewcount", 1) ) ;
		
		assertEquals(1, session.createQuery().find().count()) ;
		assertEquals(1, session.createQuery().aradonGroupId("emp", "bleujin").find().count()) ;
	}
	
	
	public void testIncrease() throws Exception {
		session.newNode().put("name", "bleujin").put("viewcount", 3).put("addresss", "seoul") ;
		session.commit() ;
		
		session.createQuery().eq("name", "bleujin").increase("viewcount", 2) ;
		
		Node found = session.createQuery().findOne() ;
		assertEquals(5, found.get("viewcount")) ;
	}
	
}
