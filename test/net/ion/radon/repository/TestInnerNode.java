package net.ion.radon.repository;


public class TestInnerNode extends TestBaseRepository {

	public void testInterrface() throws Exception {
		
		Node node = session.newNode() ;
		node.setAradonId("people", "bleujin") ;
		node.put("name", "hi") ;
		node.put("firstname", "myname") ;
		
		session.commit() ;
		
		node.inner("name").put("firstName", "bleu").put("secondName", "jin") ;
		session.commit() ;
		
		
		assertEquals("bleu", ((InNode)node.get("name")).get("firstname")) ;
		assertEquals("bleujin", ((InNode)node.get(NodeConstants.ARADON)).getString("uid") ) ;

		Node found = session.createQuery().find().next() ;

		assertEquals("bleu", found.get("name.firstname")) ;
	}

	public void testComplicateGet() throws Exception {
		Node node = session.newNode() ;
		node.setAradonId("people", "bleujin") ;
		node.inner("name").put("firstName", "bleu").put("secondName", "jin").inner("address").put("loc", "seoul") ;
		node.inner("address").put("loc", "busan") ;

		session.commit() ;
		
		assertEquals("bleu", node.getString("name.firstname")) ;
		assertEquals("bleujin", node.getString("__aradon.uid")) ;
		
		
		Node found = session.createQuery().eq("name.firstname", "bleu").find().next() ;
		assertEquals("bleu", found.getString("name.firstname")) ;
		
		assertEquals("seoul", found.getString("name.address.loc")) ;
		assertEquals("busan", found.getString("address.loc")) ;
	}
	
	public void testComplicateInner() throws Exception {
		Node node = session.newNode().put("name", "bleujin") ;
		node.inner("c1").put("name", "c1").inner("c2").put("name", "c2").inner("c3").put("name", "c3") ;
		session.commit() ;
		
		Node found = session.createQuery().findOne() ;
		assertEquals("bleujin", found.get("name")) ;
		assertEquals("c1", found.get("c1.name")) ;
		assertEquals("c2", found.get("c1.c2.name")) ;
		assertEquals("c3", found.get("c1.c2.c3.name")) ;

		assertEquals(true, found.get("c1") instanceof InNode) ;
		assertEquals(true, found.get("c1.c2") instanceof InNode) ;
		assertEquals(true, found.get("c1.c2.c3") instanceof InNode) ;

	}

	
	
	
}
