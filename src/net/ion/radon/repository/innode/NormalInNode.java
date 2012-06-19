package net.ion.radon.repository.innode;

import net.ion.framework.db.RepositoryException;
import net.ion.radon.repository.INode;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.Node;

import com.mongodb.DBObject;

public interface NormalInNode extends InNode {
	
	public Node getParent() ;

	public NormalInNode put(String key, Object val) ;

	public NormalInNode append(String key, Object val) ;
	
	public NormalInNode putEncrypt(String key, String val)  throws RepositoryException;

}



class NormalInNodeImpl extends InNodeImpl implements NormalInNode{

	private static final long serialVersionUID = 6451705402355689255L;

	NormalInNodeImpl(DBObject dbo, String pname, INode parent, int index) {
		super(dbo, pname, parent, index) ;
	}
	
	public Node getParent(){
		return (Node)super.getParent() ;
	}
	
	public NormalInNode put(String key, Object val){
		return (NormalInNode)super.put(key, val) ;
	}

	public NormalInNode append(String key, Object val) {
		return (NormalInNode)super.append(key, val) ;
	}
	
	public NormalInNode putEncrypt(String key, String val)  throws RepositoryException {
		return (NormalInNode)super.putEncrypt(key, val) ;
	}
}
