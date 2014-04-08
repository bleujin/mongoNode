package net.ion.repository.mongo;

import net.ion.repository.mongo.db.TestCrakenDBManager;
import net.ion.repository.mongo.script.TestCrakenScript;
import net.ion.repository.mongo.vfs.TestNodeVfs;
import net.ion.script.rhino.TestAsync;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllWorkspace {
	public static Test suite() {
		TestSuite suite = new TestSuite("To Use Workspace");
		
		suite.addTestSuite(TestReadSession.class);
		suite.addTestSuite(TestWriteSession.class);
		suite.addTestSuite(TestCollection.class);
		
		suite.addTestSuite(TestMapReduce.class);
		
		
		// advance
		suite.addTestSuite(TestAsync.class); 
		suite.addTestSuite(TestNodeVfs.class);
		suite.addTestSuite(TestCrakenScript.class); 
		suite.addTestSuite(TestCrakenDBManager.class); 
		return suite;
	}
}
