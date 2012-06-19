package net.ion.radon.repository.function;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

public class LpadFunction extends SingleColumn {
	
	private List<IColumn> columns = ListUtil.newList();
	private String label;
	
	public LpadFunction(String[] args, String label) {
		for(String arg : args){
			columns.add(Column.parse(arg.trim()));
		}
		this.label = label;
	}
	
	public Object getValue(Node node) {
		if(columns.size() == 2){
			return StringUtil.leftPad(ObjectUtil.toString(columns.get(0).getValue(node)), NumberUtil.toIntWithMark(columns.get(1).getValue(node), 0));
		}else if(columns.size() == 3){
			return StringUtil.leftPad(ObjectUtil.toString(columns.get(0).getValue(node)), NumberUtil.toIntWithMark(columns.get(1).getValue(node), 0), 
									ObjectUtil.toString(columns.get(2).getValue(node)));
		}else{
			return "";
		}
	}

	public String getLabel() {
		return label;
	}

}
