package net.ion.radon.repository.function;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

public class AppendFunction extends SingleColumn {

	private List<IColumn> columns = ListUtil.newList();
	private String label;
	
	public AppendFunction(String[] args, String label) {
		for(String arg : args){
			columns.add(Column.parse(arg.trim()));
		}
		this.label = label;
	}
	
	public Object getValue(Node node) {
		StringBuffer result = new StringBuffer();
		for(IColumn col : columns){
			result.append(col.getValue(node));
		}
		return result.toString();
	}

	public String getLabel() {
		return label;
	}

}
