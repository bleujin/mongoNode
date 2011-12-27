package net.ion.radon.repository;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

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
	
	
	public void xtestNullAuthMongo() throws Exception {
		try{
			RepositoryCentral rc_fail_auth = new RepositoryCentral("127.0.0.1", 10505, "auth") ;
			rc_fail_auth.login("test").createQuery().count();
			fail();
		}catch(Exception e){
			
		}
	}
	
	public void xtestPassAuthMongo() throws Exception {
		RepositoryCentral rc_pass_auth = new RepositoryCentral("127.0.0.1", 10505, "auth", "test", "1234") ;
		assertNotNull(rc_pass_auth.login("test").createQuery().count());
	}
	
	public void xtestFailAuthMongo() throws Exception {
		try{
			RepositoryCentral rc_fail_auth = new RepositoryCentral("127.0.0.1", 10505, "auth", "test", "12345") ;
			rc_fail_auth.login("test").createQuery().count();
			fail();
		}catch(Exception e){
			
		}
	}
	
}
