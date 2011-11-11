package net.ion.radon.repository;

import net.ion.framework.util.Debug;
import net.ion.radon.repository.relation.IRelation;

public class NodeRelation implements IRelation {

	private Node parent;
	private InListNode relList;
	private String relType ;

	private NodeRelation(Node parent, InListNode relList, String relType) {
		this.parent = parent;
		this.relList = relList;
		this.relType = relType ;
	}

	public static NodeRelation load(Node parent, InListNode relList, String relType) {
		return new NodeRelation(parent, relList, relType);
	}

	public Node fetch(int index) {
		if (relList.size() <= index)
			return null ;

		return parent.getQuery().findOne(parent.getSession(), getWsName(index), getQuery(), getRefQuery(index));
	}

	private PropertyQuery getRefQuery(int index) {
		InNode inref = (InNode) relList.get(index) ;
		return NodeRef.load(inref).toQuery() ;
	}

	private String getWsName(int index) {
		InNode inref = (InNode) relList.get(index);

		return inref.getString(NodeRef.TARAGET_REF);
	}

	public NodeCursor fetchs() {
		return ProxyFromCursor.create(parent, this);
	}

	public PropertyQuery getQuery() {
		return parent.getQuery();
	}

	public InListNode getRelation() {
		return relList;
	}

	public int remove() {
		return parent.inner(NodeConstants.RELATION).inlist(relType).createQuery().remove() ;
		// return parent.getSession().createQuery().id(parent.getIdentifier()).inlist(NodeConstants.RELATION + "." + relType).pull() ;
	}


}
