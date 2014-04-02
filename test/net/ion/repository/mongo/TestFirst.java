package net.ion.repository.mongo;

import junit.framework.TestCase;

public class TestFirst extends TestCase {

	private ReadSession session;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		RepositoryMongo rm = RepositoryMongo.testLocal() ;
		this.session = rm.login("test", "wsname") ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		session.workspace().repository().shutdown() ;
		super.tearDown();
	}
	
	public void testFirst() throws Exception {
		session.dropCollection() ;
		
		session.tranSync(new WriteJob<Void>(){
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20) ;
				return null;
			}
		}) ;
		
		assertEquals("/bleujin", session.pathBy("/bleujin").fqn().toString())  ;
		assertEquals("bleujin", session.pathBy("/bleujin").property("name").asString()) ;
	}
	
}
