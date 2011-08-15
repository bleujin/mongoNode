package net.ion.radon.repository.myapi;

import static net.ion.radon.repository.NodeConstants.ARADON_GROUP;
import static net.ion.radon.repository.NodeConstants.ARADON_UID;
import net.ion.radon.repository.IPropertyFamily;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.PropertyQuery;
public class AradonQuery {

	IPropertyFamily props ;
	private AradonQuery(PropertyQuery props) {
		this.props = props ;
	}

	public static AradonQuery newByGroup(String groupid) {
		return new AradonQuery(PropertyQuery.create(ARADON_GROUP, groupid));
	}
	public static AradonQuery newByGroupId(String groupid, Object uid) {
		return new AradonQuery(PropertyQuery.create(ARADON_GROUP, groupid).put(ARADON_UID, uid));
	}

	public IPropertyFamily getQuery() {
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
	
	

}
