package net.ion.repository.mongo.comvert;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllConvert extends TestCase{

	public static TestSuite suite(){
		TestSuite suite = new TestSuite() ;
		
		suite.addTestSuite(TestChild.class);
		suite.addTestSuite(TestToChildBean.class);
		suite.addTestSuite(TestToFlatBean.class);
		suite.addTestSuite(TestToRefBean.class);
		
		return suite ;
	}
}
