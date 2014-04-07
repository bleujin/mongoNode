package net.ion.repository.mongo.expression;

import net.ion.repository.mongo.node.NodeCommon;

public final class WildcardExpression extends ValueObject implements Expression {
	public final QualifiedName owner;

	public WildcardExpression(QualifiedName owner) {
		this.owner = owner;
	}

	@Override
	public Comparable value(NodeCommon node) {
		ComparableSet set = new ComparableSet() ;
		
		return null;
	}
	

}

