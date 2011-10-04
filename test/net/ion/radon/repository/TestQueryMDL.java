package net.ion.radon.repository;

import net.ion.framework.util.MapUtil;

public class TestQueryMDL extends TestBaseRepository{

	public void testQueryRemove() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.commit() ;
		
		assertEquals(1, session.createQuery().eq("name", "bleujin").find().count()) ;
		assertEquals(1, session.createQuery().eq("name", "bleujin").remove()) ;
		assertEquals(0, session.createQuery().eq("name", "bleujin").find().count()) ;
	}
	
	
	public void testQueryUpdate() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.newNode().put("name", "hero") ;
		session.newNode().put("name", "jin") ;
		session.commit() ;

		assertEquals(1, session.createQuery().eq("name", "bleujin").find().count()) ;
		assertEquals(1, session.createQuery().eq("name", "bleujin").update(MapUtil.chainMap().put("ADDress", "seoul")).getRowCount()) ;
		
		assertEquals(1, session.createQuery().eq("address", "seoul").find().count()) ;
	}
}
