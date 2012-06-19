package net.ion.radon.repository;

import junit.framework.TestCase;
import net.ion.radon.core.PageBean;

public class TestDB extends TestCase{

	public void testChangeDB() throws Exception {
		RepositoryCentral rc1 = RepositoryCentral.testCreate("db1");

        Session session1 = rc1.testLogin("ion1");
        session1.dropWorkspace();
        
        session1.newNode().put("name", "nick");
        session1.commit() ;
        session1.logout() ;
        
        RepositoryCentral rc2 = RepositoryCentral.testCreate("db2");
        Session session2 = rc2.testLogin("ion2");
        session2.dropWorkspace();
        
        session2.newNode().put("name", "joon");
        session2.commit();
	}
	
	public void testViewDB1() throws Exception {
		RepositoryCentral rc = RepositoryCentral.testCreate("db1");
		Session session = rc.testLogin("ion1");
		
		session.createQuery().find().debugPrint(PageBean.ALL) ;
	}

	public void testViewDB2() throws Exception {
		RepositoryCentral rc = RepositoryCentral.testCreate("db2");
		Session session = rc.testLogin("ion2");
		
		session.createQuery().find().debugPrint(PageBean.ALL) ;
	}

}
