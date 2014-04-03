package net.ion.repository.mongo.util;

import java.util.Map;

import net.ion.framework.util.Debug;
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
			return "ReadNode:fqn[" + target.fqn() + "]" ;
		}
	};
	
	public static final Function<ReadNode, Map<PropertyId, PropertyValue>> READ_TOMAP = new Function<ReadNode, Map<PropertyId, PropertyValue>>(){
		@Override
		public Map<PropertyId, PropertyValue> apply(ReadNode target) {
			Map<PropertyId, PropertyValue> result = MapUtil.newMap() ;
			for(PropertyId pid : target.normalKeys()){
				result.put(pid, target.propertyId(pid)) ;
			}
			return result;
		}
	};

	public static final Function<ReadNode, Map<String, Object>> READ_TOFLATMAP = new Function<ReadNode, Map<String, Object>>(){
		@Override
		public Map<String, Object> apply(ReadNode target) {
			Map<String, Object> result = MapUtil.newMap() ;
			for(PropertyId pid : target.normalKeys()){
				result.put(pid.name(), target.propertyId(pid).asObject()) ;
			}
			return result;
		}
	} ;

	
	public static Function<ReadNode, Void> READ_DEBUGPRINT = new Function<ReadNode, Void>(){
		@Override
		public Void apply(ReadNode target) {
			Map<String, Object> result = MapUtil.newMap() ;
			for(PropertyId pid : target.keys()){
				PropertyValue pvalue = target.propertyId(pid);
				result.put(pid.fullString(), pvalue.size() <= 1 ? pvalue.asObject() : pvalue.asSet()) ;
			}
			Debug.debug(result);
			return null;
		}
	};

	
	
	
	
	
	
	public static final Function<WriteNode, String> WRITE_TOSTRING = new Function<WriteNode, String>(){
		@Override
		public String apply(WriteNode target) {
			return "WriteNode:fqn[" + target.fqn() + "]" ;
		}
	};
	
	public static final Function<WriteNode, Map<PropertyId, PropertyValue>> WRITE_TOMAP = new Function<WriteNode, Map<PropertyId, PropertyValue>>(){
		@Override
		public Map<PropertyId, PropertyValue> apply(WriteNode target) {
			Map<PropertyId, PropertyValue> result = MapUtil.newMap() ;
			for(PropertyId pid : target.normalKeys()){
				result.put(pid, target.propertyId(pid)) ;
			}
			return result;
		}
		
	};
	public static final Function<WriteNode, Map<String, Object>> WRITE_TOFLATMAP = new Function<WriteNode, Map<String, Object>>(){
		@Override
		public Map<String, Object> apply(WriteNode target) {
			Map<String, Object> result = MapUtil.newMap() ;
			for(PropertyId pid : target.normalKeys()){
				result.put(pid.name(), target.propertyId(pid).asObject()) ;
			}
			return result;
		}
	} ;

	
	public static Function<WriteNode, Void> WRITE_DEBUGPRINT = new Function<WriteNode, Void>(){
		@Override
		public Void apply(WriteNode target) {
			Map<String, Object> result = MapUtil.newMap() ;
			for(PropertyId pid : target.keys()){
				PropertyValue pvalue = target.propertyId(pid);
				result.put(pid.fullString(), pvalue.size() <= 1 ? pvalue.asObject() : pvalue.asSet()) ;
			}
			Debug.debug(result);
			return null;
		}
	};



}
