package net.ion.radon.repository.innode;

import net.ion.framework.db.RepositoryException;
import net.ion.radon.repository.INode;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.TempNode;

import com.mongodb.DBObject;

public interface TempInNode extends InNode {
	
	public TempNode getParent() ;

	public TempInNode put(String key, Object val) ;

	public TempInNode append(String key, Object val) ;
	
	public TempInNode putEncrypt(String key, String val)  throws RepositoryException;
}


class TempInNodeImpl extends InNodeImpl implements TempInNode {
	private static final long serialVersionUID = 6451705402355689255L;

	TempInNodeImpl(DBObject dbo, String pname, INode parent, int index) {
		super(dbo, pname, parent, index) ;
	}
	
	public TempNode getParent(){
		return (TempNode)super.getParent() ;
	}
	
	public TempInNode put(String key, Object val){
		return (TempInNode)super.put(key, val) ;
	}

	public TempInNode append(String key, Object val) {
		return (TempInNode)super.append(key, val) ;
	}
	
	public TempInNode putEncrypt(String key, String val)  throws RepositoryException {
		return (TempInNode)super.putEncrypt(key, val) ;
	}
}