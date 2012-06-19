package net.ion.radon.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllInner extends TestCase{

	public static Test suite() {
		TestSuite result = new TestSuite("Test Inner") ;

		result.addTestSuite(TestBasicInner.class) ;
		
		return result;
	}

	
}
