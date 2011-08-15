package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ARADON;

import com.mongodb.BasicDBObject;

public class ReferenceObject extends BasicDBObject{

	
	public ReferenceObject(String workspaceName, Object id) {
		super() ;
		this.put("workspace", workspaceName) ;
		this.put("id", id) ;
	}
	
	public ReferenceObject(InNode dbo){
		this.put("workspace", dbo.getString("workspace")) ;
		this.put("id", dbo.get("id")) ;
		this.put(ARADON, dbo.get(ARADON));
	}

	public String getWorkspaceName() {
		return getString("workspace");
	}
	
	public Object getId(){
		return get("id") ;
	}
	
	public void setProperty(String key, String value){
		this.put(key, value);
	}
	

}
