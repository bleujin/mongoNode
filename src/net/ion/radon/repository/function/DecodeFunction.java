package net.ion.radon.repository.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

import org.apache.commons.lang.ArrayUtils;

public class DecodeFunction extends SingleColumn {

	private List<IColumn> columns = ListUtil.newList();
	private String label;

	public DecodeFunction(String[] cols, String label) {
		for (int i = 0; i < cols.length; i++) {
			columns.add(Column.parse(cols[i].trim()));
		}
		this.label = label;
	}


	public String getLabel() {
		return label;
	}

	public Object getValue(Node node) {
		return recursiveDecode(node, columns.toArray(new IColumn[0])) ;
	}
	
	private Object recursiveDecode(Node node, IColumn[] args){
		if (args.length < 3) throw new IllegalArgumentException("not permitted") ;
		if (args.length == 3) return ObjectUtil.equals(args[0].getValue(node), args[1].getValue(node)) ? args[2].getValue(node) : null ;
		if (args.length == 4) return ObjectUtil.equals(args[0].getValue(node), args[1].getValue(node)) ? args[2].getValue(node) : args[3].getValue(node);
		else {
			if (ObjectUtil.equals(args[0].getValue(node), args[1].getValue(node))) {
				return args[2].getValue(node)  ;
			} else {
				List<IColumn> newList = new ArrayList(Arrays.asList(ArrayUtils.subarray(args, 3, args.length)));
				newList.add(0, args[0]) ;
				return recursiveDecode(node, newList.toArray(new IColumn[0])) ;
			}
		}
	}
	
}
