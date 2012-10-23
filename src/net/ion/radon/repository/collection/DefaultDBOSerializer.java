package net.ion.radon.repository.collection;

import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.radon.repository.AradonId;
import net.ion.radon.repository.NodeConstants;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DefaultDBOSerializer<E> implements DBOSerializer<E> {

	private String groupId ;
	private String field;
	private Class<? extends E> type;

	public DefaultDBOSerializer(String groupId, final String field, final Class<? extends E> value) {
		this.groupId = groupId ;
		this.field = field;
		this.type = value;
	}

	public DBObject toDBObject(final E element) {
		JsonElement jsonElement = JsonParser.fromObject(element);
		if (jsonElement.isJsonPrimitive()) {
			return new BasicDBObject(field, element);
		} else if (jsonElement.isJsonArray()) {
			Object[] array = jsonElement.getAsJsonArray().toObjectArray();
			BasicDBList list = new BasicDBList();
			for (Object obj : array) {
				list.add(obj);
			}
			return new BasicDBObject(field, list);
		} else {
			return new BasicDBObject(field, JsonObject.fromObject(element).toMap());
		}
	}

	public E toElement(final DBObject dbObject) {
		Object obj;

		if (dbObject.containsField(field) && (obj = dbObject.get(field)) != null && obj instanceof DBObject) {
			return JsonObject.fromObject(obj).getAsObject(type);
		}

		return null;
	}

	public DBObject groupQuery() {
		return new BasicDBObject(NodeConstants.ARADON_GROUP, groupId);
	}

	public String groupId(){
		return groupId ;
	}
	
}

class AradonIdSerializer<E> implements DBOSerializer<E> {

	private String groupId ;
	private String field;
	private Class<? extends E> type;

	AradonIdSerializer(String groupId, final String field, final Class<? extends E> value) {
		this.groupId = groupId ;
		this.field = field;
		this.type = value;
	}

	public DBObject toDBObject(final E element) {
		JsonElement jsonElement = JsonParser.fromObject(element);
		if (jsonElement.isJsonPrimitive()) {
			return new BasicDBObject(field, element);
		} else if (jsonElement.isJsonArray()) {
			Object[] array = jsonElement.getAsJsonArray().toObjectArray();
			BasicDBList list = new BasicDBList();
			for (Object obj : array) {
				list.add(obj);
			}
			return new BasicDBObject(field, list);
		} else {
			return new BasicDBObject(field, JsonObject.fromObject(element).toMap());
		}
	}

	public E toElement(final DBObject dbObject) {
		Object _aradonId = dbObject.get("__aradon");
		if (_aradonId == null || (! (_aradonId instanceof DBObject))) return null ;

		DBObject uId = (DBObject)AradonId.load((DBObject)_aradonId).getUid() ;
		return JsonObject.fromObject(uId.get(field)).getAsObject(type);
	}

	public DBObject groupQuery() {
		return new BasicDBObject(NodeConstants.ARADON_GROUP, groupId);
	}

	public String groupId(){
		return groupId ;
	}

}
