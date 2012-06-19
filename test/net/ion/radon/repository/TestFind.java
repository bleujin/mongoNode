package net.ion.radon.repository;


public class TestFind extends TestBaseRepository{
	
	
	public void testFindById() throws Exception {
		Node bleujin = session.newNode().put("name", "bleujin") ;
		session.commit() ;
		
		assertEquals("bleujin", session.createQuery().id(bleujin.getIdentifier()).findOne().get("name")) ;
		assertEquals(true, session.getAttribute(Explain.class.getCanonicalName(), Explain.class).useIndex()) ;
	}
	
	public void testFindByProperty() throws Exception {
		Node bleujin = session.newNode().put("name", "bleujin") ;
		session.commit() ;
		
		assertEquals("bleujin", session.createQuery().eq("name", "bleujin").findOne().get("name")) ;
		assertEquals(false, session.getAttribute(Explain.class.getCanonicalName(), Explain.class).useIndex()) ;
	}
	
	public void testFindByPath() throws Exception {
		Node newNode = session.newNode("name") ;
		
		Node child = newNode.createChild("child") ;
		child.put("name", "bleujin") ;
		session.commit();

		
		Node load = session.createQuery().path("/name/child").findOne() ;
		assertEquals("bleujin", load.getString("name")) ;
		
		Node gchild = load.createChild("gchild") ;
		gchild.append("name", "hero") ;
		session.commit();
		
		Node gload = session.createQuery().path("/name/child/gchild").findOne() ;
		assertEquals("hero", gload.getString("name")) ;
		assertEquals(true, session.getAttribute(Explain.class.getCanonicalName(), Explain.class).useIndex()) ;
	}
	
	
	public void testByAradonId() throws Exception {
		session.newNode().setAradonId("emp", 7756) ;
		session.commit() ;
		
		
		assertEquals(1, session.createQuery().aradonGroup("emp").find().count()) ;
		assertEquals(1, session.createQuery().aradonGroupId("emp", 7756).find().count()) ;
		assertEquals(true, session.getAttribute(Explain.class.getCanonicalName(), Explain.class).useIndex()) ;
	}
	
	
	public void testEqual() throws Exception {
		Node bleujin = session.newNode("bleujin");
		bleujin.put("name", "bleu");
		session.commit() ;
		
		Node load = session.createQuery().path("/bleujin").findOne() ;
		
		assertEquals(bleujin.getIdentifier(), load.getIdentifier()) ;
		assertTrue(bleujin.equals(load)) ;
	}

	
	
	
}
