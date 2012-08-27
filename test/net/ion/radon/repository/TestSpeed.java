package net.ion.radon.repository;


import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;

import com.mongodb.BasicDBList;
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
//		query = query.eq("useflg", "T");
//		query = query.lte("operday", pDate).gte("expireday", pDate);
		
		long start = System.nanoTime();
		
		NodeCursor nc =  query.find().limit(50);
		System.out.println("actual time(ms) : " + (int) ((System.nanoTime() - start) / 1000000));

//		PropertyFamily initial = PropertyFamily.create().put("count", 0);
//		String reduce = "function(doc, out){ out.count++; }";
//		NodeCursor nc2 = query.group(PropertyFamily.create(), initial, reduce) ;
//		nc2.debugPrint(PageBean.TEN) ;
//		
//		System.out.println("actual time(ms) : " + (int) ((System.nanoTime() - start) / 1000000));

//		Debug.line(query.count());
//		
//		System.out.println("actual time(ms) : " + (int) ((System.nanoTime() - start) / 1000000));
		
		Debug.line(nc.count());
		
		System.out.println("actual time(ms) : " + (int) ((System.nanoTime() - start) / 1000000));
		
//		while(nc.hasNext()){
//			Debug.line(nc.next().getAradonId()) ;
//		}
		
//		System.out.println(query);
//		System.out.println("actual time(ms) : " + (int) ((System.nanoTime() - start) / 1000000));
		
		//Debug.line(s.getAttribute(Explain.class.getCanonicalName(), Explain.class)) ;
//		Debug.line(nc.explain());
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

		DBCursor cu = collection.find(ref).limit(10);
		System.out.println("actual time(ms) : " + (int) ( (System.nanoTime() - start) / 1000000));
//		Debug.line(cu.count()) ;
//		System.out.println("actual time(ms) : " + (int) ( (System.nanoTime() - start) / 1000000));
//		while(cu.hasNext()){
//			Debug.line(cu.next()) ;
//		}
		
		Debug.line(collection.count(ref));
		
		System.out.println("actual time(ms) : " + (int) ( (System.nanoTime() - start) / 1000000));
		
//		PropertyFamily initial = PropertyFamily.create().put("count", 0);
//		String reduce = "function(doc, out){ out.count++; }";
//		
//		BasicDBList list = (BasicDBList) collection.group(PropertyFamily.create().getDBObject(), ref, initial.getDBObject(), reduce);
//		for (Object obj : list) {
//			Debug.line(obj) ;
//
//		}
//		System.out.println("actual time(ms) : " + (int) ( (System.nanoTime() - start) / 1000000));
//
//		//System.out.println(ref);
//		System.out.println("actual time(ms) : " + (int) ( (System.nanoTime() - start) / 1000000));
	}
	
	
}
