package net.ion.radon.repository.innode;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllInQuery extends TestCase{

	
	public static TestSuite suite() throws Exception {
		
		TestSuite suite = new TestSuite() ;
		
		
		suite.addTestSuite(TestInPut.class) ;
		suite.addTestSuite(TestInListQuery.class) ;
		suite.addTestSuite(TestInListQuerySort.class) ;
		suite.addTestSuite(TestInListMDL.class) ;
		
		return suite ;
		
	}
	
	
	
	

}
