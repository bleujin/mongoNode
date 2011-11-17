package net.ion.radon.repository;

import junit.framework.TestCase;

public class TestBaseRepository  extends TestCase{
	
	protected final String WORKSPACE_NAME = "abcd";
	protected final String WORKSPACE_NAME2 = "abcd2";
	protected Session session ;
	protected RepositoryCentral rc ;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		rc = RepositoryCentral.testCreate() ;
		session = rc.testLogin(WORKSPACE_NAME) ;
		session.dropWorkspace();
//		session.getReferenceManager().reset() ;
		session.clear() ;
		session.changeWorkspace(WORKSPACE_NAME) ;
	}
	
	public SessionQuery createQuery(){
		return session.createQuery() ;
	}

	
}
