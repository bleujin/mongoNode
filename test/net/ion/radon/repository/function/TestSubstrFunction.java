package net.ion.radon.repository.function;

import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.Queryable;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.TestBaseRepository;

public class TestSubstrFunction extends TestBaseRepository{


	/*
	 * substr('abc', 2) 
	 * substr(abc, 2) ;
	 * substr(abc, 2, 3) ;
	 * 
	 */
	public void testInit() throws Exception {
		
		IColumn col = Column.parse("substr('1234567890', 2)") ;
		assertTrue(col instanceof SubstrFunction) ;

		assertEquals("34567890", Column.parse("substr('1234567890', 2)").getValue(null)) ;
		assertEquals("34567890", Column.parse("substr('1234567890', 2, 3)").getValue(null)) ;
	}
	
	
	public void testOverMax() throws Exception {
		assertEquals("3", Column.parse("substr('123', 2, 10)").getValue(null)) ;
	}
	
	
	public void testNode() throws Exception {
		Node node = session.newNode().put("key", "1234567890") ;
		Rows rows = NodeRows.createByNode(Queryable.Fake, node, NodeColumns.create("substr(key, 2)")) ;
		assertEquals("34567890", rows.firstRow().getString(1)) ;
	}
	
	public void testOver() throws Exception {
		Node node = session.newNode().put("key", "123") ;
		Rows rows = NodeRows.createByNode(Queryable.Fake, node, NodeColumns.create("substr(key, 2, 5)")) ;
		assertEquals("3", rows.firstRow().getString(1)) ;
	}
	
}
