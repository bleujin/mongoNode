package net.ion.radon.repository.function;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

public class PowerFunction extends SingleColumn{

	private List<IColumn> columns = ListUtil.newList();
	private String label;
	
	public PowerFunction(String[] args, String label) {
		for(String arg : args){
			columns.add(Column.parse(arg.trim()));
		}
		this.label = label;
	}
	
	public Object getValue(Node node) {
		double result = Math.pow( NumberUtil.toDouble(ObjectUtil.toString(columns.get(0).getValue(node)), 0D), 
					NumberUtil.toDouble(ObjectUtil.toString(columns.get(1).getValue(node)), 0D));
		return Double.valueOf(result).intValue();
	}

	public String getLabel() {
		return label;
	}

}

//private IColumn col ;
//private String label;
//
//public LengthFunction(String[] args, String label) {
//	col = Column.parse(args[0]) ;
//	this.label = label ;
//}
//
//public String getLabel() {
//	return label;
//}
//
//public Object getValue(Node node) {
//	String val = String.valueOf(col.getValue(node));
//	return StringUtil.length(val);
//}