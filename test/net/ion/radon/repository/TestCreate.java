package net.ion.radon.repository;

import java.util.Date;

import net.ion.framework.util.Debug;
import junit.framework.TestCase;

public class TestCreate extends TestCase{

	private Session session ;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		RepositoryCentral rc = RepositoryCentral.testCreate() ;
		session = rc.testLogin("mywork") ;
		session.dropWorkspace() ;
	}
	
	public void testNewNode() throws Exception {
		Node newNode = session.newNode().put("name", "bleujin").put("age", 20) ;
		assertEquals(0, newNode.getLastModified()) ; 
		
		
		session.commit() ;
		assertEquals(1, session.createQuery().eq("name", "bleujin").lte("age", 20).find().count()) ;
		
		assertEquals(true, newNode.getLastModified() > 0) ;
	}

	public void testOtherType() throws Exception {
		Node node = session.newNode() ;
		node.put("string", "string") ;
		node.put("sint", "99") ;
		node.put("int", 1) ;
		node.put("long", 1L) ;
		node.put("boolean", true) ;
		node.put("date", new Date()) ;
		session.commit() ;
		
		
		
		Node found = session.createQuery().findOne() ;
		assertEquals("string", found.get("string")) ;
		assertEquals("99", found.get("sint")) ;
		assertEquals(99, found.getAsInt("sint")) ;
		assertEquals(1, found.get("int")) ;
		assertEquals(1L, found.get("long")) ;
		assertEquals(true, found.get("boolean")) ;
		assertEquals(new Date().getDay(), ((Date)found.get("date")).getDay()) ;
	}
	
	public void testCaseSentive() {
		Node newNode = session.newNode().put("nAme", "bleujin") ;

		assertEquals("bleujin", newNode.getString("Name")) ;
		assertEquals("bleujin", newNode.getString("NAME")) ;
		assertEquals("bleujin", newNode.getString("name")) ;
		assertEquals("bleujin", newNode.getString("namE")) ;

		assertEquals("bleujin", newNode.get("Name")) ;
		assertEquals("bleujin", newNode.get("NAME")) ;
		assertEquals("bleujin", newNode.get("name")) ;
		assertEquals("bleujin", newNode.get("namE")) ;

	}
	

	public void testCaseInSentiveQuery() throws Exception {
		Node newNode = session.newNode().put("nAme", "bleujin") ;
		
		session.commit() ;
		
		Node node = session.createQuery().eq("NAME", "bleujin").findOne() ;
		assertEquals("bleujin", node.getString("Name")) ;
	}
	
	
	
	public void testCreateWithPath() throws Exception {
		session.newNode("it").put("name", "bleujin") ;
		session.commit() ;
		
		assertEquals(1, session.createQuery().eq("name", "bleujin").find().count()) ;
		assertEquals("bleujin", session.createQuery().findByPath("/it").getString("name")) ;
	}
	
	
	public void testChildNode() throws Exception {
		RepositoryCentral rc = RepositoryCentral.testCreate() ;
		Session session = rc.testLogin("mywork") ;
		session.dropWorkspace() ;
		
		Node parent = session.newNode("bleu").put("age", 20);
		parent.createChild("jin").put("name", "jin") ;
		session.commit();
		
		assertEquals(2, session.createQuery().find().count()) ;
		
		Node foundNode = session.createQuery().id(parent.getIdentifier()).findOne();
		assertEquals(20, foundNode.get("age"));
		
		assertEquals("jin", session.createQuery().findByPath("/bleu/jin").getString("name")) ;
	}
	

	public void testDupName() throws Exception {
		Node newNode = session.newNode("name") ;
		assertEquals("/name", newNode.getPath()) ;
		
		Node child = newNode.createChild("child") ;
		assertEquals("/name/child", child.getPath()) ;
		
		try {
			newNode.createChild("/child") ;
			fail() ;
		} catch(IllegalArgumentException ignore) {
		} catch(Exception e){
			fail() ;
		}
		
		session.commit() ;
	}
	
	public void testPath() throws Exception {
		Node hello = session.newNode("hello").put("name", "bleujin") ;
		
		assertEquals("/hello", hello.getPath()) ;
		session.commit() ;
		
		assertEquals("/hello", session.createQuery().findOne().getPath()) ;
	}
	
	public void testReserved() throws Exception {
		Node newNode = session.newNode().put("name", "bleujin") ;
		
		assertEquals("/" + newNode.getIdentifier(), newNode.getPath()) ;
		assertEquals("__empty", newNode.getAradonId().getGroup()) ;
		assertEquals(newNode.getIdentifier(), newNode.getAradonId().getUid()) ;
	}


}
