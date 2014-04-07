package net.ion.repository.mongo.expression;

import java.lang.reflect.InvocationTargetException;

import net.ion.repository.mongo.node.NodeCommon;
import net.ion.repository.mongo.util.Filters;

import org.apache.commons.lang.reflect.MethodUtils;
import org.apache.lucene.search.Filter;

public final class UnaryExpression extends ValueObject implements Expression {
	public final Expression operand;
	public final Op operator;

	public UnaryExpression(Op operator, Expression operand) {
		this.operand = operand;
		this.operator = operator;
	}

	@Override
	public Comparable value(NodeCommon node) {
		return operator.compute(operand.value(node));
	}
	
	
	public Filter filter(){
		try {
			Filter filter = (Filter) MethodUtils.invokeMethod(operand, "filter", new Object[0]);
			return Filters.not(filter) ;
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("can't make filter : " + operand) ;
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("can't make filter : " + operand) ;
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("can't make filter : " + operand) ;
		}
	}

}