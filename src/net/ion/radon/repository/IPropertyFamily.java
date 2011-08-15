package net.ion.radon.repository;

import java.io.Serializable;
import java.util.Map;

import com.mongodb.DBObject;

public interface IPropertyFamily extends Serializable {
	
	public DBObject getDBObject() ;
	public Map<String, ? extends Object> toMap() ; 

}
