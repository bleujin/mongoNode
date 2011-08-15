package net.ion.radon.repository.function;

import net.ion.framework.util.NumberUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

public class SignFunction extends SingleColumn{
	
	private IColumn col ;
	private String label;
	
	public SignFunction(String[] args, String label) {
		col = Column.parse(args[0]) ;
		this.label = label ;
	}
	
	public String getLabel() {
		return label;
	}

	public Object getValue(Node node) {
		String val = String.valueOf(col.getValue(node));
		Long result = NumberUtil.toLong( val);
		return result == 0L ? 0 : (result > 0 ? 1 : -1) ; 
	}

}
