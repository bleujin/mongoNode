package net.ion.radon.repository;

import java.io.Serializable;

import net.ion.framework.util.ListUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Columns implements Serializable{

	private static final long serialVersionUID = -6553780481603324409L;
	private boolean include ;
	private NodeObject no ;
	
	public final static Columns ALL = new Columns(true) ;
	
	public static final String[] MetaColumns = ListUtil.toList(NodeConstants.ID, NodeConstants.OWNER, NodeConstants.LASTMODIFIED, NodeConstants.CREATED, NodeConstants.TIMEZONE, NodeConstants.NAME, NodeConstants.PATH, NodeConstants.ARADON, NodeConstants.RELATION).toArray(new String[0]);
	public static final Columns Meta = Columns.append().add(MetaColumns);
	
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
		if (! include) throw new IllegalArgumentException("only supported include-expression") ;
		no.put(key, new BasicDBObject("$slice", new int[]{skip, limit})) ;
		return this;
	}


}
