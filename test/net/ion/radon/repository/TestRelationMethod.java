package net.ion.radon.repository;

import java.util.Date;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.AradonQuery;

public class TestRelationMethod extends TestBaseRepository{

	public void testFrom() throws Exception {

		Node bleu = session.newNode().put("name", "bleu").setAradonId("test", "bleu");
		
		Node jin = session.newNode().put("name", "jin").put("greeting", 44564).setAradonId("test", "jin");
		Node heeya = session.newNode().put("name", "heeya").put("greeting", new Date()).setAradonId("test", "heeya");
		
		bleu.toRelation("friend", jin.selfRef()) ;
		bleu.toRelation("friend", heeya.selfRef()) ;
		
		session.commit();
		
		NodeCursor nrc =  bleu.relation("friend").fetchs() ;
		assertEquals(2, nrc.count()) ;
	}

	public void testReference() throws Exception {
		Node work1 = session.newNode().put("name", "bleu");
		
		Node child1 = work1.createChild("child1");
		Node child2 = work1.createChild("child2");
		
		child1.toRelation("friend", child2.selfRef()); 
		session.commit();
		
		Node found = session.createQuery().id(child1.getIdentifier()).findOne();
		final NodeCursor nrc = found.relation("friend").fetchs();
		assertEquals(1, nrc.count());
	}
	
	public void testOtherWorkspace() throws Exception {
		Node bleu = session.newNode("bleu").put("name", "bleu").setAradonId("test", "bleu");
		session.changeWorkspace("w2") ;
		
		session.dropWorkspace() ;
		Node jin = session.newNode().put("name", "jin").append("name", "hero").put("greeting", 44564).setAradonId("test", "jin");
		session.commit();

		bleu.toRelation("friend", jin.selfRef()) ;
		
		
		NodeCursor nrc = bleu.relation("friend").fetchs() ;
		assertEquals(1, nrc.count()) ;
		assertEquals("44564", nrc.next().getString("greeting")) ;
	}
	
	public void testRemoveAllReference() throws Exception {
		Node node1 = session.newNode().put("name", "bleujin").put("greeting", "hello") ;
		Node node2 = session.newNode().put("name", "hero").put("greeting", "hi") ; 
		node1.toRelation("ref", node2.selfRef());
		session.commit() ;
		
		assertEquals(1, node1.relation("ref").fetchs().count()) ;
		int count = node1.relation("ref").remove() ;
		
		assertEquals(1, count) ;
		node1.relation("ref").fetchs().debugPrint(PageBean.ALL) ;
		assertEquals(0, node1.relation("ref").fetchs().count()) ;
		
		
		int notapplid = session.createQuery().id(node1.getIdentifier()).findOne().relation("ref").fetchs().count() ;
		assertEquals(1, notapplid) ;
		
		session.commit() ;
		assertEquals(0, session.createQuery().id(node1.getIdentifier()).findOne().relation("ref").fetchs().count());
		
	}

//	public void testRemoveReference() throws Exception {
//		Node parent = createTestNode().setAradonId("gnode", "bleujin") ;
//		Node red = session.newNode().setAradonId("gcolor", "red").put("color", "red");
//		session.newNode().setAradonId("gcolor", "white").put("color", "white");
//		session.newNode().setAradonId("gcolor", "blue").put("color", "blue");
//		
//		parent.toRelation("color", NodeRef.create(AradonId.create("gcolor", "red")));
//		parent.toRelation("color", NodeRef.create(AradonId.create("gcolor", "white")));
//		session.commit();
//		
//		int count = parent.relation("color").remove(red.selfRef());
//		parent.addReference("r_color", AradonQuery.newByGroupId("gcolor", "white"));
//	}
	
	
	public void xtestSortReference() throws Exception {
		Node red = session.newNode().put("color", "red").put("age", 20);
		Node white = session.newNode().put("color", "white").put("age", 10);
		Node black = session.newNode().put("color", "black").put("age", 30);
		
		red.toRelation("white", white.selfRef());
		red.toRelation("black", black.selfRef());
		session.commit() ;
		
		//session.createRefQuery().from(red).ascending("color").find();
	}

}
