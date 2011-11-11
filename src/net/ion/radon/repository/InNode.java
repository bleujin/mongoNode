package net.ion.radon.repository;

import net.ion.framework.db.RepositoryException;

public interface InNode extends IPropertyFamily, INode  {

	
	public INode getParent() ;
	
	public InNode put(String key, Object val) ;
	
	public InNode put(PropertyId key, Object val) ;

	public InNode append(String key, Object val) ;
	
	public InNode putEncrypt(String key, String val)  throws RepositoryException;

	public InListNode inlist(String key);
}
