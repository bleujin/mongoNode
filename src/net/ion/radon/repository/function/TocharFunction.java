package net.ion.radon.repository.function;

import java.util.Date;

import net.ion.framework.util.DateUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.Node;

public class TocharFunction extends SingleColumn {

	private String colName ;
	private String format ;
	private String label;

	public TocharFunction(String[] cols, String label) {
		this.colName = cols[0] ;
		if(cols.length > 1)
			this.format = StringUtil.substringBetween(cols[1], "'") ;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public Object getValue(Node node) {
		Object value = node.get(colName) ;
		if (value instanceof Date){
			return DateUtil.dateToString((Date)value, format) ;
		}else{
			return String.valueOf(value);
		}
		//throw new IllegalArgumentException("not date format");
	}
	
}
