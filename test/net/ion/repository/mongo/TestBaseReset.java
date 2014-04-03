package net.ion.repository.mongo;

import junit.framework.TestCase;
import net.ion.repository.mongo.util.WriteJobs;

public class TestBaseReset extends TestCase {

	protected ReadSession session;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		RepositoryMongo rm = RepositoryMongo.testLocal() ;
		this.session = rm.login("test", "wsname") ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		session.collection().drop() ;
		session.workspace().repository().shutdown() ;
		super.tearDown();
	}

	
	protected void createHelloNode(){
		session.tranSync(WriteJobs.HELLO) ;
	}
}
