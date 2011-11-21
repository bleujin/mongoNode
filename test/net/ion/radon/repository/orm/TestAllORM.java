package net.ion.radon.repository.orm;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllORM extends TestCase{

	public static TestSuite suite(){
		TestSuite result = new TestSuite("Test All ORM");
		
		result.addTestSuite(TestPeople.class );
		result.addTestSuite(TestPeopleFind.class );
		result.addTestSuite(TestEmployee.class );
		
		return result ;
	}
}

