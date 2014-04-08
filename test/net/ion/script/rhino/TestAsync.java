package net.ion.script.rhino;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.util.WriteJobs;

public class TestAsync extends TestBaseReset {

	public void testAsync() throws Exception {
		session.tranAsync(WriteJobs.HELLO).get() ;
		assertEquals(true, session.exists("/bleujin")) ;
	}
	
	public void testChangeExecutorService() throws Exception {
		session.workspace().executorService(Executors.newCachedThreadPool()) ;
		Future<Void> future = session.tranAsync(WriteJobs.HELLO) ;
		
		assertEquals(false, session.exists("/bleujin")) ;
		future.get() ;
		assertEquals(true, session.exists("/bleujin")) ;
	}
}
