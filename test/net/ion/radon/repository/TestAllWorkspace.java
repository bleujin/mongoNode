package net.ion.radon.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllWorkspace extends TestCase{

	public static Test suite() {
		TestSuite suite = new TestSuite("Test Workspace") ;
		
		suite.addTestSuite(TestRepositoryCentral.class) ;
		suite.addTestSuite(TestSession.class);
		suite.addTestSuite(TestWorkspace.class);
		return suite;
	}

}
