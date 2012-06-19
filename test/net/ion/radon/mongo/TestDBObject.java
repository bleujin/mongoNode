package net.ion.radon.mongo;

import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.RandomUtil;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

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
		JsonObject json = JsonParser.fromString("{userid:'bleujin',num:23, address:{city:'seoul'}}").getAsJsonObject() ;
		wrapper.put("person", json) ;
		col.save(wrapper);

		DBObject dbo = col.findOne();
		Debug.line(dbo.get("person"), dbo.get("person.userId")) ; 
		Debug.line(dbo.get("address"), dbo);
	}

	
	public void testSet() throws Exception {
		BasicDBObject dbo = new BasicDBObject() ;
		dbo.put("name", "bleujin") ;
		dbo.put("address", "seoul") ;
		
		col.save(dbo) ;
		
		Debug.debug(col.findOne()) ;
		
		BasicDBObject mod = new BasicDBObject() ;
		mod.put("$set", new BasicDBObject("name", "hero")) ;
		
		col.findAndModify(col.findOne(), mod) ;
		Debug.debug(col.findOne()) ;
	}
	
	
	public void testGroup() throws Exception {
		col.save(new ChainDBObject().put("grp", 1).put("name", "bleujin").put("rtime", 3).getDBObject() ) ;
		col.save(new ChainDBObject().put("grp", 1).put("name", "bleujin").put("rtime", 2).getDBObject() ) ;
		col.save(new ChainDBObject().put("grp", 1).put("name", "hero").put("rtime", 5).getDBObject() ) ;
		
		
		DBObject key = new BasicDBObject("name", true);
		DBObject cond = new BasicDBObject("grp", 1);
		DBObject initial = new BasicDBObject(MapUtil.chainMap().put("count", 0).put("total", 0).toMap()) ;
		String reduce = "function(doc, out){out.count++; out.total += doc.rtime; } ";
		String finalize = "function(out) {out.avg = out.total / out.count; }";
		
		DBObject gresult = col.group(key, cond, initial, reduce, finalize);
		Debug.line(gresult, gresult.getClass()) ;
	}

	
	public void testInGroup() throws Exception {
		
		ChainDBObject cdbo = new ChainDBObject().put("name", "bleujin").put("address", "seoul").inner("friend")
			.push(MapUtil.chainKeyMap().put("name", "novision").put("age", 20)) 
			.push(MapUtil.chainKeyMap().put("name", "iihi").put("age", 25))
			.push(MapUtil.chainKeyMap().put("name", "pm1200").put("age", 30)).getParent() ;
		col.save(cdbo.getDBObject()) ;

		
		String compareFn = "var comfn = function(f1, f2){ var order = 1 ;  if (f1[out.sortkey] > f2[out.sortkey]) return 1 * order ; else if(f1[out.sortkey] < f2[out.sortkey]) return -1 * order; else return 0 }" ;

		DBObject key = new BasicDBObject("name", true);
		DBObject cond = new BasicDBObject("name", "bleujin");
		DBObject initial = new BasicDBObject(MapUtil.chainMap().put("sortkey", "name").put("skip", 0).put("limit", 2).put("friends", new BasicDBList()).toMap()) ;
		String reduce = "function(doc, out){ " + compareFn + ";  out.friends = Array.prototype.slice.call(Array.prototype.sort.call(doc.friend, comfn), out.skip, out.limit); }";
		String finalize = "function(out) {;}";
		
		Debug.line(col.group(key, cond, initial, reduce, finalize)) ;
	}

	private BasicDBObject makePerson(String name, String address, int age) {
		BasicDBObject dbo = new BasicDBObject();
		dbo.append("name", name);
		dbo.append("address", address);
		dbo.append("age", age);
		return dbo;
	}
}






