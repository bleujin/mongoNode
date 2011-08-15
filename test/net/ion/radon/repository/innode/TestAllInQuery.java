package net.ion.radon.repository.innode;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllInQuery extends TestCase{

	
	public static TestSuite suite() throws Exception {
		
		TestSuite suite = new TestSuite() ;
		
		
		suite.addTestSuite(TestInPut.class) ;
		suite.addTestSuite(TestInFilter.class) ;
		suite.addTestSuite(TestInQuerySort.class) ;
		suite.addTestSuite(TestInMDL.class) ;
		
		return suite ;
		
	}
	
	
	
	

}
