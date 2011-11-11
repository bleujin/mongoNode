package net.ion.radon.repository.myapi;

import static net.ion.radon.repository.NodeConstants.ARADON_GROUP;
import static net.ion.radon.repository.NodeConstants.ARADON_UID;
import static net.ion.radon.repository.NodeConstants.ARADON_GHASH;

import java.util.Map;

import com.mongodb.DBObject;

import net.ion.framework.util.HashFunction;
import net.ion.radon.repository.IPropertyFamily;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.PropertyQuery;
public class AradonQuery implements IPropertyFamily{

	private static final long serialVersionUID = -2160983060926085268L;
	private PropertyQuery props ;
	private AradonQuery(PropertyQuery props) {
		this.props = props ;
	}

	public static AradonQuery newByGroup(String groupid) {
		return new AradonQuery(PropertyQuery.create(ARADON_GROUP, groupid));
	}
	public static AradonQuery newByGroupId(String groupId, Object uid) {
		return new AradonQuery(PropertyQuery.create(ARADON_GROUP, groupId).put(ARADON_UID, uid).put(ARADON_GHASH, HashFunction.hashGeneral(groupId)) );
	}

	public PropertyQuery getQuery() {
		return props;
	}

	public PropertyFamily getSort() {
		return PropertyFamily.create(ARADON_UID, -1);
	}

	public String getGroupId() {
		return (String)props.getDBObject().get(ARADON_GROUP);
	}

	public Object getUId() {
		return props.getDBObject().get(ARADON_UID);
	}

	public DBObject getDBObject() {
		return props.getDBObject();
	}

	public Map<String, ? extends Object> toMap() {
		return props.toMap() ;
	}
	
	

}
