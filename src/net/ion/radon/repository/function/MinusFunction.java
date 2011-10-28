package net.ion.radon.repository.function;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

public class MinusFunction extends SingleColumn{

	private List<IColumn> columns = ListUtil.newList();
	private String label;
	
	public MinusFunction(String[] args, String label) {
		for(String arg:args){
			columns.add(Column.parse(arg.trim()));
		}
		this.label = label;
	}
	
	public Object getValue(Node node) {
		int result =  NumberUtil.toIntWithMark(columns.get(0).getValue(node), 0);
		for(int i=1; i<columns.size(); i++){
			result = result -NumberUtil.toIntWithMark(columns.get(i).getValue(node), 0);
		}
		return result;
	}

	public String getLabel() {
		return label;
	}

}
