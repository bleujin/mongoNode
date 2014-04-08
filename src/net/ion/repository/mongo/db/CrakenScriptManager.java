package net.ion.repository.mongo.db;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import net.ion.framework.db.Rows;
import net.ion.repository.mongo.ReadSession;
import net.ion.repository.mongo.script.CrakenScript;

public class CrakenScriptManager extends CrakenManager {

	private final CrakenScript cs;
	private CrakenScriptManager(CrakenScript cs) {
		this.cs = cs ;
	}

	public static CrakenScriptManager create(ReadSession rsession, ScheduledExecutorService ses, File baseScriptDir) throws IOException {
		CrakenScript cs = new CrakenScript(rsession, ses) ;
		cs.readDir(baseScriptDir) ;
		
		return new CrakenScriptManager(cs) ;
	}
	
	public static CrakenScriptManager create(CrakenScript cs){
		return new CrakenScriptManager(cs) ;
	}

	@Override
	public Rows queryBy(CrakenUserProcedure cupt) throws Exception {
		return cs.execQuery(cupt.getProcName(), cupt.getParams().toArray(new Object[0])) ;
	}

	@Override
	public int updateWith(CrakenUserProcedure cupt) throws Exception {
		return cs.execUpdate(cupt.getProcName(), cupt.getParams().toArray(new Object[0])) ;
	}

	@Override
	public int updateWith(CrakenUserProcedureBatch cupt) throws Exception {
		return cs.execUpdate(cupt.getProcName(), cupt.getParams().toArray(new Object[0])) ;
	}

	
	

}
