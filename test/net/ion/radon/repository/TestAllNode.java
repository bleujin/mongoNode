package net.ion.radon.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.radon.repository.function.TestFunction;

public class TestAllNode extends TestCase{

	public static Test suite() {
		TestSuite suite = new TestSuite("To Use Node") ;
		suite.addTestSuite(TestCreate.class) ;
		suite.addTestSuite(TestUpdate.class);
		suite.addTestSuite(TestRemove.class);
		suite.addTestSuite(TestRootNode.class);
		suite.addTestSuite(TestFind.class);
		suite.addTestSuite(TestColumns.class);
		suite.addTestSuite(TestAdvanceColumns.class) ;
		suite.addTestSuite(TestTemporaryNode.class) ;
		suite.addTestSuite(TestMergeNode.class) ;
		
		suite.addTestSuite(TestToMap.class) ;
		suite.addTestSuite(TestAppend.class) ;
		suite.addTestSuite(TestParticialNode.class) ;
		
		suite.addTestSuite(TestNodeCursor.class) ;
		suite.addTestSuite(TestAdvanceNode.class) ;
		suite.addTestSuite(TestAdvanceNodeGet.class) ;
		suite.addTestSuite(TestAradonId.class);
		suite.addTestSuite(TestFunction.class);
		suite.addTestSuite(TestInnerNode.class) ;

		
		// working
		suite.addTestSuite(TestLastModified.class) ;
		
		return suite ;
	}

	
}
