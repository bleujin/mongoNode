package net.ion.radon.repository.admin;

import com.mongodb.DB;

public class DBStatus {

	// http://docs.mongodb.org/manual/reference/server-status/
	
	private DB system ;
	private DB db ;
	private DBStatus(DB system, DB db) {
		this.system = system ;
		this.db = db ;
	}

	public static DBStatus create(DB system, DB db) {
		return new DBStatus(system, db);
	}

	DB db(){
		return db ;
	}

	DB system() {
		return system ;
	}
	
}
