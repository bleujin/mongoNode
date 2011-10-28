package net.ion.radon.repository.function;

import net.ion.framework.util.NumberUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

public class TonumberFunction extends SingleColumn {

	private IColumn col ;
	private String label;
	
	public TonumberFunction(String[] cols, String label) {
		this.col = Column.parse(cols[0]) ;
		this.label = label;
	}

	public Object getValue(Node node) {
		return NumberUtil.toIntWithMark(col.getValue(node), 0);
	}

	public String getLabel() {
		return label;
	}

}
