package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.GHASH;
import static net.ion.radon.repository.NodeConstants.GROUP;
import static net.ion.radon.repository.NodeConstants.UID;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
public class AradonId extends BasicDBObject{

	static final AradonId EMPTY = new AradonId("n/a", -1, -1);

	private AradonId(String group, Object uid, Object ghash) {
		this.put(GROUP, group.toLowerCase()) ;
		this.put(UID, uid) ;
		this.put(GHASH, ghash) ;
	}

	public String getGroup() {
		return super.getString(GROUP) ;
	}
	
	public Object getUid(){
		return super.get(UID) ;
	}

	static AradonId create(InNode dbo) {
		
		if (dbo == null) return EMPTY ;
		
		BasicDBList dlist = (BasicDBList) dbo.get(GROUP) ;
		String group = (String) dlist.get(dlist.size()-1) ;
		Object uid = dbo.get(UID) ;
		Object ghash = dbo.get(GHASH) ;
		
		return new AradonId(group, uid, ghash);
	}

	public static AradonId load(DBObject dbo) {
		return new AradonId((String)dbo.get(GROUP), dbo.get(UID), dbo.get(GHASH));
	}

}
