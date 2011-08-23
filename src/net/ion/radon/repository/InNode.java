package net.ion.radon.repository;

import net.ion.framework.db.RepositoryException;
import net.sf.json.JSONObject;

public interface InNode extends IPropertyFamily, INode  {

	public InNode put(String key, Object val) ;

	public InNode append(String key, Object val) ;
	
	public InNode putEncrypt(String key, String value)  throws RepositoryException;

	public InNode append(JSONObject json);

	public InQuery createQuery();

}
