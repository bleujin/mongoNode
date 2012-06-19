package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ARADON_GROUP;
import static net.ion.radon.repository.NodeConstants.ARADON_UID;

import java.io.Serializable;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class WorkspaceOption implements IPropertyFamily, Serializable{
	private static final long serialVersionUID = -8022714785589409998L;
	public final static WorkspaceOption EMPTY = new WorkspaceOption(new BasicDBObject(), true) ;
	public final static WorkspaceOption NONE = new WorkspaceOption(new BasicDBObject(), false) ;
	
	private DBObject ioption ;
	private boolean createAradonIndex = true ;
	
	private WorkspaceOption(DBObject option, boolean createAradonIndex){
		this.ioption = option ;
		this.createAradonIndex = createAradonIndex ;
	}
	
	public final static WorkspaceOption createByMax(int maxRow){
		return createByMax(maxRow, maxRow * 10 * 1024) ;
	}
	
	public final static WorkspaceOption createByMax(int maxRow, int size){
		BasicDBObject dbo = new BasicDBObject() ;
		dbo.put("capped", true) ;
		dbo.put("max", maxRow) ;
		dbo.put("size", size) ;
		
		return new WorkspaceOption(dbo, true) ;
	}
	
	public final static WorkspaceOption createNone(){
		return NONE ;
	}
	
	private static PropertyFamily ARADON_INDEX = PropertyFamily.create(ARADON_GROUP, 1).put(ARADON_UID, -1);
	private static PropertyFamily PATH_INDEX = PropertyFamily.create(NodeConstants.PATH, 1);

	void initWorkspace(DBCollection dc){
		if (createAradonIndex){
			BasicDBObject aradon_options = new BasicDBObject();
			aradon_options.put("name", "_aradon_id");
			aradon_options.put("unique", Boolean.TRUE);
			dc.ensureIndex(ARADON_INDEX.getDBObject(), aradon_options);
			
			BasicDBObject path_options = new BasicDBObject();
			path_options.put("name", "_path_id");
			path_options.put("unique", Boolean.TRUE);
			
			dc.ensureIndex(PATH_INDEX.getDBObject(), path_options);
		}
	}
	 	
	
	public DBObject getDBObject() {
		return ioption;
	}
	
	public Map<String, ? extends Object> toMap() {
		return ioption.toMap();
	}
}



