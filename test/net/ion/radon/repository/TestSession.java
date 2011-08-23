package net.ion.radon.repository;

import java.net.UnknownHostException;

import junit.framework.TestCase;

import org.bson.types.ObjectId;

import com.mongodb.MongoException;

public class TestSession extends TestCase{
	
	
	public void testLoadRepository() throws Exception {

		RepositoryCentral rc1 = RepositoryCentral.create("61.250.201.78", 27017) ;
		RepositoryCentral rc2 = RepositoryCentral.create("61.250.201.78", 27017) ;
		
		assertTrue(rc1.getMongo() == rc2.getMongo()) ;
	}
	
	public void testSession() throws Exception {

		RepositoryCentral rc = RepositoryCentral.create("61.250.201.78", 27017) ;
		Session session1 =  rc.testLogin("test", "abcd") ;
		Session session2 =  rc.testLogin("test", "abcd") ;
		
		assertTrue(session1 == session2) ;
	}
	
	public void testSessionLogout() throws Exception {

		RepositoryCentral rc = RepositoryCentral.create("61.250.201.78", 27017) ;
		Session session1 =  rc.testLogin("test", "abcd") ;
		session1.logout() ;
		Session session2 =  rc.testLogin("test", "abcd") ;
		
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