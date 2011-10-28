package net.ion.radon.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.radon.repository.advance.TestAllAdvance;

public class TestAllSimpleRepository extends TestCase {
	
	public static Test suite() throws Exception{
		TestSuite suite = new TestSuite("Test All Simple Repository");
		
		suite.addTest(TestAllNode.suite()) ;
		suite.addTest(TestAllQuery.suite()) ;
		suite.addTest(TestAllWorkspace.suite()) ;

		suite.addTest(TestAllInList.suite()) ;
		suite.addTest(TestAllInner.suite()) ;
		suite.addTest(TestAllAdvance.suite()) ;
		
		suite.addTest(TestAllCommon.suite()) ;


		return suite;
	}
}
