package net.ion.radon.repository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.ion.framework.db.RepositoryException;
import net.ion.radon.core.PageBean;
import net.sf.json.JSONObject;

import com.mongodb.DBObject;

public interface InNode extends IPropertyFamily, INode  {

	public InNode put(String key, Object val) ;

	public InNode append(String key, Object val) ;
	
	public InNode putEncrypt(String key, String value)  throws RepositoryException;

	public InNode append(JSONObject json);

	public InQuery createQuery();

}
