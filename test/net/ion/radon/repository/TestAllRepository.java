package net.ion.radon.repository;

import net.ion.radon.impl.let.TestObjectLet;
import net.ion.radon.repository.function.TestColumn;
import net.ion.radon.repository.innode.TestAllInQuery;
import net.ion.radon.repository.innode.TestInListQuery;
import net.ion.radon.repository.innode.TestInListQuerySort;
import net.ion.radon.repository.innode.TestInListMDL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllRepository extends TestCase{

	public static Test suite() throws Exception{
		TestSuite ts = new TestSuite("RepositoryAll");
		
		ts.addTestSuite(TestSession.class);
		ts.addTestSuite(TestWorkspace.class);
		ts.addTestSuite(TestUse.class);
		ts.addTestSuite(TestMongoIO.class);
		ts.addTestSuite(TestNodeSerialize.class);
		ts.addTestSuite(TestUpdate.class);
		ts.addTestSuite(TestNode.class);
		ts.addTestSuite(TestReference.class);
		ts.addTestSuite(TestNodeReference.class);
		ts.addTestSuite(TestRootNode.class);

		
		ts.addTestSuite(TestColumn.class);
		ts.addTestSuite(TestNodeRows.class);
		ts.addTestSuite(TestQueryOperator.class);
		ts.addTestSuite(TestAdvanceQuery.class) ;
		ts.addTestSuite(TestInnerNode.class) ;
		
		
		ts.addTest(TestAllInQuery.suite()) ;
		return ts;
	}
}
