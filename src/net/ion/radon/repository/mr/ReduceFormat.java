package net.ion.radon.repository.mr;

import net.ion.radon.repository.IPropertyFamily;
import net.ion.radon.repository.NodeConstants;
import net.ion.radon.repository.PropertyFamily;

public class ReduceFormat {

	private static PropertyFamily NODEKEY = PropertyFamily.create().put(NodeConstants.ID, true);
	
	private IPropertyFamily key ;
	private IPropertyFamily initial ;
	private String reduce ;
	private String finalize ;
	
	private ReduceFormat(IPropertyFamily key, IPropertyFamily initial, String reduce) {
		this.key = key ;
		this.initial = initial ;
		this.reduce = reduce ;
	}

	public String getReduce() {
		return reduce;
	}

	public String getFinalize() {
		return finalize;
	}

	public String getMap() {
		return "";
	}


}
