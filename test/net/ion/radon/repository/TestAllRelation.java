package net.ion.radon.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllRelation extends TestCase{

	public static Test suite() {
		TestSuite result = new TestSuite("Test Node Relation") ;
		
		result.addTestSuite(TestParentChild.class);
		result.addTestSuite(TestGeneralRelation.class);
		result.addTestSuite(TestFindRelation.class) ;
		result.addTestSuite(TestAdvanceRelation.class);
		result.addTestSuite(TestAdvanceRelation2.class);

		return result;
	}

}
