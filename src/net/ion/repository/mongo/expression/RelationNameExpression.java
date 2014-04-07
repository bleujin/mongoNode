package net.ion.repository.mongo.expression;

import net.ion.repository.mongo.node.NodeCommon;

public final class RelationNameExpression extends ValueObject implements Expression {
	
	public final RelationName qname;

	public RelationNameExpression(RelationName qname) {
		this.qname = qname;
	}

	public static RelationNameExpression of(String... names) {
		return new RelationNameExpression(RelationName.of(names));
	}

	@Override
	public Comparable value(NodeCommon node) {
		// TODO Auto-generated method stub
		return null;
	}

}
