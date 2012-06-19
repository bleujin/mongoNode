package net.ion.radon.repository.function;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

public class ModFunction extends SingleColumn{

	private List<IColumn> columns = ListUtil.newList();
	private String label;

	public ModFunction(String[] cols, String label) {
		for (String col : cols ) {
			columns.add(Column.parse(col.trim()));
		}
		this.label = label;
	}
	
	public Object getValue(Node node) {
		return NumberUtil.toIntWithMark(columns.get(0).getValue(node), 0) 
		       % NumberUtil.toIntWithMark(columns.get(1).getValue(node), 0);
	}

	public String getLabel() {
		return label;
	}

}
