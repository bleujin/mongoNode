package net.ion.radon.repository.function;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

public class SubstrFunction extends SingleColumn{


	private IColumn col ;
	private String label ;
	private IColumn begin ;
	private IColumn end ;
	
	public SubstrFunction(String[] args, String label){
		col = Column.parse(args[0]) ;
		begin =  Column.parse(args[1].trim());
		end = args.length == 3 ?  Column.parse(args[2].trim()) :  Column.parse(String.valueOf(Integer.MAX_VALUE)) ;
		
		this.label = label ;
	}
	
	public String getLabel() {
		return label;
	}

	public Object getValue(Node node) {
		String result = StringUtil.toString(col.getValue(node));
		return result.substring( NumberUtil.toIntWithMark(begin.getValue(node), 0) ,  Math.min(NumberUtil.toIntWithMark(end.getValue(node),0), result.length())) ;
	}

}
