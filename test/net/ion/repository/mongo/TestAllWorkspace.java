package net.ion.repository.mongo;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllWorkspace {
	public static Test suite() {
		TestSuite suite = new TestSuite("To Use Workspace");
		
		suite.addTestSuite(TestReadSession.class);
		suite.addTestSuite(TestWriteSession.class);
		suite.addTestSuite(TestCollection.class);
		
		suite.addTestSuite(TestMapReduce.class);
		return suite;
	}
}
