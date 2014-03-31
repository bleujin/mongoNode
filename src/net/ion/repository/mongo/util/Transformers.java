package net.ion.repository.mongo.util;

import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import net.ion.framework.util.MapUtil;
import net.ion.repository.mongo.PropertyId;
import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.node.NodeCommon;
import net.ion.repository.mongo.node.ReadNode;

import com.google.common.base.Function;

public class Transformers {

	public static final Function<ReadNode, String> TOSTRING = new Function<ReadNode, String>(){
		@Override
		public String apply(ReadNode target) {
			return "fqn:" + target.fqn() ;
		}
	};
	
	public static final Function<ReadNode, Map<PropertyId, PropertyValue>> TOMAP = new Function<ReadNode, Map<PropertyId, PropertyValue>>(){
		@Override
		public Map<PropertyId, PropertyValue> apply(ReadNode target) {
			Map<PropertyId, PropertyValue> result = MapUtil.newMap() ;
			for(PropertyId pid : target.keys()){
				result.put(pid, target.propertyId(pid)) ;
			}
			return result;
		}
		
	};

}
