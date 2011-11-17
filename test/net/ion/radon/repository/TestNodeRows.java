package net.ion.radon.repository;

import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.Queryable;
import net.ion.radon.repository.myapi.ICursor;

public class TestNodeRows extends TestBaseRepository {


	public void testCreate() throws Exception {
		Rows rows = makeRow();

		assertEquals(true, rows.first());
		assertEquals("hello", rows.getString(1));
	}

	private Rows makeRow() throws SQLException {
		Node node = session.newNode("hello").put("string", "hello").put("boolean", true).put("int", new Integer(10)).put("long", new Long(20)).put("double", new Double(30)).put("date", new Date(System.currentTimeMillis()));
		
		Rows rows = NodeRows.createByNode(Queryable.Fake, node, NodeColumns.create("string", "boolean", "int", "long", "double", "date"));
		return rows;
	}

	public void testFavoriate() throws Exception {
		Rows rows = makeRow();

		while (rows.next()) {
			assertEquals(1, rows.getRowCount());
			assertEquals(10, rows.getInt("int"));
			assertEquals("10", rows.getString("int"));
			assertEquals(true, rows.first()) ;
			assertEquals(true, rows.last()) ;
			rows.beforeFirst() ;
			rows.afterLast() ;
			rows.last() ;
			assertEquals(false, rows.previous()) ;
			assertEquals(true, rows.absolute(1)) ;
		}
	}

	public void testMeta() throws Exception {
		Rows rows = makeRow();
		ResultSetMetaData meta = rows.getMetaData() ;

		assertEquals(Types.LONGVARCHAR, meta.getColumnType(1)) ;
		assertEquals(Types.BOOLEAN, meta.getColumnType(2)) ;
		assertEquals(Types.INTEGER, meta.getColumnType(3)) ;
		assertEquals(Types.BIGINT, meta.getColumnType(4)) ;
		assertEquals(Types.DOUBLE, meta.getColumnType(5)) ;
		assertEquals(Types.DATE, meta.getColumnType(6)) ;
		
	}
	
	public void testValue() throws Exception {
		
		Rows rows = makeRow();
		rows.first();
		Date date = rows.getDate("date");
		Date currDate = new Date(System.currentTimeMillis());
		assertEquals(currDate.getDate(), date.getDate());
	}
	
	public void testCursor() throws Exception {
		Node parent = makeRows();

		
		final ICursor childCursor = parent.getChild();
		Rows rows = NodeRows.createByCursor(Queryable.Fake, childCursor, NodeColumns.create("key"));
		assertEquals(3, rows.getRowCount());
	}

	private Node makeRows() {
		Node parent = session.newNode("hello");
		parent.createChild("child1").put("key", "child1");
		parent.createChild("child2").put("key", "child2");
		parent.createChild("child3").put("key", "child3");
		
		session.commit() ;
		return parent;
	}
	
	public void testCursor2() throws Exception {
		makeRows() ;
		
		final StopWatch sw = new StopWatch("make nodeRows");
		final NodeCursor cursor = session.createQuery().find();
		final NodeColumns columns = NodeColumns.create("key");
		sw.current() ;
		Rows rows = NodeRows.createByCursor(Queryable.Fake, cursor, columns);
		sw.end() ;
		assertEquals(4, rows.getRowCount());
		
		ResultSetMetaData meta = rows.getMetaData() ;
		assertEquals(1, meta.getColumnCount()) ;
		assertEquals("key", meta.getColumnName(1)) ;
	}
	
	public void testReference() throws Exception {
		Node dev = session.newNode().setAradonId("dept", "dev").put("deptno", 20).put("dname", "dev") ;
		Node bleujin = session.newNode().setAradonId("employee", "bleujin").put("id", "bleujin").put("name", "bleu").put("age", 20) ;
		bleujin.toRelation("dept", dev.selfRef()) ;
		session.commit() ;
		
		NodeColumns columns = NodeColumns.create("id", "name", "#dept.deptno", "age", "#dept.deptno dno");
		Rows rows = NodeRows.createByNode(Queryable.Fake, bleujin, columns) ;
		assertEquals(5, rows.getMetaData().getColumnCount());
		assertEquals("deptno",rows.getMetaData().getColumnName(3));
		
		assertEquals(20, rows.firstRow().getObject("deptno")) ;
		assertEquals(20, rows.firstRow().getObject("dno")) ;
	}
	
	
	public void testDecode() throws Exception {

		session.newNode().put("key", "child1").put("p1", "p1value");
		session.newNode().put("key", "child2").put("p1", "p1value");
		session.newNode().put("key", "child3").put("p2", "p2value");
		
		session.commit() ;
		
		NodeColumns columns = NodeColumns.create(Column.parse("key mykey"), Column.nvl("p1", "p2", "p"), Column.constant(1, "c"));
		Rows rows = NodeRows.createByCursor(Queryable.Fake, session.createQuery().find(), columns) ;
	
		assertEquals(3, rows.getRowCount()) ;
	}
	
	public void testColumn() throws Exception {
		session.newNode().put("key", "child1").put("p1", "p1value");
		session.newNode().put("key", "child2").put("p1", "p1value");
		session.newNode().put("key", "child3").put("p2", "p2value");
		
		session.commit() ;
		
		NodeColumns columns = NodeColumns.create("key mykey", "nvl(p1, p2) p", "1 c");
		Rows rows = NodeRows.createByCursor(Queryable.Fake, session.createQuery().find().descending("p1"), columns) ;
	
		rows.absolute(3) ;
		assertEquals("p2value", rows.getString("p")) ;
	}

	
	public void testSort() throws Exception {
		session.newNode().setAradonId("group1", "mySugg1").put("suggId", "mySugg1").put("suggNm", "mySugg1") ;
		session.newNode().setAradonId("group1", "mySugg2").put("suggId", "mySugg2").put("suggNm", "mySugg2") ;
		session.newNode().setAradonId("group1", "mySugg2").put("suggId", "mySugg3").put("suggNm", "mySugg2") ;
		session.commit() ;
		
		Node node1 = super.createQuery().aradonGroup("group1").ascending("suggId").find().next() ;
		assertEquals("mySugg1", node1.getString("suggid")) ;
		
		Rows rows = NodeRows.createByCursor(Queryable.Fake, session.createQuery().aradonGroup("group1").ascending("suggId").find(), NodeColumns.create("suggId catId", "'suggest' upperCatId", "suggNm catNm", "'suggest' type")) ;
		assertEquals("mySugg1", rows.firstRow().getString("catId")) ;
	}

	
	public void testUnionAll() throws Exception {
		session.newNode().setAradonId("group1", "mySugg1").put("suggId", "mySugg1").put("suggNm", "mySugg1") ;
		session.newNode().setAradonId("group1", "mySugg2").put("suggId", "mySugg2").put("suggNm", "mySugg2") ;
		
		Node n2 = session.newNode().put("scheId", 3).put("scheNm", "mySche").put("scheType", "auto") ;
		Node n3 = session.newNode().put("ctexId", "myCtex").put("ctexNm", "myCtex") ;
		session.commit() ;
		
		Rows rows1 = NodeRows.createByCursor(Queryable.Fake, session.createQuery().aradonGroup("group1").ascending("suggId").find(), NodeColumns.create("suggId catId", "'suggest' upperCatId", "suggNm catNm", "'suggest' type")) ;
		Rows rows2 = NodeRows.createByNode(Queryable.Fake, n2, NodeColumns.create("scheId catId", "'auto_grp' upperCatId", "scheNm catNm", "'auto' type")) ;
		Rows rows3 = NodeRows.createByNode(Queryable.Fake, n3, NodeColumns.create("ctexId catId", "'context' upperCatId", "ctexNm catNm", "'context' type")) ;

		Rows urows = NodeRows.unionAll(rows1, rows2, rows3) ;
		
		assertEquals(4, urows.getRowCount()) ;
		assertEquals("mySugg1", urows.firstRow().getString("catId")) ;
	}
	
	
	
	
	
}
