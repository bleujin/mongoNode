package net.ion.radon.repository;

import net.ion.framework.util.MapUtil;

public class TestGetPath extends TestBaseRepository{

	
	public void testNormalPath() throws Exception {
		Session session = createSampleNode();

		Node find = session.createQuery().path("/root/bleujin").findOne() ;
		assertEquals("bleujin", find.getString("name")) ;
	}
	
	public void testname() throws Exception {
		Session session = createSampleNode();
		Node find = session.createQuery().path("/root/bleujin").findOne() ;
		
		
	}
	
	

	private Session createSampleNode() {
		Node root = session.newNode("root").put("name", "root").put("depth", 1) ;
		Node bleujin = root.createChild("bleujin") ;
		bleujin.put("name", "bleujin").put("address", "seoul").inner("loc").put("x", 1).put("y", 1) ;
		
		for (int i = 0; i < 10 ; i++) {
			bleujin.inlist("comments").push(MapUtil.<String, Object>chainMap().put("greeting", "hello").put("index", i)) ;
		}
		session.commit() ;
		return session ;
	}
	
	
	
	
	
}
