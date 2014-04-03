package net.ion.repository.mongo;

import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.node.ReadNode;

public class TestWriteSession extends TestBaseReset {

	
	public void testHasReadSession() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				assertEquals(true, wsession.readSession() == session) ;
				assertEquals(true, wsession.workspace() == session.workspace()) ;
				return null;
				
			}
		}) ;
	}
	
	
	
}
