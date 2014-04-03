package net.ion.repository.mongo.node;

import net.ion.repository.mongo.PropertyId;
import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.util.WriteJobs;

public class TestReadNode extends TestBaseReset{

	
	public void testHasFqn() throws Exception {
		session.tranSync(WriteJobs.HELLO) ;
		ReadNode found = session.pathBy("/bleujin") ;
		
		assertEquals("/bleujin", found.fqn().toString()) ;
	}
	
	public void testPropKeys() throws Exception {
		session.tranSync(WriteJobs.HELLO) ;

		ReadNode found = session.pathBy("/bleujin") ;
		assertEquals(2, found.normalKeys().size()) ;
	}
	
	public void testHasRelationAsProperty() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").refTo("dept", "/dev") ;
				return null;
			}
		}) ;
		
		ReadNode found = session.pathBy("/bleujin") ;
		assertEquals(1, found.normalKeys().size()) ;
		
		int normalCount = 0 ;
		int referenceCount = 0 ;
		int reservedCount = 0 ;
		for(PropertyId pid : found.keys()){
			if (pid.type().isNormal()) normalCount++ ;
			if (pid.type().isReference()) referenceCount++ ;
			if (pid.name().startsWith("_")) reservedCount++ ;
		}
		
		assertEquals(4, normalCount) ;
		assertEquals(1, referenceCount) ;
		assertEquals(3, reservedCount) ;
	}
	
	public void testToString() throws Exception {
		session.tranSync(WriteJobs.HELLO) ;

		ReadNode found = session.pathBy("/bleujin") ;
		assertEquals("ReadNode:fqn[/bleujin]", found.toString()) ;
	}
}
