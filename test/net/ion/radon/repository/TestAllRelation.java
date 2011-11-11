package net.ion.radon.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllRelation extends TestCase{

	public static Test suite() {
		TestSuite result = new TestSuite("Test Node Relation") ;
		
		
		result.addTestSuite(TestAradonRefImpl.class) ;
		result.addTestSuite(TestToRelation.class) ;
		result.addTestSuite(TestGetRelation.class) ;
		result.addTestSuite(TestMyRef.class) ;
		
		
		result.addTestSuite(TestParentChild.class);
		result.addTestSuite(TestRelationMethod.class);
		result.addTestSuite(TestFindRelation.class) ;
		result.addTestSuite(TestAdvanceRelation.class);

		return result;
	}

}
