package net.ion.repository.mongo.expression;

import net.ion.repository.mongo.node.NodeCommon;

public interface Expression {
	public Comparable value(NodeCommon node) ;
}

