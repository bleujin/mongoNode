package net.ion.repository.mongo.node;

import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.TestBaseReset;

public class TestGhostBy extends TestBaseReset{
	
	public void testGhostBy() throws Exception {
		createHelloNode();
		assertEquals("bleujin", session.ghostBy("/bleujin").property("name").asString());

		assertEquals(PropertyValue.NotFound, session.ghostBy("/notfound").property("name"));
		assertEquals(0, session.ghostBy("/notfound").children().count()) ;
	}
	
	public void testEqualIfExist() throws Exception {
		createHelloNode();
		
		assertEquals(true, session.pathBy("/bleujin").equals(session.ghostBy("/bleujin"))) ;
	}

}
