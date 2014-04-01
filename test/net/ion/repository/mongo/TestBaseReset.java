package net.ion.repository.mongo;

import net.ion.repository.mongo.util.WriteJobs;
import junit.framework.TestCase;

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
		session.dropCollection() ;
		session.workspace().repository().shutdown() ;
		super.tearDown();
	}

	
	protected void createHelloNode(){
		session.tranSync(WriteJobs.HELLO) ;
	}
}
