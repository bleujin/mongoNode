package net.ion.radon.repository;

import net.ion.radon.repository.function.TestFunction;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllNode extends TestCase{

	public static Test suite() {
		TestSuite suite = new TestSuite("To Use Node") ;
		suite.addTestSuite(TestCreate.class) ;
		suite.addTestSuite(TestUpdate.class);
		suite.addTestSuite(TestRemove.class);
		suite.addTestSuite(TestRootNode.class);
		suite.addTestSuite(TestFind.class);
		suite.addTestSuite(TestColumns.class);
		
		
		
		suite.addTestSuite(TestNodeCursor.class) ;
		suite.addTestSuite(TestAdvanceNode.class) ;
		suite.addTestSuite(TestAradonId.class);
		suite.addTestSuite(TestFunction.class);
		suite.addTestSuite(TestInnerNode.class) ;

		
		// working
		suite.addTestSuite(TestTempNode.class) ;
		suite.addTestSuite(TestLastModified.class) ;
		
		return suite ;
	}

	
}