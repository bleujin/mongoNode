package net.ion.radon.repository.function;

import java.util.Date;

import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.TestBaseRepository;

public class TestFunction extends TestBaseRepository{
	
	public void testColumnParse() throws Exception {
		assertEquals("c", Column.parse("nvl(a, b) c").getLabel()) ;

		assertEquals("c", Column.parse("'abc' c").getLabel()) ;
	}
	
	
	public void testSecurity() throws Exception {
		Node node = session.newNode() ;
		node.putEncrypt("abc", "cccc") ;
		
		assertEquals(true, node.isMatchEncrypted("abc", "cccc")) ;
	}
	
	
	public void testToCharFromDate() throws Exception {
		final String expr = "toChar(a, 'yyyyMMdd HHmmss') c";
		assertEquals("c", Column.parse(expr).getLabel()) ;
		assertEquals(true, Column.parse(expr)  instanceof TocharFunction) ;
	}
	
	public void testToChar() throws Exception {
		Debug.debug(DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss")) ;
		Debug.debug(DateUtil.dateToString(new Date(), "yyyyMMdd-HHmmss")) ;
		
	}
	
	
	public void testDateColumn() throws Exception {
		
		final Date d = new Date();
		final String format = "yyyyMMdd-HHmmss";
		String expect = DateUtil.dateToString(d, format) ;
		Node node = session.newNode() ;
		node.put("date", d) ;
		
		Rows rows = NodeRows.createByNode(Queryable.Fake, node, NodeColumns.create("toChar(date, 'yyyyMMdd-HHmmss') date")) ;
		
		Debug.debug(rows.firstRow().getString("date")) ;
		
		assertEquals(expect, rows.firstRow().getString(1)) ;
		assertEquals(expect, rows.firstRow().getString("date")) ;
	}
	
	
	public void testDateOrder() throws Exception {
		Date d1 = new Date(new Date().getTime() - 1000000) ;
		Date d2 = new Date() ;
		
		session.newNode().put("date", d1).put("name", "d1") ;
		session.newNode().put("date", d2).put("name", "d2") ;
		
		session.commit() ;
		
		
		NodeCursor cursor = session.createQuery().descending("date").find() ;
		Rows rows = NodeRows.createByCursor(Queryable.Fake, cursor, NodeColumns.create("date", "name")) ;
		
		assertEquals("d2", rows.firstRow().getString("name")) ;
	}
	
	public void testPrefix() throws Exception {
		String a = "'a'" ;
		assertEquals(true, a.startsWith("'")) ;
	}
	
	
	
	
}
