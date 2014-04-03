package net.ion.repository.mongo.node;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;

public class TestPropertyValue extends TestBaseReset{

	public void testAsType() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20).append("color", "red", "blue", "white", "red") ;
				return null;
			}
		}) ;
		
		
		
		ReadNode found = session.pathBy("/bleujin") ;
		assertEquals("bleujin", found.property("name").asString());
		assertEquals(20, found.property("age").asInt());
		assertEquals("red", found.property("color").asString());
		assertEquals(3, found.property("color").asSet().size());
		assertEquals(4, found.property("color").asList().size());
		
		assertEquals(false, found.property("color").isNotFound());
		assertEquals(true, found.property("notfound").isNotFound());
	}
	
	
	public void testExtendProperty() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").refTo("dept", "/dept/dev").child("address/city").property("name", "seoul") ;
				wsession.pathBy("/dept/dev").property("name", "develop").child("sub").property("name", "platform") ;
				return null;
			}
		}) ;
		
		
		ReadNode bleujin = session.pathBy("/emps/bleujin");
		assertEquals("bleujin", bleujin.extendProperty("name").asString()) ;
		assertEquals("seoul", bleujin.extendProperty("address/city/name").asString()) ;
		assertEquals("develop", bleujin.extendProperty("dept@name").asString()) ;
		assertEquals("platform", bleujin.extendProperty("dept@sub/name").asString()) ;
		
		assertEquals("bleujin", session.pathBy("/emps/bleujin/address").extendProperty("../name").asString()) ;
	}
	
}
