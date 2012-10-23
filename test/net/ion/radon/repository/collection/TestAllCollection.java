package net.ion.radon.repository.collection;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllCollection extends TestCase {

	public static TestSuite suite(){
		TestSuite result = new TestSuite("Test All Collection") ;
		
		result.addTestSuite(TestCollectionFactory.class) ;
		result.addTestSuite(TestMongoMap.class) ;
		result.addTestSuite(TestMongoQueue.class) ;
		
		return result ;
	}
	
}
