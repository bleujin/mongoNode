package net.ion.radon.repository;

import java.net.UnknownHostException;

import junit.framework.TestCase;

import net.ion.framework.util.Debug;

import org.bson.types.ObjectId;

import com.mongodb.MongoException;

public class TestSession extends TestCase{
	
	public void testParentChild2() throws Exception {
		RepositoryCentral rc = RepositoryCentral.testCreate() ;
		Session session =  rc.testLogin("abcd") ;
		Node work1 = session.newNode();
		work1.put("name", "bleu");
		
		assertTrue(session == work1.getSession());
	}

	public void testSameSessionInSameThread() throws Exception {

		RepositoryCentral rc = RepositoryCentral.testCreate() ;
		Session session1 =  rc.testLogin("abcd") ;
		Session session2 =  rc.testLogin("abcd") ;
		
		assertTrue(session1 == session2) ;
	}
	
	public void testSessionLogout() throws Exception {

		RepositoryCentral rc = RepositoryCentral.testCreate() ;
		Session session1 =  rc.testLogin("abcd") ;
		session1.logout() ;
		Session session2 =  rc.testLogin("abcd") ;
		
		assertTrue(session1 != session2) ;
	}
	
	
	
	public void xtestOtherSession() throws Exception {
		
		final int THREAD_COUNT = 10;
		ClientThread[] clients = new ClientThread[THREAD_COUNT] ;
		for (int i = 0; i < THREAD_COUNT; i++) {
			clients[i] = new ClientThread("abcd", 10) ;
		}
		
		for (int i = 0; i < THREAD_COUNT; i++) {
			clients[i].start() ;
		}
		
		for (int i = 0; i < THREAD_COUNT; i++) {
			clients[i].join() ;
		}
	}
	
	
	public void testThread() throws Exception {
		RepositoryCentral rc = RepositoryCentral.testCreate() ;
		final Session session =  rc.testLogin("abcd") ;
		session.dropWorkspace() ;

		final Node node = session.newNode().put("name", "bleujin") ;
		
		Thread t = new Thread(new Runnable(){
			public void run() {
				Session s = node.getSession() ;
				s.commit() ;
				assertEquals(true, session == s) ;
				assertEquals(true, session.createQuery().count() == 1) ;
			}
		}) ;
		t.start() ;
		t.join() ;

		Node found = session.createQuery().findOne() ;
		assertEquals(true, found != null) ;
	}
}	


class ClientThread extends Thread {
	
	private String wname ;
	private int count ;
	ClientThread(String wname, int count){
		super(new ObjectId().toString()) ;
		this.wname = wname ;
		this.count = count ;
	}
	
	public void run(){
		try {
			Session session = RepositoryCentral.create("61.250.201.157", 27017).testLogin(wname) ;
			for (int i = 0; i < count; i++) {
				Node node = session.newNode() ;
				node.put("name", Thread.currentThread().getName()) ;
			}
			session.commit() ;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
}