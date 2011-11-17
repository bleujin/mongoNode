package net.ion.radon.repository.function;

import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.TestBaseRepository;

import org.apache.commons.lang.ArrayUtils;

public class TestDecode extends TestBaseRepository{


	public void testDecode() throws Exception {
		session.newNode().put("age", 10).put("name", "d1") ;
		session.newNode().put("age", 20).put("name", "d2") ;
		
		session.commit() ;
		
		NodeCursor cursor = session.createQuery().descending("name").find() ;
		Rows rows = NodeRows.createByCursor(Queryable.Fake, cursor, NodeColumns.create("decode(name, 'd1', age, 0)", "name")) ;
		assertEquals(10, rows.firstRow().getInt(1)) ;
		Debug.debug(rows) ;
	}
	
	public void testDecodeE() throws Exception {
		
		Object[] o = {"0", 1, 2, "3", 4, 5, 6} ;
		Debug.debug(ArrayUtils.subarray(o, 3, o.length)) ;
		
		
		assertEquals(true, recursiveDecode(new Object[]{"1", "1", true, false})) ;
		assertEquals(false, recursiveDecode(new Object[]{"1", "2", true, false})) ;
		assertEquals(true, recursiveDecode(new Object[]{"1", "3", true, "1", true})) ;
		assertEquals(false, recursiveDecode(new Object[]{"1", "3", true, "2", true, false})) ;
	}
	
	
	// Decode(a, b, c)
	// Decode(a, b, c, d)
	// Decode(a, b, c, d)
	private Object recursiveDecode(Object[] args){
		if (args.length < 3) throw new IllegalArgumentException("not permitted") ;
		if (args.length == 3) return ObjectUtil.equals(args[0], args[1]) ? args[2] : null ;
		if (args.length == 4) return ObjectUtil.equals(args[0], args[1]) ? args[2] : args[3];
		else {
			return ObjectUtil.equals(args[0], args[1]) ? args[2] : recursiveDecode(ArrayUtils.add(ArrayUtils.subarray(args, 3, args.length), 0, args[0])) ;
		}
	}
}
