package net.ion.radon.mongo;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.Mongo;

public class TestDBRef extends TestCase{
	
	private Mongo m ; 
	private DBCollection col ;
	
	@Override protected void setUp() throws Exception {
		m = new Mongo("61.250.201.78");
		DB db = m.getDB("test");

		this.col = db.getCollection("mongonode");
		col.drop();
	}
	
	@Override protected void tearDown() throws Exception {
		m.close() ;
	}

	public void testDBObject() throws Exception {
		BasicDBObject bleujin = new BasicDBObject() ;
		bleujin.put("name", "bleujin") ;
		bleujin.put("city", "seoul") ;
		bleujin.put("age", 20) ;
		
		col.save(bleujin) ;

		DBObject foundbleujin = col.findOne() ;

		BasicDBObject hero = new BasicDBObject() ;
		hero.put("name", "hero") ;
		hero.put("friend", new DBRef(m.getDB("test"), "mongonode", foundbleujin.get("_id"))) ;

		col.save(hero) ;
		
		Debug.line(foundbleujin) ;
		
		
		DBObject fhero = col.findOne(new BasicDBObject("name", "hero")) ;
		Debug.line(  ((DBRef)fhero.get("friend")).fetch()  ) ;
		
		
	}
	
	
	public void testIdentifier() throws Exception {
		BasicDBObject bleujin = new BasicDBObject() ;
		bleujin.put("_id", "bleujin") ;
		bleujin.put("_id", "bleujin") ;
		
		col.save(bleujin) ;
		
		DBObject found = col.findOne(new BasicDBObject("_id", "bleujin")) ;
		Debug.line(found, new ObjectId()) ;
	}
	

	public void testTime() throws Exception {
		int val = _curtime();
		Debug.line(val, String.valueOf(val).length()) ;
	}
	
	public int _flip(int x) {
		int z = 0;
		z |= x << 24 & -16777216;
		z |= x << 8 & 16711680;
		z |= x >> 8 & 65280;
		z |= x >> 24 & 255;
		return z;
	}

	private int _curtime() {
		return _flip((int) (System.currentTimeMillis() / 1000L));
	}
	
}
