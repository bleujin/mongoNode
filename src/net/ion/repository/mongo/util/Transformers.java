package net.ion.repository.mongo.util;

import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.repository.mongo.PropertyId;
import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.node.ReadNode;
import net.ion.repository.mongo.node.WriteNode;

import com.google.common.base.Function;

public class Transformers {

	public static final Function<ReadNode, String> READ_TOSTRING = new Function<ReadNode, String>(){
		@Override
		public String apply(ReadNode target) {
			return "fqn:" + target.fqn() ;
		}
	};
	
	public static final Function<ReadNode, Map<PropertyId, PropertyValue>> READ_TOMAP = new Function<ReadNode, Map<PropertyId, PropertyValue>>(){
		@Override
		public Map<PropertyId, PropertyValue> apply(ReadNode target) {
			Map<PropertyId, PropertyValue> result = MapUtil.newMap() ;
			for(PropertyId pid : target.keys()){
				result.put(pid, target.propertyId(pid)) ;
			}
			return result;
		}
		
	};

	
	public static final Function<WriteNode, String> WRITE_TOSTRING = new Function<WriteNode, String>(){
		@Override
		public String apply(WriteNode target) {
			return "fqn:" + target.fqn() ;
		}
	};
	
	public static final Function<WriteNode, Map<PropertyId, PropertyValue>> WRITE_TOMAP = new Function<WriteNode, Map<PropertyId, PropertyValue>>(){
		@Override
		public Map<PropertyId, PropertyValue> apply(WriteNode target) {
			Map<PropertyId, PropertyValue> result = MapUtil.newMap() ;
			for(PropertyId pid : target.keys()){
				result.put(pid, target.propertyId(pid)) ;
			}
			return result;
		}
		
	};

}
