package net.ion.repository.mongo.node;

import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;

public class TestWriteSession extends TestBaseReset {

	public void testRefs() throws Exception {
		int refCount = session.tranSync(new WriteJob<Integer>() {
			@Override
			public Integer handle(WriteSession wsession) {
				wsession.pathBy("/dept/dev").property("name", "develop").refTos("emps", "/emps/bleujin", "/emps/hero") ;
				
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").refTo("dept", "/dept/dev") ;
				wsession.pathBy("/emps/hero").property("name", "hero").refTo("dept", "/dept/dev") ;
				wsession.pathBy("/emps/jin").property("name", "jin").refTo("dept", "/dept/dev") ;
				
				wsession.pathBy("/dept/dev").refTos("emps", "/emps/jin") ;
				
				return wsession.pathBy("/dept/dev").refs("emps").toList().size() ;
			}
		}) ;
		
		assertEquals(3, refCount); 
		
		
		assertEquals(true, session.tranSync(new WriteJob<Integer>(){
			@Override
			public Integer handle(WriteSession wsession) {
				return wsession.pathBy("/dept/dev").refs("emps").toList().size() ;
			}
		}) == 3) ;
	}
	
	
	public void testClear() throws Exception {
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
		
		
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").clear() ;
				return null;
			}
		}) ;

		found = session.pathBy("/bleujin") ;
		assertEquals(0, found.propSize());
		assertEquals(0, found.normalKeys().size()) ;
	}
	
	
	public void testUnSet() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20).append("color", "red", "blue", "white", "red") ;
				return null;
			}
		}) ;
		
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").unset("name").unset("color", "red", "blue") ;
				return null;
			}
		}) ;
		
		assertEquals(true, session.pathBy("/bleujin").property("name") == PropertyValue.NotFound);
		assertEquals("white", session.pathBy("/bleujin").property("color").asString());
		assertEquals(1, session.pathBy("/bleujin").property("color").asSet().size());
	}
	
	
	
}
