package net.ion.radon.repository;

public class TestAdvanceQuery extends TestBaseRepository{

	
	public void testAnd() throws Exception {
		
		Node n1 = session.newNode().put("Name", "bleujin") ;
		Node n2 = session.newNode().put("Name", "hero") ;
		
		session.commit() ;
		
		assertEquals(1, createQuery().gt("Name", "h").count()) ; 
		
	} 
	
	
	
	public void testNotify() throws Exception {
		Node n1 = session.newNode().put("Name", "bleujin") ;
		session.commit() ;

		
		n1.setAradonId("mygroup", 11) ;
		
		assertEquals(1, session.getModified().size()) ;
	}
	
	
	
	
}
