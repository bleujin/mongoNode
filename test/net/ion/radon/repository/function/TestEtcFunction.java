package net.ion.radon.repository.function;

import net.ion.framework.db.DBController;
import net.ion.framework.db.IDBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.manager.OracleDBManager;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.Debug;
import net.ion.framework.util.NumberUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.TestBaseRepository;

public class TestEtcFunction extends TestBaseRepository{


	public void testToNumber() throws Exception {
		Object i = -10 ;
		Debug.debug(NumberUtil.toLong(String.valueOf(i))) ;
	}
	
	public void testSign() throws Exception {
		
		IColumn col = Column.parse("sign(1)") ;
		assertTrue(col instanceof SignFunction) ;

		assertEquals(1, Column.parse("sign(5)").getValue(null)) ;
		assertEquals(-1, Column.parse("sign(-10)").getValue(null)) ;
	}
	
	public void testLength() throws Exception {
		IColumn col = Column.parse("length('heeya')") ;
		assertTrue(col instanceof LengthFunction) ;
		
		assertEquals(5, Column.parse("length('heeya')").getValue(null)) ;
	}
	

	public void testNode() throws Exception {
		Node node = session.newNode() ;
		node.put("name", "bleujin").put("age", 10) ;
		
		session.commit() ;
		
		Rows rows = NodeRows.createByCursor(Queryable.Fake, session.createQuery().find(), NodeColumns.create("length(name) len")) ;
		assertEquals(7, rows.firstRow().getObject("len")) ;
	}

	

	
}
