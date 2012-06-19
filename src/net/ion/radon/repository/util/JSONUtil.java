package net.ion.radon.repository.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.parse.gson.internal.LazilyParsedNumber;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class JSONUtil {

	public static DBObject toDBObject(JsonObject json) {
		BasicDBObject result = new BasicDBObject() ;
		
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			result.put(entry.getKey(), toDBObject(entry.getValue())) ;
		}
		return result;
	}

	private static Object toDBObject(JsonElement jsonElement) {
		if (jsonElement.isJsonArray()) {
			JsonElement[] jeles = jsonElement.getAsJsonArray().toArray();
			BasicDBList list = new BasicDBList() ;
			for (JsonElement jele : jeles) {
				list.add(toDBObject(jele));
			}
			return list;
		} else if (jsonElement.isJsonObject()) {
			BasicDBObject newDBO = new BasicDBObject() ;
			for (Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
				newDBO.put(entry.getKey(), toDBObject(entry.getValue())) ;
			}
			return newDBO;
		} else if (jsonElement.isJsonPrimitive()) {
			if (jsonElement.getAsJsonPrimitive().getValue() instanceof LazilyParsedNumber) {
				long longValue = ((LazilyParsedNumber) jsonElement.getAsJsonPrimitive().getValue()).longValue();
				return longValue ;
			} else {
				return jsonElement.getAsJsonPrimitive().getValue();
			}
		} else if (jsonElement.isJsonNull()) {
			return null;
		} else {
			return null;
		}
	}

}
