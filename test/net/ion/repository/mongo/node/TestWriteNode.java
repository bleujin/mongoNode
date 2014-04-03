package net.ion.repository.mongo.node;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.util.WriteJobs;

public class TestWriteNode extends TestBaseReset{

	public void testToString() throws Exception {
		session.tranSync(WriteJobs.HELLO) ;

		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				WriteNode wnode = wsession.pathBy("/bleujin") ;
				assertEquals("WriteNode:fqn[/bleujin]", wnode.toString()) ;
				return null;
			}
		}) ;
	}
	

}
