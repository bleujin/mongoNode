package net.ion.radon.repository.util;

import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;


public class JSONMessage  {

	private Map<String, Object> store = MapUtil.newCaseInsensitiveMap() ;
	private String curr = "" ;
	
	public static JSONMessage create() {
		return new JSONMessage();
	}

	public JSONMessage put(String key, Object value) {
		store.put(curr + key, value);
		return this ;
	}

	public JSONMessage inner(String inner) {
		this.curr = StringUtil.isBlank(curr) ? inner + "." : this.curr + inner + ".";
		return this;
	}

	public String getString(String path) {
		return ObjectUtil.toString(store.get(path));
	}
	
	public Object get(String path){
		return store.get(path) ;
	}

	public String toString(){
		return store.toString() ;
	}
	
	public JSONObject toJSON(){
		JSONObject result = new JSONObject() ;
		for (Entry<String, Object> entry : store.entrySet()) {
			accumulate(result, entry.getKey(), entry.getValue()) ;	
		}
		return result ;
	}
	
	private void accumulate(JSONObject that, String path, Object value) {
		String[] names = StringUtil.split(path, "./") ;
		String firstPath = names[0];
		if (names.length == 1){
			that.accumulate(firstPath, value) ;
		} else {
			if (that.containsKey(firstPath)){
				String subPath = StringUtil.join(ArrayUtils.subarray(names, 1, names.length), '.') ;
				accumulate(that.getJSONObject(firstPath), subPath, value) ;
			} else {
				JSONObject newChild = new JSONObject();
				that.accumulate(firstPath, newChild) ;
				String subPath = StringUtil.join(ArrayUtils.subarray(names, 1, names.length), '.') ;
				accumulate(that.getJSONObject(firstPath), subPath, value) ;
			}
		}
	}

	public JSONMessage toRoot() {
		curr = "" ;
		return this;
	}

	
}
