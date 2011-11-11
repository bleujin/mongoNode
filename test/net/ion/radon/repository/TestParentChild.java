package net.ion.radon.repository;


public class TestParentChild extends TestBaseRepository{

	public void testGetParent() throws Exception {
		Node parent = session.newNode();
		parent.put("name", "bleu");
		
		Node child1 = parent.createChild("child1");
		Node child2 = parent.createChild("child2");

		assertEquals(3, session.getModified().size()) ;
		session.commit() ;
		
		
		Node found = child1.getParent() ;
		assertEquals("bleu", found.getString("name")) ;
		
		assertTrue(session.getRoot() == parent.getParent()) ;
	}
	
	public void testGetParent2() throws Exception {
		Node parent = session.newNode() ;
		Node child = parent.createChild("child") ;
		Node grandchild = child.createChild("gchild") ;

		assertEquals(session.getRoot(), grandchild.getParent()) ;
		session.commit() ;
		
		assertEquals(child.getIdentifier(), grandchild.getParent().getIdentifier()) ;
	}
	
	public void testGetChild() throws Exception {
		Node parent = session.newNode() ;
		parent.createChild("child1").put("name", "bleujin") ;
		parent.createChild("child2").put("name", "hero") ;
		
		session.commit() ;
		
		Node found = parent.getChild("child1") ;
		assertEquals("bleujin", found.getString("name")) ;
		
		assertEquals(2, parent.getChild().count()) ;
	}
	
	
	
}
