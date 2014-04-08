package net.ion.repository.mongo.script;

import java.io.File;
import java.util.concurrent.Executors;

import net.ion.repository.mongo.TestBaseReset;

public class TestCrakenScript extends TestBaseReset{

	public void testCreate() throws Exception {
		CrakenScript cs = CrakenScript.create(session, Executors.newScheduledThreadPool(1)) ;
		cs.readDir(new File("./test/net/ion/repository/mongo/script"), true) ;
		
		
	}
}
