package net.ion.radon.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.radon.repository.mr.TestAllMapReduce;

public class TestAllAdvanceRepository extends TestCase{
	
	public static Test suite() throws Exception{
		TestSuite suite = new TestSuite("Test All Advance Repository");
		
		suite.addTest(TestAllRelation.suite()) ;
		suite.addTest(TestAllMapReduce.suite()) ;

		return suite;
	}
	
}
