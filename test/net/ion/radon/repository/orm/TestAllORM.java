package net.ion.radon.repository.orm;

import net.ion.radon.repository.orm.inlist.TestArticle;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllORM extends TestCase{

	public static TestSuite suite(){
		TestSuite result = new TestSuite("Test All ORM");
		
		result.addTestSuite(TestPeople.class );
		result.addTestSuite(TestPeopleFind.class );
		result.addTestSuite(TestEmployee.class );
		result.addTestSuite(TestGenericManager.class) ;
		
		result.addTestSuite(TestArticle.class) ;
		
		return result ;
	}
}

