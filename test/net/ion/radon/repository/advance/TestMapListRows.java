package net.ion.radon.repository.advance;

import java.util.List;
import java.util.Map;

import net.ion.framework.db.Rows;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapListRows;
import net.ion.framework.util.MapUtil;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.TestBaseRepository;

public class TestMapListRows extends TestBaseRepository{

	public void testInList() throws Exception {
		session.newNode().put("name", "bleujin").inlist("friend")
			.push(MapUtil.chainKeyMap().put("name", "novision").put("age", 20))
			.push(MapUtil.chainKeyMap().put("name", "iihi").put("age", 21)) ;
		session.commit() ;
		
		Node found = session.createQuery().findOne() ;
		List<InNode> ins = found.inlist("friend").createQuery().find() ;
		
		List<Map<String, ? extends Object>> datas = ListUtil.newList() ;
		
		for (InNode in : ins) {
			datas.add(in.toMap()) ;
		}
		
		Rows rows = MapListRows.create(datas, new String[]{"name", "age", "unknown"}) ;
		assertEquals("novision", rows.firstRow().getString("name")) ;
		assertEquals(20, rows.firstRow().getInt("age")) ;
		assertEquals(true, null == rows.firstRow().getString("unknown")) ;
			
	}
}
