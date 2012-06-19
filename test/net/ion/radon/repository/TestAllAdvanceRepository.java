package net.ion.radon.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.radon.repository.mr.TestAllMapReduce;
import net.ion.radon.repository.orm.TestAllORM;
import net.ion.radon.repository.speed.TestAllSpeed;

public class TestAllAdvanceRepository extends TestCase{
	
	public static Test suite() throws Exception{
		TestSuite suite = new TestSuite("Test All Advance Repository");
		
		suite.addTest(TestAllSpeed.suite()) ;
		suite.addTest(TestAllRelation.suite()) ;
		suite.addTest(TestAllMapReduce.suite()) ;
		suite.addTest(TestAllORM.suite()) ;

		return suite;
	}
	
}
