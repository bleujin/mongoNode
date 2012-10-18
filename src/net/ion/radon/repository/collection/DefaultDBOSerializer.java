package net.ion.radon.repository.collection;

import java.util.Arrays;
import java.util.Collections;

import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.ArrayUtil;
import net.ion.framework.util.ListUtil;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DefaultDBOSerializer<E> implements DBOSerializer<E> {

	private String field;
	private Class<? extends E> type;

	public DefaultDBOSerializer(final String field, final Class<? extends E> value) {
		this.field = field;
		this.type = value;
	}

	public DBObject toDBObject(final E element, final boolean equalFunctions, final boolean negate) {
		if (equalFunctions && negate) {
			return new BasicDBObject(field, new BasicDBObject("$ne", JsonObject.fromObject(element).toMap()));
		}
		JsonElement jsonElement = JsonParser.fromObject(element) ;
		if (jsonElement.isJsonPrimitive()) return new BasicDBObject(field, element) ;
		if (jsonElement.isJsonArray()) {
			Object[] array = jsonElement.getAsJsonArray().toObjectArray() ;
			BasicDBList list = new BasicDBList() ;
			for (Object obj : array) {
				list.add(obj) ;
			}
			return new BasicDBObject(field, list) ;
		}
		
		return new BasicDBObject(field, JsonObject.fromObject(element).toMap());
	}

	public E toElement(final DBObject dbObject) {
		Object obj;

		if (dbObject.containsField(field) && (obj = dbObject.get(field)) != null && obj instanceof DBObject) {
			return JsonObject.fromObject(obj).getAsObject(type);
		}

		return null;
	}

}
