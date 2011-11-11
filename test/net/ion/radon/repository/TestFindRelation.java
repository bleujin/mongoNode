package net.ion.radon.repository;

import net.ion.framework.util.Debug;


public class TestFindRelation extends TestBaseRepository{

	public void testToRelation() throws Exception {
		Node from = session.newNode().put("name", "bleujin").put("greeting", "hello") ;
		Node to = session.newNode().put("name", "hero").put("greeting", "hi");

		from.toRelation("friend", to.selfRef()) ;
		session.commit() ;
		
		assertEquals(1, session.createQuery().to(to, "Friend").find().count()) ;
	}
	
	public void testRelationValue() throws Exception {
		Node to = session.newNode().setAradonId("emp", 7756).put("name", "hero").put("greeting", "hi");
		Node from = session.newNode().put("name", "bleujin").put("greeting", "hello") ;

		from.toRelation("friend",  to.selfRef()) ;
		Debug.line(from.toMap()) ;
	}
	
	public void testToAradonId() throws Exception {
		Node to = session.newNode().setAradonId("emp", 7756).put("name", "hero").put("greeting", "hi");
		Node from = session.newNode().put("name", "bleujin").put("greeting", "hello").toRelation("friend", to.selfRef()) ;
		session.commit() ;
		
		assertEquals(1, session.createQuery().to(to, "Friend").find().count()) ;
	}
	
	
	
	
	
}
