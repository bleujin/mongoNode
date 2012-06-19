package net.ion.radon.repository.function;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

public class NvlFunction extends SingleColumn {

	private List<IColumn> columns = ListUtil.newList();
	private String label;

	public NvlFunction(String[] cols, String label) {
		for (int i = 0; i < cols.length; i++) {
			columns.add(Column.parse(cols[i].trim()));
		}

		this.label = label;
	}


	public String getLabel() {
		return label;
	}

	public Object getValue(Node node) {
		for (IColumn col : columns) {
			Object result = col.getValue(node);
			if (result != null)
				return result;
		}
		return null;
	}

}
