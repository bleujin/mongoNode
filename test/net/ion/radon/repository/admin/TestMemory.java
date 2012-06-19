package net.ion.radon.repository.admin;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.Gson;
import net.ion.framework.parse.gson.GsonBuilder;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;

import com.mongodb.CommandResult;

public class TestMemory extends TestCase {

	public void testViewMemory() throws Exception {
		RepositoryCentral rc = RepositoryCentral.testCreate() ;
		
		Session session = rc.login("bptest") ;

		
		
		
		
		DBStatus ds = rc.getDBStatus() ;
		Debug.line(ds.db().getName()) ;
		
		// Debug.line(prettyJson(ds.db().getStats())) ; 
		// Debug.line(prettyJson(ds.db().command("serverStatus"))) ;
		
	}
	
	private String prettyJson(CommandResult result){
		new GsonBuilder().setPrettyPrinting().toString() ;
		Gson gson = new GsonBuilder().setPrettyPrinting().create() ;

		return gson.toJson(JsonObject.fromString(result.toString())) ;
	}
	
}
