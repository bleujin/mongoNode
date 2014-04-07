package net.ion.repository.mongo.expression;

import net.ion.framework.util.NumberUtil;
import net.ion.repository.mongo.node.NodeCommon;
import net.ion.repository.mongo.util.Filters;

import org.apache.lucene.search.Filter;

public final class NumberExpression extends ValueObject implements Expression , ConstantExpression{
	public final String number;

	public NumberExpression(String number) {
		this.number = number;
	}

	@Override
	public Comparable value(NodeCommon node) {
		return NumberUtil.createBigDecimal(number);
	}
	
	public Object constantValue(){
		return NumberUtil.createBigDecimal(number) ;
	}
	
	@Override
	public Filter filter(Op operand, QualifiedNameExpression qne) {
		String field = qne.lastName();
		long longValue = Long.parseLong(number) ;
		if( operand == Op.EQ){
			return Filters.eq(qne.lastName(), longValue) ;
		} else if (operand == Op.CONTAIN) {
			return Filters.eq(qne.lastName(), longValue) ;
		} else if (operand == Op.GT){
			return Filters.gt(field, longValue) ;
		} else if (operand == Op.GE) {
			return Filters.gte(field, longValue) ;
		} else if (operand == Op.LT) {
			return Filters.lt(field, longValue) ;
		} else if (operand == Op.LE){
			return Filters.lte(field, longValue) ;
		} else {
			throw new IllegalArgumentException("operand :" + operand) ;
		}
	}
}
