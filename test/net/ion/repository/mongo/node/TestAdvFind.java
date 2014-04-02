package net.ion.repository.mongo.node;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.util.ReadChildrenEachs;
import net.ion.repository.mongo.util.WriteJobs;

public class TestAdvFind extends TestBaseReset {

	
	public void testApplyLastModifiedWhenModified() throws Exception {
		session.tranSync(WriteJobs.HELLO) ;
		
		long created = session.pathBy("/bleujin").getLastModified() ;
		Thread.sleep(10);
		session.tranSync(WriteJobs.HELLO) ;
		long modified = session.pathBy("/bleujin").getLastModified() ;
		
		assertEquals(true, created < modified);
	}
	
	
	
	
	public void testExplainWhenRead() throws Exception {
		createHelloNode();

		assertEquals("bleujin", session.pathBy("/bleujin").property("name").asString());

		Explain explain = session.pathBy("/bleujin").children().eachNode(new ReadChildrenEach<Explain>() {
			@Override
			public Explain handle(ReadChildrenIterator citer) {
				return citer.explain();
			}
		});

		assertEquals(false, explain.useIndex());
	}
	

}
