package net.ion.radon.repository;

public class TestParentChild extends TestBaseRepository{

	public void testParentChild() throws Exception {
		Node parent = session.newNode();
		parent.put("name", "bleu");
		
		Node child1 = parent.createChild("child1");
		Node child2 = parent.createChild("child2");

		assertEquals(3 + 2, session.getModified().size()) ;
		session.commit() ;
		
		
		Node fparent = child1.getParent() ;
		assertEquals("bleu", fparent.getString("name")) ;
		
		assertTrue(session.getRoot() == parent.getParent()) ;
	}
	

	
	public void testReferenceDepth() throws Exception {
		Node parent = session.newNode() ;
		Node child = parent.createChild("child") ;
		Node grandchild = child.createChild("gchild") ;
		
		// parent.getDecendant(2) ;
		
	}
	
}
