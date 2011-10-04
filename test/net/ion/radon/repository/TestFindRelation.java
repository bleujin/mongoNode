package net.ion.radon.repository;

public class TestFindRelation extends TestBaseRepository{

	public void testReference() throws Exception {
		Node node1 = createTestNode() ;
		Node node2 = createTestNode() ;

		session.addReference(node1.createChild("child1"), "curson", node2.createChild("child2")) ;
		session.commit() ;
		
		Node find =  session.createRefQuery().from( session.createRefQuery().from(node1).findOne()).findOne() ;
		assertEquals("child2", find.getName()) ;
		session.remove(session.createRefQuery().from(node1).findOne()) ; // remove child1
		assertEquals(1, session.createRefQuery().to(find).find().count()) ;
	}
	

	public void testSimpleReference() throws Exception {
		Node parent = createTestNode() ;
		Node child = parent.createChild("child1") ;
		session.commit() ;

//		ReferenceTaragetCursor rc =  session.createRefQuery().from(child).find() ;
//		while(rc.hasNext()) {
//			Debug.line(rc.next()) ;
//		}

		assertEquals("child1", session.createRefQuery().from(parent).findOne().getName()) ;
		assertEquals("child1", session.createRefQuery().from(parent, "_child" ).findOne().getName()) ;
		
		assertEquals(parent.getIdentifier(), session.createRefQuery().to(child).findOne().getName()) ;
		assertEquals(parent.getIdentifier(), session.createRefQuery().to(child, "_child" ).findOne().getName()) ;
	}
	

	private Node createTestNode() {
		Node node = session.newNode().put("name", "bleujin").put("greeting", "hello") ;
		
		session.commit() ;
		return node;
	}
	
}
