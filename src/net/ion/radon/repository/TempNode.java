package net.ion.radon.repository;

import java.io.Serializable;
import java.util.Map;

import org.bson.types.ObjectId;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.ChainMap;
import net.ion.radon.repository.innode.TempInNode;

import com.mongodb.DBObject;

public interface TempNode extends INode, IPropertyFamily{

	public Session getSession();

	// @TODO if prop has '.' 
	public Serializable get(String propId) ;

	public Serializable get(String propId, int index) ;
	
	public int getAsInt(String propId) ;

	public DBObject getDBObject() ;

	public TempNode putEncrypt(String key, String value) throws RepositoryException ;

	public boolean isMatchEncrypted(String key, String value) ;

	public void putAll(Map<String, ? extends Object> props) ;

	public void putAll(ChainMap<String, ? extends Object> props) ;
	
	public TempNode put(String key, Object val);

	public TempNode append(String key, Object val);

	public Map<String, Serializable> toMap() ;

	public Map<String, ? extends Object> toPropertyMap() ;

	public Map<String, ? extends Object> toPropertyMap(NodeColumns cols) ;

	public Object getId();

	public String getString(String key) ;

	public void clearProp() ;

	public boolean hasProperty(String key) ;

	public TempInNode inner(String name) ;
	
	public InListNode inlist(String name) ;

	public TempNode putProperty(PropertyId id, Object value);

}
