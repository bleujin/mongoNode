package net.ion.radon.repository.relation;

import net.ion.radon.repository.InListNode;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeRef;
import net.ion.radon.repository.PropertyQuery;

public interface IRelation {
	public Node fetch(int index)  ;
	public NodeCursor fetchs();
	public PropertyQuery getQuery();
	public InListNode getRelation() ;
	public int remove();
	// add minato
	public int remove(NodeRef selfRef);
//	public int remove(NodeRef nref);
	public NodeCursor froms();
}
