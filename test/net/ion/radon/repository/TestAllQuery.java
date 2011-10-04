package net.ion.radon.repository;

import net.ion.radon.repository.innode.TestInListMDL;
import net.ion.radon.repository.innode.TestInListNodeQuery;
import net.ion.radon.repository.innode.TestInListQuery;
import net.ion.radon.repository.innode.TestInListQuerySort;
import net.ion.radon.repository.innode.TestInPut;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllQuery extends TestCase{

	public static Test suite() {
		TestSuite result = new TestSuite("Test Query") ;

		result.addTestSuite(TestQueryOperator.class);
		result.addTestSuite(TestQueryMDL.class) ;
		result.addTestSuite(TestAdvanceQuery.class) ;


		// test inlist
		result.addTestSuite(TestInPut.class) ;
		result.addTestSuite(TestInListQuery.class) ;
		result.addTestSuite(TestInListQuerySort.class) ;
		result.addTestSuite(TestInListMDL.class) ;
		result.addTestSuite(TestInListNodeQuery.class) ;
		
		return result;
	}

}
