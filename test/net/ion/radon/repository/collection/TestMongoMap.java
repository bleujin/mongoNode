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

	private CollectionFactory cf;
	protected void setUp() throws Exception {
		super.setUp();
		RepositoryCentral rc = RepositoryCentral.testCreate();
		this.cf = rc.colFactory("map") ;
	}

	public void testPut() throws Exception {
		
		
		MongoConcurrentMap<Long, Person> map = cf.newConcurrentMap(Long.class, Person.class);
		map.clear() ;
		
		map.put(7789L, Person.create(7789, "bleujin", 20, Address.create("seoul"))) ;
		map.put(7789L, Person.create(7789, "hero", 30, Address.create("seoul"))) ;
		
		
	}
	
	public void testFindNode() throws Exception {
		RepositoryCentral rc = RepositoryCentral.testCreate();
		Session session = rc.login("map");
		Node found = session.createQuery().findOne() ;
		Debug.line(found) ;
	}
	
	public void testConfirm() throws Exception {
		DBCursor cursor = cf.getCollection().find() ;
		List<DBObject> list = cursor.toArray();
		assertEquals(1, list.size()) ;
		
		Debug.line(list.get(0)) ;
	}
	
}
