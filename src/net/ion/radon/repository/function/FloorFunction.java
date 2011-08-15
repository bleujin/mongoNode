package net.ion.radon.repository.function;

import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

public class FloorFunction  extends SingleColumn{
	
	private IColumn col ;
	private String label;
	
	public FloorFunction(String[] args, String label) {
		col = Column.parse(args[0].trim());
		this.label = label;
	}
	
	public Object getValue(Node node) {
		double result = Math.floor(NumberUtil.toDouble(ObjectUtil.toString(col.getValue(node)))); 
		return Double.valueOf(result).intValue();
	}
	public String getLabel() {
		return label;
	}
}
