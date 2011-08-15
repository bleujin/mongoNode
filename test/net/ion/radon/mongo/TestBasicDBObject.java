package net.ion.radon.mongo;

import net.ion.framework.util.Debug;

import com.mongodb.BasicDBObject;

import junit.framework.TestCase;

public class TestBasicDBObject extends TestCase{

	
	public void testDBObject() throws Exception {
		BasicDBObject dbo = new BasicDBObject() ;
		dbo.put("name", "bleujin") ;
		dbo.put("city", "seoul") ;
		dbo.put("age", 20) ;
		
		
		BasicDBObject query = new BasicDBObject() ;
		query.put("name", "bleujin") ;
		
		
		
		Debug.debug(dbo.containsValue(query)) ;
		
	}
	
	
	public void testEqual() throws Exception {
		BasicDBObject actual = new BasicDBObject() ;
		actual.put("name", "bleujin") ;
		actual.put("city", "seoul") ;
		
		BasicDBObject expect = new BasicDBObject() ;
		expect.put("name", "bleujin") ;
		expect.put("city", "seoul") ;
		
		assertEquals(expect, actual) ;

		expect.put("age", 20) ;
		assertEquals(false, actual.equals(expect)) ;
	}
}
