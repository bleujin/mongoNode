package net.ion.radon.repository;

import net.ion.framework.util.MapUtil;

public class TestParticialNode extends TestBaseRepository {

	public void testWithColumns() throws Exception {
		session.newNode().put("fname", "bleu").put("lname", "jin").put("age", 20);
		session.commit();

		Node found = session.createQuery().findOne(Columns.append().add("fname", "lname"));

		assertEquals("bleu", found.getString("fname"));
		assertEquals("jin", found.getString("lname"));
		assertNull(found.getString("age"));

		found.put("lname", "hero");
		try {
			session.commit();
			fail();
		} catch (IllegalArgumentException expect) {
		} catch (Exception ex) {
			fail();
		}
	}

	public void testPush() throws Exception {
		InListNode dept = session.newNode().put("fname", "bleu").inlist("dept");
		dept.push(MapUtil.<String, Object> chainMap().put("dname", "dev").put("dno", 10)).push(MapUtil.<String, Object> chainMap().put("dname", "sol").put("dno", 20));

		session.commit();

		Node found = session.createQuery().findOne(Columns.append().add("fname").slice("dept", 1));
		assertEquals("bleu", found.getString("fname"));
		assertEquals(1, found.inlist("dept").createQuery().find().size());

		found.put("fname", "jin");
		try {
			session.commit();
		} catch (IllegalArgumentException expect) {
		} catch (Exception ex) {
			fail();
		}

	}

}
