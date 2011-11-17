package net.ion.radon.mongo;

import net.ion.framework.util.ChainMap;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ChainDBObject {

	private DBObject dbo = new BasicDBObject() ;
	
	public ChainDBObject put(String key, Object value) {
		dbo.put(key, value) ;
		return this ;
	}

	public DBObject getDBObject() {
		return dbo ;
	}

	public ChainDBObjectList inner(String ikey) {
		Object obj = dbo.get(ikey) ;
		if (obj == null){
			dbo.put(ikey, new BasicDBList()) ;
			return inner(ikey) ;
		}
		BasicDBList dlist = (BasicDBList)obj ;
		
		return ChainDBObjectList.load(dlist, this)  ;
	}

}

class ChainDBObjectList {

	private BasicDBList dlist ;
	private ChainDBObject parent ;
	private ChainDBObjectList(BasicDBList dlist, ChainDBObject parent) {
		this.dlist = dlist ;
		this.parent = parent ;
	}

	public static ChainDBObjectList load(BasicDBList dlist, ChainDBObject parent) {
		return new ChainDBObjectList(dlist, parent);
	}

	public ChainDBObjectList push(ChainMap cmap){
		dlist.add(new BasicDBObject(cmap.toMap())) ;
		return this ;
	}

	public ChainDBObject getParent() {
		return parent;
	}
	
}
