package net.ion.radon.repository.function;

import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;


public class LengthFunction extends SingleColumn{
	
	private IColumn col ;
	private String label;
	
	public LengthFunction(String[] args, String label) {
		col = Column.parse(args[0]) ;
		this.label = label ;
	}
	
	public String getLabel() {
		return label;
	}

	public Object getValue(Node node) {
		String val = String.valueOf(col.getValue(node));
		return StringUtil.length(val);
	}

}
