package net.ion.radon.repository;


import junit.framework.TestCase;
import net.ion.framework.util.Debug;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryOperators;


public class TestSpeed extends TestCase  {
	
	public void testSpeed() throws Exception {

		RepositoryCentral rc = new RepositoryCentral("61.250.201.117", 20001, "fluffy");
		String pDate = "20120827-102042";
		Session s = rc.login("ics_article");
		SessionQuery query = s.createQuery().eq("catidpath", "siya_root");
		query = query.eq("useflg", "T");
		query = query.lte("operday", pDate).gte("expireday", pDate);
		
		long start = System.nanoTime();

		//Debug.line(query.find().limit(50).explain()) ;
		
		NodeCursor nc =  query.find();
		
		
		long actual_time = (System.nanoTime() - start);
		System.out.println(query);
		System.out.println("actual time(ms) : " + (int) (actual_time / 1000000));
		
		Debug.line(nc.explain()) ;
		//Debug.line(nc.explain());
		//nc.debugPrint(PageBean.TEN);
	}
	
	public void testMong() throws Exception {
		String pDate = "20120827-102042";
		
		Mongo mongo = new Mongo("61.250.201.117", 20001);
		DB db = mongo.getDB("fluffy");
		DBCollection collection = db.getCollection("ics_article");
		DBObject ref = new BasicDBObject() ;
		ref.put("catidpath", "siya_root");
		ref.put("useflg", "T");
		ref.put("operday", new BasicDBObject(QueryOperators.LTE, pDate));
		ref.put("expireday", new BasicDBObject(QueryOperators.GTE, pDate));

		long start = System.nanoTime();

		DBCursor cu = collection.find(ref).limit(50);
		
		while(cu.hasNext()){
			Debug.line(cu.next()) ;
		}

		long actual_time = (System.nanoTime() - start);
		System.out.println(ref);
		System.out.println("actual time(ms) : " + (int) (actual_time / 1000000));
	}
	
	
}
