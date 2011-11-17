package net.ion.radon.repository;

import net.ion.framework.util.MapUtil;

public class TestAdvanceColumns extends TestBaseRepository {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Node node = session.newNode().put("name", "bleujin");
		node.inlist("comfr").
			push(MapUtil.chainMap().put("name", "novision").put("age", 20)).
			push(MapUtil.chainMap().put("name", "iihi").put("age", 30)).
			push(MapUtil.chainMap().put("name", "pm1200").put("age", 40));
		session.commit();
	}

	public void testSlice() throws Exception {
		Node found = session.createQuery().eq("name", "bleujin").findOne(Columns.append().slice("comfr", 2));
		assertEquals("bleujin", found.get("name"));
		assertEquals(2, found.inlist("comfr").createQuery().find().size());
	}

}
