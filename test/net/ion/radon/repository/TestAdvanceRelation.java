package net.ion.radon.repository;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.Queryable;

public class TestAdvanceRelation extends TestBaseRepository {
	
	public void testFirst() throws Exception {
		Node dev = session.newNode().setAradonId("dept", "dev").put("deptno", 20).put("dname", "dev");
		Node bleujin = session.newNode().setAradonId("employee", "bleujin").put("id", "bleujin").put("name", "bleu").put("age", 20);

		bleujin.toRelation("dept", dev.selfRef()) ;
		session.commit() ;
		
		Rows rows = NodeRows.createByNode(Queryable.Fake, bleujin, NodeColumns.create("id", "#dept.deptno"));
		
		assertEquals(20, rows.firstRow().getInt("deptno"));
	}
	
	
	public void testAradonDuplicate() throws Exception {
		session.newNode().setAradonId("dept", "dev").put("deptno", 20);
		session.commit() ;
		
		Node dupNode = session.newNode() ;
		try {
			dupNode.setAradonId("dept", "dev");
			fail() ;
		} catch(RepositoryException ignore){
		}
	}
	
	public void testDifferentWorksapce() throws Exception {
		session.changeWorkspace("dworkspace");
		session.dropWorkspace() ;
		
		Node dev = session.newNode().setAradonId("dept", "dev").put("deptno", 20).put("dname", "dev");
		session.commit();

		session.changeWorkspace("eworkspace") ;
		session.dropWorkspace() ;
		
		Node bleujin = session.newNode().setAradonId("employee", "bleujin").put("id", "bleujin").put("name", "bleu").put("age", 20);
		bleujin.toRelation("dept", dev.selfRef()) ;
		session.commit() ;
		
		Node found = session.createQuery().findOneInDB("employee", "bleujin") ;

		assertEquals("dev", found.get("#dept.dname")) ;
	}
	
	
	public void testRelation() throws Exception {
		Node dev = session.newNode().setAradonId("dept", "dev").put("deptno", 20).put("dname", "dev");
		Node bleujin = session.newNode().setAradonId("employee", "bleujin").put("id", "bleujin").put("name", "bleu").put("age", 20);
		bleujin.toRelation("dept", dev.selfRef()) ;
		session.commit() ;

		NodeColumns columns = NodeColumns.create("id", "name", "#dept.deptno deptno", "#dept.deptno dno");
		
		
		Rows rows = NodeRows.createByNode(Queryable.Fake, bleujin, columns);
		assertEquals(4, rows.getMetaData().getColumnCount());
		assertEquals("deptno", rows.getMetaData().getColumnName(3));

		assertEquals(20, rows.firstRow().getObject("deptno"));
		assertEquals(20, rows.firstRow().getObject("dno"));
	}


	
	public void testCaseInsentiveReference() throws Exception {
		Node dev = session.newNode().setAradonId("dept", "dev").put("deptno", 20).put("dname", "dev");
		Node bleujin = session.newNode().setAradonId("employee", "bleujin").put("id", "bleujin").put("name", "bleu").put("age", 20);
		bleujin.toRelation("dept", dev.selfRef()) ;
		session.commit() ;

		Rows rows = NodeRows.createByNode(Queryable.Fake, bleujin, "id", "name", "#Dept.deptno", "#dePt.dePtno Dno");
		assertEquals(4, rows.getMetaData().getColumnCount());
		assertEquals("deptno", rows.getMetaData().getColumnName(3));

		assertEquals(20, rows.firstRow().getObject("deptno"));
		assertEquals(20, rows.firstRow().getObject("dno"));
	}


}
