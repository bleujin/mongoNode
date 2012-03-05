package net.ion.radon.repository.util;

import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.util.ArrayUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;


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
	
	public JsonObject toJSON(){
		return JsonUtil.arrangeKey(JsonParser.fromMap(store).getAsJsonObject()) ;
	}

	public JSONMessage toRoot() {
		curr = "" ;
		return this;
	}

	
}
