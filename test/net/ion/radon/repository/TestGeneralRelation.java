package net.ion.radon.repository;

import java.util.Date;

import net.ion.framework.util.Debug;
import net.ion.radon.repository.myapi.AradonQuery;

public class TestGeneralRelation extends TestBaseRepository{

	public void testRelation() throws Exception {
		session.dropWorkspace();

		Node bleu = session.newNode();
		bleu.put("name", "bleu");
		bleu.setAradonId("test", "bleu");
		
		Node jin = session.newNode() ;
		jin.put("name", "jin");
		jin.put("greeting", 44564);
		jin.setAradonId("test", "jin");

		Node heeya = session.newNode() ;
		heeya.put("name", "heeya");
		heeya.put("greeting", new Date());
		heeya.setAradonId("test", "heeya");
		
		
		session.addReference(bleu, "friend", jin) ;
		session.addReference(bleu, "pair", heeya) ;
		
		session.commit();
		ReferenceTaragetCursor refCursor =  session.createRefQuery().from(bleu).find() ;
		while(refCursor.hasNext()) {
			Debug.debug(refCursor.next()) ;
		}
	
	}

	public void testReference2() throws Exception {
		Node work1 = session.newNode();
		work1.put("name", "bleu");
		
		Node child1 = work1.createChild("child1");
		Node child2 = work1.createChild("child2");
		
		session.addReference(child1, "friend", child2);
		session.commit();
		
		Node result = session.createQuery().id(child1.getIdentifier()).findOne();
		
		final ReferenceTaragetCursor findReference = session.createRefQuery().from(result).find();
		assertEquals(1, findReference.size());
	}
	
	public void testOtherRelation() throws Exception {
		session.dropWorkspace() ;

		Node bleu = session.newNode("bleu");
		bleu.put("name", "bleu");
		bleu.setAradonId("test", "bleu");
		
		session.changeWorkspace("w2") ;
		session.dropWorkspace() ;
		
		Node jin = session.newNode() ;
		jin.put("name", "jin");
		jin.append("name", "hero");
		jin.put("greeting", 44564);
		jin.setAradonId("test", "jin");
		
		session.commit();

		session.addReference(bleu, "friend", jin) ;
		
		ReferenceTaragetCursor refCursor = session.createRefQuery().from(bleu).find() ;
		while(refCursor.hasNext()) {
			Debug.debug(refCursor.next()) ;
		}
	}
	
	public void testAddReference() throws Exception {
		Node node1 = createTestNode() ;
		session.changeWorkspace(WORKSPACE_NAME2) ;
		session.dropWorkspace() ;
		Node node2 = createTestNode() ;
		
		session.addReference(node1, "friend", node2) ;
		
		session.commit();
		ReferenceTaragetCursor rc = session.createRefQuery().from(node1).find();
		assertEquals(1, rc.count()) ;
	}
	
	public void testRemoveReference() throws Exception {
		Node node1 = createTestNode() ;
		Node node2 = createTestNode() ;
		
		session.addReference(node1, "ref", node2);
		
		session.commit() ;
		ReferenceTaragetCursor rc = session.createRefQuery().from(node1).find() ;
		assertEquals(1, rc.count()) ;
		
		NodeResult nr = session.createRefQuery().from(node1, "ref").remove();
		Debug.debug(nr) ;
		
		
		session.commit() ;
		rc = session.createRefQuery().from(node1).find();
		assertEquals(0, rc.count()) ;
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

	private Node createTestNode() {
		Node node = session.newNode().put("name", "bleujin").put("greeting", "hello") ;
		session.commit() ;
		return node;
	}
}
