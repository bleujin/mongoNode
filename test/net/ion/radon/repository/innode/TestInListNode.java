package net.ion.radon.repository.innode;

import net.ion.radon.repository.Node;

public class TestInListNode extends  TestBaseInListQuery{

	public void testEqualFilter() throws Exception {
		createNode() ;
		Node found = session.createQuery().findOne() ;
		
		assertEquals(0, found.inlist("people").createQuery().eq("index", 0).findOne().get("index")) ;
		assertEquals(1, found.inlist("people").createQuery().eq("index", 1).findOne().get("index")) ;
		assertEquals(4, found.inlist("people").createQuery().eq("index", 4).findOne().get("index")) ;

		assertEquals(1, found.inlist("people").createQuery().eq("index", 1).find().size()) ;
		assertEquals(5, found.inlist("people").createQuery().eq("address.city", "seoul").find().size()) ;
		assertEquals(1, found.inlist("people").createQuery().eq("address.city", "seoul").eq("index", 1).find().size()) ;

		assertEquals(4, found.inlist("people").createQuery().ne("index", 1).find().size()) ;
	}
	
	
	public void testNotEqualFilter() throws Exception {
		createNode() ;
		Node found = session.createQuery().findOne() ;
		
		assertEquals(4, found.inlist("people").createQuery().ne("index", 0).find().size()) ;
	}
	

	public void testGreater() throws Exception {
		createNode() ;
		Node found = session.createQuery().findOne() ;
		
		assertEquals(1, found.inlist("people").createQuery().gt("index", 3).find().size()) ;
		assertEquals(2, found.inlist("people").createQuery().gte("index", 3).find().size()) ;
	}

	
	public void testLess() throws Exception {
		createNode() ;
		Node found = session.createQuery().findOne() ;
		
		assertEquals(2, found.inlist("people").createQuery().lt("index", 2).find().size()) ;
		assertEquals(3, found.inlist("people").createQuery().lte("index", 2).find().size()) ;

		assertEquals(2, found.inlist("people").createQuery().between("index", 2, 3).find().size()) ;
	}


	public void testIn() throws Exception {
		createNode() ;
		Node found = session.createQuery().findOne() ;
		
		assertEquals(2, found.inlist("people").createQuery().in("index", new Object[]{2,3}).find().size()) ;
	}
	

	public void testExist() throws Exception {
		createNode() ;
		Node found = session.createQuery().findOne() ;
		
		assertEquals(5, found.inlist("people").createQuery().exist("index").find().size()) ;
	}
	

	
}
