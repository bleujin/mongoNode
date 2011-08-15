package net.ion.radon.repository;

import java.util.List;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.AradonQuery;

public class TestRows extends TestBaseRepository{

	public void testStart() throws Exception {
		initData() ;
		
		Node dev = session.createQuery().eq("dname", "dev").findOne() ;
		assertEquals(true, dev != null) ;
		
		ReferenceTaragetCursor cursor = dev.getReferencedNodes("emps");
		cursor.debugPrint(PageBean.ALL) ;
	}
	
//	public void testInitDB() throws Exception {
//		session.dropDB() ;
//	}
	
	private void initData() {
		session.dropDB() ;
		session.changeWorkspace("esample") ;
		
		Node dept = session.newNode("dept") ;
		
		dept.createChild("dev").put("dname", "dev").put("loc", "seoul").setAradonId("dept", 10) ;
		dept.createChild("exp").put("dname", "exp").put("loc", "busan").setAradonId("dept", 20) ;
		dept.createChild("sol").put("dname", "sol").put("loc", "inchon").setAradonId("dept", 30) ;
		session.commit() ;
		
		Node emps = session.newNode("emps") ;
		emps.createChild("bleu").put("ename", "bleu").put("age", 20).setAradonId("emps", "bleu").addReference("current_dept", AradonQuery.newByGroupId("dept", 10)) ;
		emps.createChild("hero").put("ename", "hero").put("age", 21).setAradonId("emps", "hero").addReference("current_dept", AradonQuery.newByGroupId("dept", 10)) ;
		emps.createChild("jin").put("ename", "jin").put("age", 22).setAradonId("emps", "jin").addReference("current_dept", AradonQuery.newByGroupId("dept", 20)) ;
		
		
		// not permitted
		try {
			Node another  = emps.createChild("jin").put("ename", "jin").put("age", 22);
			another.addReference("current_dept", AradonQuery.newByGroupId("dept", 10)) ;
			fail() ;
		} catch(RepositoryException ingore){
		}

		
		// not permitted
		try {
			Node another  = emps.createChild("jin").put("ename", "jin").put("age", 22).setAradonId("emps", "another");
			another.addReference("current_dept", AradonQuery.newByGroupId("dept", 99)) ; // not found
			fail() ;
		} catch(RepositoryException ingore){
		}
		

		session.commit() ;
	}
}
