package net.ion.radon.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Columns {

	private boolean include ;
	private NodeObject no ;
	
	public final static Columns ALL = new Columns(true) ;
	
	private Columns(boolean include){
		this.no = NodeObject.create() ;
		this.include = include ;
	}
	
	
	public static Columns exclude() {
		return new Columns(false);
	}

	public static Columns append() {
		return new Columns(true);
	}

	
	public Columns add(String... columns) {
		for (String col : columns) {
			no.put(col, include ? 1 : 0) ;	
		}
		return this;
	}

	public DBObject getDBOjbect() {
		return no.getDBObject();
	}


	public Columns slice(String key, int limit) {
		return slice(key, 0, limit);
	}

	public Columns slice(String key, int skip, int limit) {
		if (! include) throw new IllegalArgumentException("only include expression") ;
		no.put(key, new BasicDBObject("$slice", new int[]{skip, limit})) ;
		return this;
	}


}
