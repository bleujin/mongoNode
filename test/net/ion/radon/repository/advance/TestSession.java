package net.ion.radon.repository.advance;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.innode.TestBaseInListQuery;

public class TestSession extends TestCase{

	public void testOtherNotEqual() throws Exception {
		RepositoryCentral rc = RepositoryCentral.testCreate("ICSS_MONGO") ;
		Session session = rc.testLogin("novision") ;
		
		RepositoryCentral rc2 = RepositoryCentral.testCreate("ICS_MONGO") ;
		Session session2 = rc2.testLogin("ics_article") ;

		assertEquals("ICSS_MONGO.novision" , session.getCurrentWorkspace().toString()) ;
		assertEquals("ICS_MONGO.ics_article", session2.getCurrentWorkspace().toString()) ;
	}
}
