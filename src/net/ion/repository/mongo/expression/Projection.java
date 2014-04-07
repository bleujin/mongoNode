package net.ion.repository.mongo.expression;

import net.ion.framework.util.StringUtil;
import net.ion.repository.mongo.node.ReadNode;

public final class Projection extends ValueObject {
	private final Expression expression;
	private final String alias;

	public Projection(Expression expression, String alias) {
		this.expression = expression;
		this.alias = alias;
	}
	
	public Object value(ReadNode node){
		return expression.value(node) ;
	}

	
	public String label(){
		if (StringUtil.isNotBlank(alias)){
			return alias ;
		}
		if (expression instanceof QualifiedNameExpression){
			return ((QualifiedNameExpression)expression).lastName() ;
		}
		
		return "";
	}
}
