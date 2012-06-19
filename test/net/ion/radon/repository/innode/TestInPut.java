package net.ion.radon.repository.innode;

import net.ion.radon.repository.Node;

public class TestInPut extends TestBaseInListQuery{

	public void testPath() throws Exception {
		Node node = makeNode();
		
		assertEquals("bleujin", node.get("people.name")) ;
		assertEquals("seoul", node.get("people.address.city")) ;
	}

	public void testCase() throws Exception {
		Node node = makeNode();
		
		assertEquals("bleujin", node.get("people.Name")) ;
		assertEquals("seoul", node.get("people.ADDRESS.city")) ;
	}
	
	public void testQuery() throws Exception {
		Node node = makeNode() ;
		session.commit() ;
		
		assertEquals(true, session.createQuery().eq("people.name", "bleujin").existNode());
		assertEquals(true, session.createQuery().in("people.address.street", new Integer[]{1}).existNode());
		assertEquals(false, session.createQuery().in("people.address.street", new Integer[]{4}).existNode());
		assertEquals(true, session.createQuery().in("people.color", new String[]{"red"}).existNode());
	}
	
}
