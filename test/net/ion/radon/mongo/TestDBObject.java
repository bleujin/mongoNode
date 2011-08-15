package net.ion.radon.mongo;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;
import net.sf.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import junit.framework.TestCase;

public class TestDBObject extends TestCase {

	private DBCollection col ;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Mongo m = new Mongo("61.250.201.78");
		DB db = m.getDB("test");

		this.col = db.getCollection("mongonode");
		col.drop();
	}
	
	public void testDBObject() throws Exception {

		BasicDBObject wrapper = new BasicDBObject() ;
		List<BasicDBObject> list = ListUtil.newList() ;
		for (int i = 0; i < 5; i++) {
			list.add(makePerson(RandomUtil.nextRandomString(10), "seoul", 20)) ;
		}
		wrapper.append("people", list) ;

		col.save(wrapper);

		DBObject dbo = col.findOne();
		Debug.line(dbo);
	}
	
	public void testAppend() throws Exception {
		BasicDBObject wrapper = new BasicDBObject() ;
		for (int i = 0; i < 5; i++) {
			wrapper.append("people", makePerson(RandomUtil.nextRandomString(10), "seoul", 20)) ;
		}

		col.save(wrapper);

		DBObject dbo = col.findOne();
		Debug.line(dbo);
	}

	public void testArray() throws Exception {
		BasicDBObject wrapper = new BasicDBObject() ;
		BasicDBList list = new BasicDBList() ;
		for (int i = 0; i < 5; i++) {
			list.add(makePerson(RandomUtil.nextRandomString(10), "seoul", 20)) ;
		}
		wrapper.append("people", list) ;
		
		col.save(wrapper);

		DBObject dbo = col.findOne();
		Debug.line(dbo);
	}
	
	public void testJSON() throws Exception {
		BasicDBObject wrapper = new BasicDBObject() ;
		JSONObject json = JSONObject.fromObject("{userid:'bleujin',num:23, address:{city:'seoul'}}") ;
		wrapper.put("person", json) ;
		col.save(wrapper);

		DBObject dbo = col.findOne();
		Debug.line(dbo.get("person"), dbo.get("person.userId")) ; 
		Debug.line(dbo.get("address"), dbo);
	}

	
	
	
	
	
	
	
	private BasicDBObject makePerson(String name, String address, int age) {
		BasicDBObject dbo = new BasicDBObject();
		dbo.append("name", name);
		dbo.append("address", address);
		dbo.append("age", age);
		return dbo;
	}
}
