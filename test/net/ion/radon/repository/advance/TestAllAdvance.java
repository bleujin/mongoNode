package net.ion.radon.repository.advance;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.radon.repository.TestNodeRows;
import net.ion.radon.repository.TestNodeSerialize;
import net.ion.radon.repository.TestSequence;

public class TestAllAdvance extends TestCase {

	
	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite("Test Advanced Use");
		suite.addTestSuite(TestSequence.class) ;
		suite.addTestSuite(TestExplain.class);
		suite.addTestSuite(TestIndex.class);
		suite.addTestSuite(TestNodeRows.class);
		suite.addTestSuite(TestNodeSerialize.class);
		
		// suite.addTestSuite(TestORM.class) ;
		
		return suite ;
	}

}