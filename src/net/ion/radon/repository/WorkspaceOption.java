package net.ion.radon.repository;

import java.io.Serializable;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class WorkspaceOption implements IPropertyFamily, Serializable{
	private static final long serialVersionUID = -8022714785589409998L;
	public final static WorkspaceOption EMPTY = new WorkspaceOption(null) ;
	
	private DBObject ioption ;
	private WorkspaceOption(DBObject option){
		this.ioption = option ;
	}
	
	public final static WorkspaceOption createByMax(int maxRow){
		
		BasicDBObject dbo = new BasicDBObject() ;
		dbo.put("capped", true) ;
		dbo.put("max", maxRow) ;
		dbo.put("size", maxRow * 100 * 1024) ;
		
		return new WorkspaceOption(dbo) ;
	}

	public DBObject getDBObject() {
		return ioption;
	}
	
	public Map<String, ? extends Object> toMap() {
		return ioption.toMap();
	}
}



