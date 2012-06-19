package net.ion.radon.repository;

import net.ion.framework.util.Debug;

public class TestAppend extends TestBaseRepository {

	public void testAppend() throws Exception {
		Node node = session.newNode() ;
		
		node.append("name", "bleujin") ;
		node.append("name", "hero") ;
		node.append("name", "jin") ;
		
		session.commit() ;
		
		Debug.debug(node.get("name"), node.get("name").getClass()) ;
	}
}
