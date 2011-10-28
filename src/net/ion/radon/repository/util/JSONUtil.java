package net.ion.radon.repository.util;

import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class JSONUtil {

	public static DBObject toDBObject(JSONObject json) {
		BasicDBObject result = new BasicDBObject() ;
		Set<String> keys = json.keySet() ;
		for (String key : keys) {
			Object value = json.get(key) ;
			if (value instanceof JSONObject){
				result.put(key, toDBObject((JSONObject)value)) ;
			} else if (value instanceof JSONArray){
				BasicDBList list = new BasicDBList() ;
				JSONArray vals = (JSONArray)value ;
				for (Object obj : vals) {
					if(obj instanceof JSONObject){
						list.add(toDBObject((JSONObject)obj)) ;
					} else {
						list.add(obj) ;
					}
				}
				result.put(key, list) ;
			} else { // value object
				result.put(key, value) ;
			}
		}
		return result;
	}

}
