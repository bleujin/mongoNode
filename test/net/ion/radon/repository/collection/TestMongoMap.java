package net.ion.radon.repository.collection;

import java.util.List;
import java.util.Map;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.Debug;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;
import junit.framework.TestCase;

public class TestMongoMap extends TestCase {

	private Session session;
	private MongoConcurrentMap<Long, Person> map ;
	protected void setUp() throws Exception {
		super.setUp();
		RepositoryCentral rc = RepositoryCentral.testCreate();
		this.session = rc.login("collection");
		
		this.map = session.newCollectionFactory("map").newConcurrentMap(Long.class, Person.class);
		map.clear() ;
		
		map.put(7789L, Person.create(7789, "bleujin", 20, Address.create("seoul"))) ;
	}

	public void testOverwrite() throws Exception {
		map.put(7789L, Person.create(7789, "bleujin", 20, Address.create("seoul"))) ;
		assertEquals(1, map.keySet().size()) ;
	}
	
	public void testContainsKey() throws Exception {
		assertEquals(true, map.containsKey(7789L)) ;
	}

	public void testKeySet() throws Exception {
		map.put(7790L, Person.create(7790, "hero", 20, Address.create("busan"))) ;
		assertEquals(2, map.keySet().toArray().length) ;
	}
	
	
	public void testKeySetIterator() throws Exception {
		assertEquals((Long)7789L, map.keySet().iterator().next()) ;
	}
	
	public void testRemove() throws Exception {
		assertEquals(true, map.containsKey(7789L)) ;
		
		map.remove(7789L) ;
		assertEquals(false, map.containsKey(7789L)) ;
	}
	
	public void testGroupedMap() throws Exception {
		MongoConcurrentMap<Long, Person> otherMap = session.newCollectionFactory("map1").newConcurrentMap(Long.class, Person.class);
		final Person person = Person.create(7789, "bleujin", 20, Address.create("seoul"));
		otherMap.put(7789L, person) ;
		
		assertEquals(1, map.size()) ;
		assertEquals(1, otherMap.size()) ;
		
		otherMap.clear() ;
		assertEquals(1, map.size()) ;
		
		assertEquals(true, map.containsValue(person)) ;
		assertEquals(false, otherMap.containsValue(person)) ;
	}
	
	
	public void xtestFindNode() throws Exception {
		RepositoryCentral rc = RepositoryCentral.testCreate();
		Session session = rc.login("collection");
		Node found = session.createQuery().findOne() ;
		Debug.line(found) ;
	}
	
	public void testConfirm() throws Exception {
		Node node = session.createQuery().aradonGroup("map").findOne() ;
		assertEquals(true, node != null) ;
		assertEquals(20, node.getAsInt("value.age")) ;
	}
	
}
