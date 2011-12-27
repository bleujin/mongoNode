package net.ion.radon.repository.speed;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllSpeed extends TestCase{

	public static Test suite() {
		TestSuite ts = new TestSuite("Test All Speed") ;
		
		ts.addTestSuite(TestIndex.class) ;
		ts.addTestSuite(TestUniqueIndex.class) ;
		
		
		
		return ts;
	}
	
	

	
}
