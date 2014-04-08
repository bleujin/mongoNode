package net.ion.repository.mongo.db;

import java.io.File;
import java.util.concurrent.Executors;

import net.ion.framework.db.DBController;
import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.script.CrakenScript;

public class TestCrakenDBManager extends TestBaseReset {

	private DBController dc;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		CrakenScript cs = CrakenScript.create(session, Executors.newScheduledThreadPool(2)) ;
		cs.readDir(new File("./test/net/ion/repository/mongo/script")) ;
		CrakenScriptManager csm = CrakenScriptManager.create(cs) ;
		this.dc = new DBController(csm) ;
		dc.initSelf(); 
	}
	
	@Override
	protected void tearDown() throws Exception {
		dc.destroySelf();
		super.tearDown();
	}
	
	public void testCreate() throws Exception {
		dc.createUserProcedure("afield@createWith(?,?)").addParam("name").addParam("user name").execUpdate() ;
		dc.createUserProcedure("afield@listBy(?,?)").addParam(0).addParam(10).execQuery().debugPrint();
		
	}
}
