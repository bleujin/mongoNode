package net.ion.radon.repository;

import net.ion.framework.util.Debug;
import net.ion.radon.repository.myapi.AradonQuery;

public class TestReference extends TestBaseRepository{

	
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

	
	public void testReferenceDepth() throws Exception {
		Node parent = session.newNode() ;
		Node child = parent.createChild("child") ;
		Node grandchild = child.createChild("gchild") ;
		
		// parent.getDecendant(2) ;
		
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
	
	public void testUpdateReference() throws Exception {
		Node parent = createTestNode().setAradonId("gnode", "bleujin") ;
		Node red = session.newNode().setAradonId("gcolor", "red").put("color", "red");
		Node white = session.newNode().setAradonId("gcolor", "white").put("color", "white");
		Node blue = session.newNode().setAradonId("gcolor", "blue").put("color", "blue");
		
		session.commit();
		parent.addReference("r_color", AradonQuery.newByGroupId("gcolor", "red"));
		session.commit();
		
		parent.removeReference("r_color", AradonQuery.newByGroupId("gcolor", "red"));
		parent.addReference("r_color", AradonQuery.newByGroupId("gcolor", "white"));
	}
	
	private Node createTestNode() {
		Node node = session.newNode() ;
		node.put("name", "bleujin") ;
		node.put("greeting", "hello") ;
		
		session.commit() ;
		return node;
	}
	
	public void xtestSortReference() throws Exception {
		Node red = session.newNode().put("color", "red").put("age", 20);
		Node white = session.newNode().put("color", "white").put("age", 10);
		Node black = session.newNode().put("color", "black").put("age", 30);
		session.commit() ;
		
		session.addReference(red, "white", white);
		session.addReference(red, "black", black);
		session.commit() ;
		
		//session.createRefQuery().from(red).ascending("color").find();
	}
	
}
