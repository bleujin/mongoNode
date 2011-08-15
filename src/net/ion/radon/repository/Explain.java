package net.ion.radon.repository;

import com.mongodb.DBObject;

public class Explain {

	private DBObject dbo ;
	private Explain(DBObject dbo) {
		this.dbo = dbo ;
	}

	public static Explain load(DBObject dbo) {
		return new Explain(dbo);
	}
	
	public boolean useIndex(){
		return ! "BasicCursor".equals(dbo.get("cursor")) ;
	}
	
	public String toString(){
		return dbo.toString() ;
	}

	public String useIndexName() {
		return dbo.get("cursor").toString();
	}
	

}
