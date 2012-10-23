package net.ion.radon.repository.collection;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;

public class TestMongoQueue extends TestCase {

	private Session session;
	private MongoQueue<Person> queue ;
	protected void setUp() throws Exception {
		super.setUp();
		RepositoryCentral rc = RepositoryCentral.testCreate();
		this.session = rc.login("collection");
		
		this.queue = session.newCollectionFactory("queue").newQueue(Person.class);
		queue.clear() ;
	}
	
	public void testAdd() throws Exception {
		queue.add(Person.create(7789, "bleujin", 20, Address.create("seoul"))) ;
		queue.add(Person.create(7789, "bleujin", 20, Address.create("seoul"))) ;
		boolean result = queue.add(Person.create(7789, "bleujin", 20, Address.create("seoul"))) ;

		assertEquals(true, result) ;
		assertEquals(3, queue.size()) ;
	}
	
	public void testPoll() throws Exception {
		queue.add(Person.create(7789, "bleujin", 20, Address.create("seoul"))) ;
		queue.add(Person.create(7790, "hero", 20, Address.create("busan"))) ;
		
		Person bleujin = queue.poll() ;
		assertEquals(true, bleujin != null) ;
		assertEquals(7789, bleujin.getEmpno()) ;
		assertEquals("bleujin", bleujin.getName()) ;
		assertEquals("seoul", bleujin.getAddress().getCity()) ;
		
		assertEquals(1, queue.size()) ;
		assertEquals(false, queue.isEmpty()) ;
		
		assertEquals(true, queue.poll() != null) ;
		assertEquals(0, queue.size()) ;
	}
	
	public void testPeek() throws Exception {
		queue.add(Person.create(7789, "bleujin", 20, Address.create("seoul"))) ;
		queue.add(Person.create(7790, "hero", 20, Address.create("busan"))) ;
		
		Person bleujin = queue.peek() ;
		assertEquals(true, bleujin != null) ;
		assertEquals(7789, bleujin.getEmpno()) ;
		assertEquals("bleujin", bleujin.getName()) ;
		assertEquals("seoul", bleujin.getAddress().getCity()) ;
		
		assertEquals(2, queue.size()) ;
	}
	
	
	public void testConfuse() throws Exception {
		MongoConcurrentMap<String, Person> map = session.newCollectionFactory("map").newConcurrentMap(String.class, Person.class);
		map.clear() ;
		map.put("bleujin", Person.bleujin) ;
		
		queue.add(Person.create(7789, "hero", 20, Address.create("seoul"))) ;
		
		
		assertEquals(1, map.size()) ;
		assertEquals(1, queue.size()) ;

		Person hero = queue.poll() ;
		assertEquals(true, hero != null) ;
		assertEquals(7789, hero.getEmpno()) ;
		assertEquals("hero", hero.getName()) ;
		assertEquals("seoul", hero.getAddress().getCity()) ;
		
		assertEquals(0, queue.size()) ;
		assertEquals(1, map.size()) ;
		
		queue.clear() ;
		assertEquals(1, map.size()) ;
	}
	
}
