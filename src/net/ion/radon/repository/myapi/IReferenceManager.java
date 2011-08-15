package net.ion.radon.repository.myapi;

import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.ReferenceTaragetCursor;

public interface IReferenceManager {

	public INode setReference(String src, String relType, String target) ;
	public INode setReference(Node src, String relType, Node target) ;
	

	public ReferenceTaragetCursor findReference(String srcId) ;
	public ReferenceTaragetCursor findReference(String srcId, String relType) ;
	public ReferenceTaragetCursor findReference(String srcId, String relType, String targetId) ;
	public ReferenceTaragetCursor findReference(INode src) ;
	public ReferenceTaragetCursor findReference(INode src, String relType, String targetId) ;

	
	
	public NodeResult removeReference(String srcId) ;
	public NodeResult removeReference(String srcId, String relType);
	public NodeResult removeReference(String srcId, String relType, String targetId) ;
	public NodeResult removeReference(INode src) ;
	public NodeResult removeReference(INode src, String relType, String targetId);

	public ReferenceTaragetCursor getChildReference(INode src) ;
	public ReferenceTaragetCursor getChildReference(INode src, String cname) ;

}
