package net.ion.radon.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.radon.repository.util.TestJSONMessage;

public class TestAllCommon extends TestCase{

	public static Test suite() {
		TestSuite result = new TestSuite("Test Common") ;
		
		result.addTestSuite(TestPropertyFamily.class) ;
		result.addTestSuite(TestJSONMessage.class) ;
		
		return result;
	}

}
