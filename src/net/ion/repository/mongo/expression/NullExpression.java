package net.ion.repository.mongo.expression;

import net.ion.repository.mongo.node.NodeCommon;

public final class NullExpression implements Expression {
	private NullExpression() {
	}

	public static final NullExpression instance = new NullExpression();

	@Override
	public Comparable value(NodeCommon node) {
		return null;
	}


}
