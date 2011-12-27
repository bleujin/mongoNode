package net.ion.radon.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.radon.repository.innode.TestInListMDL;
import net.ion.radon.repository.innode.TestInListNode;
import net.ion.radon.repository.innode.TestInListQueryNode;
import net.ion.radon.repository.innode.TestInListQueryNodeMDL;
import net.ion.radon.repository.innode.TestInListQuerySort;
import net.ion.radon.repository.innode.TestInPut;
import net.ion.radon.repository.innode.TestInListQuery;

public class TestAllInList extends TestCase{

	public static Test suite() {
		
		TestSuite result = new TestSuite("Test InList") ;
		
		result.addTestSuite(TestInPut.class) ;
		result.addTestSuite(TestInListNode.class) ;
		result.addTestSuite(TestInListQuery.class) ;
		result.addTestSuite(TestInListQuerySort.class) ;
		result.addTestSuite(TestInListMDL.class) ;
		result.addTestSuite(TestInListQueryNode.class) ;
		result.addTestSuite(TestInListQueryNodeMDL.class) ;
		
		return result;
	}

}
