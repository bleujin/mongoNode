package net.ion.radon.repository.function;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

public class DivideFunction extends SingleColumn{
	
	private List<IColumn> columns = ListUtil.newList();
	private String label;
	
	public DivideFunction(String[] args, String label) {
		for(String arg : args){
			columns.add(Column.parse(arg.trim()));
		}
		this.label = label;
	}

	public Object getValue(Node node) {
//		BigDecimal d = new BigDecimal(NumberUtil.toDouble(ObjectUtil.toString(columns.get(0).getValue(node)), 0D));
//		return d.divide(divisor);
		double dividen = getDoubleValue(node, columns.get(0).getValue(node));
		for(int i=1; i<columns.size(); i++){
			double divisor = getDoubleValue(node, columns.get(i).getValue(node));
			dividen = dividen / divisor;
		}
		return Double.valueOf(dividen).intValue();
	}
		
	private double getDoubleValue(Node node, Object value) {
		return NumberUtil.toDouble(ObjectUtil.toString(columns.get(0).getValue(node)), 0D);
	}

	public String getLabel() {
		return label;
	}
	

}
