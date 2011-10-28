package net.ion.radon.repository;

import net.ion.framework.util.Debug;
import junit.framework.TestCase;

public class TestRepositoryCentral extends TestCase{

	public void testCreate() throws Exception {
		
		RepositoryCentral rc = new RepositoryCentral("61.250.201.78", 27017) ;
		Session session = rc.testLogin("test") ;
		session.dropWorkspace() ;
		
		assertEquals(0, session.createQuery().find().count()) ;
		
		RepositoryCentral another = RepositoryCentral.create("61.250.201.78", 27017) ;
		
		Debug.line(rc.getMongo(), another.getMongo()) ;
		assertEquals(true, rc.getMongo() == another.getMongo()) ;
	}
	
	
	public void testSameMongo() throws Exception {
		RepositoryCentral rc1 = new RepositoryCentral("61.250.201.78", 27017) ;
		RepositoryCentral rc2 = new RepositoryCentral("61.250.201.78", 27017) ;
		
		assertEquals(true, rc1.getMongo() == rc2.getMongo()) ;
		
	}
	
}
