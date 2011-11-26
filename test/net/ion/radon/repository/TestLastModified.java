package net.ion.radon.repository;

import java.util.GregorianCalendar;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;

public class TestLastModified extends TestBaseRepository{

	
	public void testWhenNew() throws Exception {
		assertEquals(0, session.getRoot().getLastModified()) ;
		

		long ctime = GregorianCalendar.getInstance().getTimeInMillis() ;
		
		Node bleujin = session.newNode("bleujin") ;
		long created = bleujin.getLastModified() ;  
		assertEquals(0L, created) ; // before
		
		Thread.sleep(100) ;
		session.commit() ;
		
		Node found = session.createQuery().path("/bleujin").findOne();
		assertEquals(true, found.getLastModified() > 50 + ctime) ;
	}
	
	
	public void testQueryUpdate() throws Exception {
		session.newNode("bleujin").put("name", "bleu") ;
		session.commit() ;
		
		long ctime = GregorianCalendar.getInstance().getTimeInMillis() ;
		Thread.sleep(200) ;
		
		assertEquals(1, session.createQuery().eq("name", "bleu").find().count()) ;
		
		NodeResult nr = session.createQuery().eq("name", "bleu").update(MapUtil.create("age", 20)) ;
		assertEquals(1, nr.getRowCount()) ;
		session.clear() ;
		
		
		Node found = session.createQuery().path("/bleujin").findOne();
		Debug.debug(ctime, found.getLastModified()) ;
		assertEquals(true, found.getLastModified() > ctime) ;
	}
	
	
	
	
}
