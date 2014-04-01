package net.ion.repository.mongo.node;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.util.WriteJobs;

public class TestRemove extends TestBaseReset {

	public void testNodeRemove() throws Exception {
		session.tranSync(WriteJobs.HELLO) ;
		
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().removeChild("bleujin") ;
				return null;
			}
		}) ;
		
		assertEquals(false, session.exists("/bleujin"));
	}

	public void testQueryRemove() throws Exception {

		session.tranSync(WriteJobs.HELLO) ;
		
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().removeChildren();
				return null;
			}
		}) ;

		assertEquals(false, session.exists("/bleujin"));
	}


	
}
