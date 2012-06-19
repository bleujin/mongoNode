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
	
	public void testFrom() throws Exception {
		Node parent = session.newNode().put("name", "parent") ;
		Node child = parent.createChild("child1").put("name", "bleujin") ;
		parent.createChild("child2").put("name", "hero") ;
		
		session.commit() ;
	
		Node found = session.createQuery().eq("name", "parent").findOne() ;
		NodeCursor nc = parent.relation(NodeConstants.PARENT).froms() ;
		
		assertEquals(2, nc.count()) ;
	}
	
	public void testFromSort() throws Exception {
		Node parent = session.newNode().put("name", "parent") ;
		
		parent.createChild("child1").put("name", "bleujin").put("color", "bleu").put("age", 25) ;
		parent.createChild("child2").put("name", "hero").put("color", "red").put("age", 20) ;
		session.commit() ;
		
		NodeCursor nc1 = parent.relation(NodeConstants.PARENT).froms().ascending("age") ;
		assertEquals(20, nc1.next().get("age")) ;
		
		NodeCursor nc2 = parent.relation(NodeConstants.PARENT).froms().ascending("color") ;
		assertEquals(25, nc2.next().get("age")) ;

	}
	
	public void testFromDelete() throws Exception {
		Node parent = session.newNode().put("name", "parent") ;
		
		parent.createChild("child1").put("name", "bleujin").put("color", "bleu").put("age", 25) ;
		parent.createChild("child2").put("name", "hero").put("color", "red").put("age", 20) ;
		session.commit() ;
		
		int removed = session.createQuery(parent.relation(NodeConstants.PARENT).froms().getQuery() ).remove() ;
		
		assertEquals(2, removed) ;
		assertEquals(1, session.createQuery().find().count()) ;
	}
	
	
	
}
