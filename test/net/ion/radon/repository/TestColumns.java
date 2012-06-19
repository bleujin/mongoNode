package net.ion.radon.repository;

import net.ion.framework.util.MapUtil;

public class TestColumns extends TestBaseRepository {

	public void testSelect() throws Exception {
		Session session = createSampleNode();
		Node node = session.createQuery().eq("name", "bleujin").findOne(Columns.exclude().add("age"));
		assertNull(node.get("age"));
	}

	public void testBlankColumn() throws Exception {
		Session session = createSampleNode();
		Node node = session.createQuery().eq("name", "bleujin").findOne(Columns.ALL);
		assertNotNull(node.get("name"));
		assertNotNull(node.get("address"));
		assertNotNull(node.get("age"));
		assertNotNull(node.get("loc"));
	}

	public void testSelectDup() throws Exception {
		Session session = createSampleNode();
		Node node = session.createQuery().eq("name", "bleujin").findOne(Columns.exclude().add("age").add("address"));
		assertNull(node.get("age"));
	}

	public void testCompositeSelect() throws Exception {
		Session session = createSampleNode();
		Node node = session.createQuery().eq("name", "hero").findOne(Columns.exclude().add("age"));
		assertNull(node.get("loc.x"));
	}

	public void testCompositeSelect2() throws Exception {
		Session session = createSampleNode();
		Node node = session.createQuery().eq("name", "bleujin").findOne(Columns.exclude().add("loc.y"));
		assertNotNull(node.get("loc.x"));
		assertNull(node.get("loc.y"));
	}

	public void testSlice() throws Exception {
		Session session = createSampleNode();
		Node node = session.createQuery().eq("name", "hero").findOne(Columns.append().slice("school", 10, 10));

		assertEquals(10, node.inlist("school").createQuery().find().size());
		assertEquals(node.inlist("school").createQuery().findOne().getString("index"), "10");
	}

	public void testExcludeSlice() throws Exception {
		Session session = createSampleNode();
		try {
			Node node = session.createQuery().eq("name", "hero").findOne(Columns.exclude().slice("school", 10, 10));
			fail();
		} catch (IllegalArgumentException ignore) {
		}

	}

	private Session createSampleNode() {
		session.dropWorkspace();

		session.newNode().put("name", "bleujin").put("address", "seoul").put("age", 20).inner("loc").put("x", 1).put("y", 2).put("cel", "011-111-1111");
		InListNode inlist = session.newNode().put("name", "hero").put("address", "busan").put("age", 21).inlist("school");
		for (int i = 0; i < 100; i++) {
			inlist.push(MapUtil.create("index", i));
		}

		session.newNode().put("name", "gnic").put("address", "seoul").put("age", 22);
		session.newNode().put("name", "novision").put("address", "seoul").put("age", 22);
		session.commit();

		return session;
	}
}
