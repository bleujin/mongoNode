package net.ion.radon.repository.ics;

import java.util.List;
import java.util.Stack;
import java.util.Map.Entry;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.express.ExpressUtils;
import net.ion.framework.util.express.PostfixExpress;
import net.ion.radon.repository.PropertyQuery;

public class ActionQuery {

	private PostfixExpress pfe  ; 
	private ActionQuery(PostfixExpress pfe){
		this.pfe = pfe ;
	}
	
	public static ActionQuery create(String str) {
		return new ActionQuery(ExpressUtils.toPostfixSearchExpress(str));
	}

	public void merge(PropertyQuery inner) {
		
		// [aaa>2, ff=3, &&, !, sfff=ff, sfff=2, ||, &&, vvv==xx, &&, xx<33, vvv<=43, ||, &&, catid=vvvv, &&] 
		Stack<PropertyQuery> stack = new Stack<PropertyQuery>() ;
		for (String expr : pfe.getExpressions()) {
			
			if (! isRelationOperator(expr)){
				
				
				stack.push(PropertyQuery.create().where(expr)) ;
			} else {
				if (isAND(expr)) {
					stack.add(PropertyQuery.create().and(stack.pop(), stack.pop())) ;
				} else if (isOR(expr)) {
					stack.add(PropertyQuery.create().or(stack.pop(), stack.pop())) ;
				} else if (isNOT(expr)) {
					stack.add(PropertyQuery.create().not(stack.pop())) ;
				}
			}
		}
		
		for (Entry<String, ? extends Object> entry : stack.pop().toMap().entrySet()) {
			inner.put(entry.getKey(), entry.getValue()) ;
		} 
	}
	
	
	private List<String> RelationOperator = ListUtil.toList("&&", "||", "!") ;
	private boolean isRelationOperator(String exp){
		return RelationOperator.contains(exp) ;
	}
	
	private boolean isAND(String expr){
		return "&&".equals(expr) ;
	}
	
	private boolean isOR(String expr){
		return "||".equals(expr) ;
	}
	
	private boolean isNOT(String expr){
		return "!".equals(expr) ;
	}
	
	

}
