package net.ion.repository.mongo.node;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;

public class TestRefChildren extends TestBaseReset {
	
	public void testRefsChildren() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/dept/dev").property("name", "develop").refTos("emps", "/emps/bleujin", "/emps/hero", "/emps/jin") ;
				
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").refTo("dept", "/dept/dev") ;
				wsession.pathBy("/emps/hero").property("name", "hero").refTo("dept", "/dept/dev") ;
				wsession.pathBy("/emps/jin").property("name", "jin").refTo("dept", "/dept/dev") ;
				return null;
			}
		}) ;
		
		assertEquals(3, session.pathBy("/dept/dev").refs("emps").toList().size()) ;
		
		ReadChildren refChildren = session.pathBy("/dept/dev").refChildren("emps") ;
		assertEquals(3, refChildren.count());
		assertEquals("jin", refChildren.descending("name").firstNode().property("name").asString());
	}
	
	
	public void testRefsChildrenInWriteSession() throws Exception {
		int count = session.tranSync(new WriteJob<Integer>() {
			@Override
			public Integer handle(WriteSession wsession) {
				wsession.pathBy("/dept/dev").property("name", "develop").refTos("emps", "/emps/bleujin", "/emps/hero", "/emps/jin") ;
				
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").refTo("dept", "/dept/dev") ;
				wsession.pathBy("/emps/hero").property("name", "hero").refTo("dept", "/dept/dev") ;
				wsession.pathBy("/emps/jin").property("name", "jin").refTo("dept", "/dept/dev") ;
				
				return wsession.pathBy("/dept/dev").refChildren("emps").count() ;
			}
		}) ;
		
		assertEquals(3, count) ;
	}
	
	
}
