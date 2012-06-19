package net.ion.radon.repository.innode;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.repository.InListNode;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.TestBaseRepository;

public class TestInListQuery extends TestBaseRepository {

	public void testPropertyQuery() throws Exception {
		session.newNode().put("name", "bleujin").put("address", "seoul").put("view", 2).inlist("friend")
				.push(MapUtil.chainKeyMap().put("name", "jin").put("age", 25).put("city", "busan"))
				.push(MapUtil.chainKeyMap().put("name", "hero").put("age", 20).put("city", "busan"))
				.push(MapUtil.chainKeyMap().put("name", "novision").put("age", 20).put("city", "busan"));

		session.commit();
		
		InListNode listnode = session.createQuery().findOne().inlist("friend") ;
		
		assertEquals(1, listnode.createQuery().addFilter(PropertyQuery.create().gte("age", 25)).find().size()) ;
		assertEquals(1, listnode.createQuery().addFilter(PropertyQuery.create().gt("age", 20)).find().size()) ;
		assertEquals(3, listnode.createQuery().addFilter(PropertyQuery.create().lte("age", 25)).find().size()) ;
		assertEquals(2, listnode.createQuery().addFilter(PropertyQuery.create().lt("age", 25)).find().size()) ;
		assertEquals(1, listnode.createQuery().addFilter(PropertyQuery.create().eq("age", 25)).find().size()) ;
		assertEquals(1, listnode.createQuery().addFilter(PropertyQuery.create().in("age", new Object[]{25})).find().size()) ;
		assertEquals(2, listnode.createQuery().addFilter(PropertyQuery.create().nin("age", new Object[]{25})).find().size()) ;
		assertEquals(3, listnode.createQuery().addFilter(PropertyQuery.create().isExist("age")).find().size()) ;
		assertEquals(0, listnode.createQuery().addFilter(PropertyQuery.create().isNotExist("age")).find().size()) ;
		
	}
}
