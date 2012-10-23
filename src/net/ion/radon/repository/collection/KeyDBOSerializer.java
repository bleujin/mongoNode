package net.ion.radon.repository.collection;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.radon.repository.AradonId;
import net.ion.radon.repository.NodeConstants;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class KeyDBOSerializer<E> implements DBOSerializer<E> {

	private String groupId;
	private Class<? extends E> type;

	KeyDBOSerializer(final String groupId, final Class<? extends E> value) {
		this.groupId = groupId;
		this.type = value;
	}

	public DBObject toDBObject(final E element){
		return new BasicDBObject(NodeConstants.ARADON, AradonId.create(groupId, element).toNodeObject().getDBObject()) ;
	}

	public E toElement(final DBObject dbObject) {
		
		Object _aradonId = dbObject.get(NodeConstants.ARADON) ;
		if (_aradonId == null || (! (_aradonId instanceof DBObject))) return null ;
		
		AradonId aradonId = AradonId.load((DBObject)_aradonId) ;
		Object uId = aradonId.getUid();
		if (uId != null && uId instanceof DBObject) {
			return JsonObject.fromObject(uId).getAsObject(type);
		}

		return (E)uId;
	}

	public DBObject groupQuery() {
		return new BasicDBObject(NodeConstants.ARADON_GROUP, groupId);
	}
	
	public String groupId(){
		return groupId ;
	}

}
