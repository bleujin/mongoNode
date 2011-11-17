package net.ion.radon.repository.mr;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.radon.repository.TestApplyHandler;

public class TestAllMapReduce extends TestCase{

	public static TestSuite suite() {
		TestSuite result = new TestSuite("Test MapReduce") ;
		
		result.addTestSuite(TestGroup.class) ;
		result.addTestSuite(TestMapReduce.class) ;
		result.addTestSuite(TestApplyHandler.class) ;
		// result.addTestSuite(TestOutputType.class) ;
		
		result.addTestSuite(TestNodeFormat.class) ;
		result.addTestSuite(TestMRSpeed.class) ;
		
		return result;
	}

}
