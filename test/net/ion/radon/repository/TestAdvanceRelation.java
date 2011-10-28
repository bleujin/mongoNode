package net.ion.radon.repository;

import java.util.List;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.radon.repository.myapi.ICursor;

public class TestAdvanceRelation extends TestBaseRepository {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		session.dropDB() ;
	}
	
	
	public void testSetReference() throws Exception {
		Node bleujin = initEmployee() ;
		bleujin.addReference("dept", AradonQuery.newByGroupId("dept", "dev")) ;
		session.commit() ;
		
		Rows rows = NodeRows.createByNode(Queryable.Fake, bleujin, NodeColumns.create("id", "dept.deptno"));
		
		assertEquals(20, rows.firstRow().getInt("deptno"));
	}
	
	
	public void testAradonDuplicate() throws Exception {
		Node dev = session.newNode();
		dev.setAradonId("dept", "dev");
		dev.put("deptno", 20);
		
		session.commit() ;
		
		Node dupNode = session.newNode() ;
		try {
			dupNode.setAradonId("dept", "dev");
			fail() ;
		} catch(RepositoryException ignore){
		}
	}
	
	public void testFindByGroup() throws Exception {
		Node bleujin = initEmployee() ;
		bleujin.addReference("dept", AradonQuery.newByGroupId("dept", "dev")) ;
		session.commit() ;
		
		Node dev = session.createQuery().eq("deptno", 20).findOne();
		
		session.changeWorkspace("_reference") ;
		session.createQuery().find().debugPrint(PageBean.ALL) ;
		
		ICursor cursor = session.createRefQuery().to(dev,"dept").find();
		assertEquals(true, cursor.hasNext()) ;
		Debug.debug(cursor.next()) ;
		
		ReferenceTaragetCursor nodes =  dev.getReferencedNodes("employee") ;
		assertEquals(1, nodes.size());
	}
	
	
	
	
	public void testDifferentWorksapce() throws Exception {
		
		session.changeWorkspace("dworkspace") ;
		Node dev = session.newNode();
		dev.setAradonId("dept", "dev");
		dev.put("deptno", 20);
		dev.put("dname", "dev");
		session.commit();

		session.changeWorkspace("eworkspace") ;
		Node bleujin = session.newNode();
		bleujin.setAradonId("employee", "bleujin");
		bleujin.put("id", "bleujin");
		bleujin.put("name", "bleu");
		bleujin.put("age", 20);

//		session.setReference(dev, "belongto", bleujin);
		session.commit();
		
		bleujin.addReference("dept", AradonQuery.newByGroupId("dept", "dev")) ;
		session.commit() ;
		
		Node findDept = session.createQuery().findOneInDB("dept", "dev") ;
		
		session.changeWorkspace("eworkspace") ;
		
		ReferenceTaragetCursor employees =  findDept.getReferencedNodes("employee") ;
		assertEquals(1, employees.size());
	}
	
	
	private Node initEmployee() {
		Node dev = session.newNode();
		dev.setAradonId("dept", "dev");
		dev.put("deptno", 20);
		dev.put("dname", "dev");

		Node bleujin = session.newNode();
		bleujin.setAradonId("employee", "bleujin");
		bleujin.put("id", "bleujin");
		bleujin.put("name", "bleu");
		bleujin.put("age", 20);

//		session.setReference(dev, "belongto", bleujin);
		session.commit();
		return bleujin;
	}

	public void testReference() throws Exception {
		Node bleujin = initEmployee();
		bleujin.addReference("dept", AradonQuery.newByGroupId("dept", "dev")) ;
		session.commit() ;

		NodeColumns columns = NodeColumns.create("id", "name", "dept.deptno", "dept.deptno dno");
		Rows rows = NodeRows.createByNode(Queryable.Fake, bleujin, columns);
		assertEquals(4, rows.getMetaData().getColumnCount());
		assertEquals("deptno", rows.getMetaData().getColumnName(3));

		assertEquals(20, rows.firstRow().getObject("deptno"));
		assertEquals(20, rows.firstRow().getObject("dno"));
	}


	
	public void testCaseInsentiveReference() throws Exception {
		Node bleujin = initEmployee();
		bleujin.addReference("dept", AradonQuery.newByGroupId("dept", "dev")) ;
		session.commit() ;

		Rows rows = NodeRows.createByNode(Queryable.Fake, bleujin, "id", "name", "Dept.deptno", "dePt.dePtno Dno");
		assertEquals(4, rows.getMetaData().getColumnCount());
		assertEquals("deptno", rows.getMetaData().getColumnName(3));

		Debug.line(rows.firstRow().toMap()) ;
		assertEquals(20, rows.firstRow().getObject("deptno"));
		assertEquals(20, rows.firstRow().getObject("dno"));
	}


}
