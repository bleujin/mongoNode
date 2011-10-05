package net.ion.radon.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllQuery extends TestCase{

	public static Test suite() {
		TestSuite result = new TestSuite("Test Query") ;

		result.addTestSuite(TestQueryOperator.class);
		result.addTestSuite(TestQueryMDL.class) ;
		result.addTestSuite(TestAdvanceQuery.class) ;


		return result;
	}

}
