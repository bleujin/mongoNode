package net.ion.repository.mongo.node;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.exception.NotFoundPath;

public class TestReference extends TestBaseReset{

	
	public void testRefTo() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").refTo("dept", "/dept/dev") ;
				wsession.pathBy("/dept/dev").property("name", "develop") ;
				return null;
			}
		}) ;
		
		ReadNode dept = session.pathBy("/emps/bleujin").ref("dept") ;
		assertEquals("develop", dept.property("name").asString());
	}
	
	
	
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
	
		
		assertEquals(3, session.pathBy("/dept/dev").refs("emps").toList().size()) ;
		
	}
	
	
	public void testRefTos() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/dept/dev").property("name", "develop").refTos("emps", "/emps/bleujin", "/emps/hero") ;
				
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").refTo("dept", "/dept/dev") ;
				wsession.pathBy("/emps/hero").property("name", "hero").refTo("dept", "/dept/dev") ;
				wsession.pathBy("/emps/jin").property("name", "jin").refTo("dept", "/dept/dev") ;
				
				wsession.pathBy("/dept/dev").refTos("emps", "/emps/jin") ;
				return null;
			}
		}) ;
		
		IteratorList<ReadNode> emps = session.pathBy("/dept/dev").refs("emps") ;
		assertEquals(3, emps.toList().size());
		
		
		IteratorList<ReadNode> empty = session.pathBy("/dept/dev").refs("notfound") ;
		assertEquals(0, empty.toList().size());
	}
	
	
	public void testRefFailWhenNotFoundInReadSession() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").refTo("dept", "/dept/dev") ;
				return null;
			}
		}) ;

		ReadNode bleujin = session.pathBy("/emps/bleujin");
		try {
			bleujin.ref("dept") ;
			fail() ;
		} catch(NotFoundPath expect){
		}

		try {
			bleujin.ref("notrel") ;
			fail() ;
		} catch(NotFoundPath expect){
		}
	}
	
	
	public void testUnRef() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").refTo("dept", "/dept/dev").refTos("other", "/dept/dev", "/dept/dev2") ;
				wsession.pathBy("/dept/dev").property("name", "develop") ;
				return null;
			}
		}) ;
		
		assertEquals("develop", session.pathBy("/emps/bleujin").ref("dept").property("name").asString()) ;
		
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/emps/bleujin").unref("dept").unref("other", "/dept/dev2") ;
				return null;
			}
		}) ;
		
		ReadNode bleujin = session.pathBy("/emps/bleujin");
		
		assertEquals(false, bleujin.hasRef("dept")) ;
		assertEquals(true, bleujin.hasRef("other")) ;
		
		assertEquals(1, bleujin.refs("other").toList().size());
	}
	
	
	
	
	
	
}
