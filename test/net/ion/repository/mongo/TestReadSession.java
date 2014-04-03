package net.ion.repository.mongo;

import junit.framework.TestCase;

public class TestReadSession extends TestCase{

	
	public void testGetDBName() throws Exception {
		RepositoryMongo rm = RepositoryMongo.testLocalWithShutdownHook() ;
		ReadSession session = rm.login("test", "wsname") ;

		
		assertEquals("test", session.workspace().name()) ;
	}
	
	public void testCollectionName() throws Exception {
		RepositoryMongo rm = RepositoryMongo.testLocalWithShutdownHook() ;
		ReadSession session = rm.login("test", "wsname") ;
		
		assertEquals("wsname", session.colName()) ;
	}

}
