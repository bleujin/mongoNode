package net.ion.radon.repository;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;

public class TestQueue extends TestCase{

	public void testOffer() throws Exception {
		RepositoryCentral rc = RepositoryCentral.testCreate() ;
		WorkspaceQueue queue = rc.lookupQueue("queue");
		
		queue.offer(queue.createElement().put("grp", "user").put("name", "bleujin").put("age", 10)) ;
		queue.offer(queue.createElement().put("grp", "user").put("name", "hero").put("num", 30)) ;
		queue.offer(queue.createElement().put("grp", "user").put("name", "jin")) ;
		
		Node find = null ;
		do{
			find = queue.poll(PropertyQuery.create().eq("grp", "user")) ;
			Debug.line(find) ;
		} while(find != null) ;
		
	}
	
	
	
	
}
