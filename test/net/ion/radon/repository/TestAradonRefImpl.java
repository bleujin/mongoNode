package net.ion.radon.repository;



public class TestAradonRefImpl extends TestBaseRepository {
	
	public void testRefListInfo() throws Exception {
		Node bleujin = session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin") ;
		Node hero = session.newNode().setAradonId("emp", "hero").put("name", "hero") ;
		Node novision = session.newNode().setAradonId("emp", "novision").put("name", "novision") ;
		
		bleujin.inlist("friend").push(hero.selfRef().toMap()) ;
		bleujin.inlist("friend").push(novision.selfRef().toMap()) ;
		session.commit() ;
		
		Node found = session.createQuery().eq("name", "bleujin").findOne() ;
		InListNode friends = (InListNode)found.get("friend") ;
		assertEquals(2, friends.createQuery().find().size()) ;
	}
	
	public void testIn() throws Exception {
		Node bleujin = session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin") ;
		Node hero = session.newNode().setAradonId("emp", "hero").put("name", "hero") ;
		Node novision = session.newNode().setAradonId("emp", "novision").put("name", "novision") ;
		
		bleujin.inner("__reference").inlist("friend").push(hero.selfRef().toMap()) ;
		bleujin.inner("__reference").inlist("friend").push(novision.selfRef().toMap()) ;
		session.commit() ;
		
		Node found = session.createQuery().eq("name", "bleujin").findOne() ;
		assertEquals("hero", found.relation("friend").fetch(0).getString("name")) ;
	}

}
