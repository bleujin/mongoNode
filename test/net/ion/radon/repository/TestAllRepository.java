package net.ion.radon.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllRepository extends TestCase{

	public static Test suite() throws Exception{
		TestSuite suite = new TestSuite("Test RepositoryAll");
		
		suite.addTest(TestAllSimpleRepository.suite()) ;
		suite.addTest(TestAllAdvanceRepository.suite()) ;
		
		
		// working
		// suite.addTestSuite(TestNodeCreateSpeed.class) ;
		
		return suite;
	}
}
