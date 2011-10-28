package net.ion.radon.repository;

import java.io.Serializable;
import java.util.Map;

import net.ion.framework.util.ChainMap;

import com.mongodb.DBObject;

public interface INode {

	public InNode inner(String name) ;
	
	public Serializable get(String propId) ;

	public Serializable get(String propId, int index);
	
	public INode put(String key, Object value) ;

	public INode append(String key, Object value) ;
	
	public int getAsInt(String propId) ;

	public DBObject getDBObject();
	
	public Map<String, ? extends Object> toPropertyMap();
	
	public Map<String, ? extends Object> toPropertyMap(NodeColumns cols);
	
	public void putAll(Map<String, ? extends Object> props) ;

	public void putAll(ChainMap<String, ? extends Object> createByMap);
	
	public Map<String, ? extends Object> toMap() ;
	
	public void clearProp() ;

	public String getString(String key);

	public boolean hasProperty(String key);

	public boolean isMatchEncrypted(String key, String value);

	public void notify(NodeEvent nevent);
	
}
