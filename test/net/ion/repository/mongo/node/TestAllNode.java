package net.ion.repository.mongo.node;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllNode  extends TestCase{

	public static Test suite() {
		TestSuite suite = new TestSuite("To Use Node") ;
		suite.addTestSuite(TestPathBy.class);
		suite.addTestSuite(TestCreate.class) ;
		suite.addTestSuite(TestUpdate.class);
		suite.addTestSuite(TestRemove.class);
		suite.addTestSuite(TestAppend.class) ;
		

		suite.addTestSuite(TestRootNode.class);
		suite.addTestSuite(TestGhostBy.class);
		suite.addTestSuite(TestReadNode.class) ;
		suite.addTestSuite(TestWriteNode.class);

		suite.addTestSuite(TestPropertyValue.class);
		
		suite.addTestSuite(TestIndex.class);
		suite.addTestSuite(TestReference.class);
		suite.addTestSuite(TestReadChildren.class);
		suite.addTestSuite(TestWriteChildren.class) ;
		suite.addTestSuite(TestRefChildren.class);
		suite.addTestSuite(TestToPropMap.class) ;
		
		suite.addTestSuite(TestToRows.class);
//		suite.addTestSuite(TestAdvanceColumns.class) ;
//		suite.addTestSuite(TestNodeCursor.class) ;
//		suite.addTestSuite(TestAdvanceNodeGet.class) ;
		
		
//		suite.addTestSuite(TestTemporaryNode.class) ;
//		suite.addTestSuite(TestMergeNode.class) ;
//		suite.addTestSuite(TestParticialNode.class) ;
//		suite.addTestSuite(TestAdvanceNode.class) ;
//		suite.addTestSuite(TestGetRegularExpression.class) ;
//		suite.addTestSuite(TestAradonId.class);
//		suite.addTestSuite(TestFunction.class);
//		suite.addTestSuite(TestInnerNode.class) ;

		return suite ;
	}
}
