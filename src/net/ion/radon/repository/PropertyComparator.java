package net.ion.radon.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import net.ion.framework.util.ListUtil;

public class PropertyComparator<T extends INode> implements Comparator<T>{

	private List<OrderColumn> orders = ListUtil.newList() ;  
	
	private PropertyComparator() {
	}

	public static <T extends INode> PropertyComparator<T> create(PropertyFamily pf){
		PropertyComparator<T> pc =  new PropertyComparator<T>() ;
		Integer one = new Integer(1) ;
		for (Entry<String, ? extends Object> entry : pf.toMap().entrySet()) {
			append(pc, entry.getKey(), one.equals(entry.getValue())) ;
		}  
		return pc ;
	}
	
	private static <T extends INode> PropertyComparator<T> append(PropertyComparator<T> pc, String propId, boolean asc){
		return (asc == true) ? pc.ascending(propId) : pc.descending(propId) ;
	}
	
	public PropertyComparator<T> ascending(String propId){
		orders.add(new OrderColumn(propId, 1)) ;
		return this;
	}
	
	public PropertyComparator<T> descending(String propId){
		orders.add(new OrderColumn(propId, -1)) ;
		return this;
	}

	
	public static PropertyComparator<? super INode> newAscending(String propId) {
		return new PropertyComparator<INode>().ascending(propId);
	}
	
	public static PropertyComparator<? super INode> newDescending(String propId) {
		return new PropertyComparator<INode>().descending(propId);
	}

	public int compare(T n1, T n2) {
		for(OrderColumn oc : orders){
			Object p1 = n1.get(oc.getPropId()) ;
			Object p2 = n2.get(oc.getPropId()) ;
			
			int result = 0;
			if (p1 != null && p2 != null && p1 instanceof Comparable && p2 instanceof Comparable){
				result = ((Comparable)p1).compareTo(p2);
			} else if (p1 == null) {
				result = 1 ;
			} else if (p2 == null){
				result = -1;
			}
			if (result == 0) continue ;
			return result * oc.getOrder();
			
		}
		
		return 0;
	}


}

class OrderColumn {
	
	private String propId ;
	private int order ;
	OrderColumn(String col, int order){
		this.propId = col ;
		this.order = order ;
	}
	
	String getPropId(){
		return propId ;
	}
	
	int getOrder(){
		return order ;
	}
	
}

