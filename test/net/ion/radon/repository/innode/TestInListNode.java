package net.ion.radon.repository.innode;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.Node;

public class TestInListNode extends TestBaseInListQuery {

	public void testEqualFilter() throws Exception {
		createNode();
		Node found = session.createQuery().findOne();

		assertEquals(0, found.inlist("people").createQuery().eq("index", 0).findOne().get("index"));
		assertEquals(1, found.inlist("people").createQuery().eq("index", 1).findOne().get("index"));
		assertEquals(4, found.inlist("people").createQuery().eq("index", 4).findOne().get("index"));

		assertEquals(1, found.inlist("people").createQuery().eq("index", 1).find().size());
		assertEquals(5, found.inlist("people").createQuery().eq("address.city", "seoul").find().size());
		assertEquals(1, found.inlist("people").createQuery().eq("address.city", "seoul").eq("index", 1).find().size());

		assertEquals(4, found.inlist("people").createQuery().ne("index", 1).find().size());
	}

	public void testNotEqualFilter() throws Exception {
		createNode();
		Node found = session.createQuery().findOne();

		assertEquals(4, found.inlist("people").createQuery().ne("index", 0).find().size());
	}

	public void testGreater() throws Exception {
		createNode();
		Node found = session.createQuery().findOne();

		assertEquals(1, found.inlist("people").createQuery().gt("index", 3).find().size());
		assertEquals(2, found.inlist("people").createQuery().gte("index", 3).find().size());
	}

	public void testLess() throws Exception {
		createNode();
		Node found = session.createQuery().findOne();

		assertEquals(2, found.inlist("people").createQuery().lt("index", 2).find().size());
		assertEquals(3, found.inlist("people").createQuery().lte("index", 2).find().size());

		assertEquals(2, found.inlist("people").createQuery().between("index", 2, 3).find().size());
	}

	public void testIn() throws Exception {
		createNode();
		Node found = session.createQuery().findOne();

		assertEquals(2, found.inlist("people").createQuery().in("index", new Object[] { 2, 3 }).find().size());
	}

	public void testExist() throws Exception {
		createNode();
		Node found = session.createQuery().findOne();

		assertEquals(5, found.inlist("people").createQuery().exist("index").find().size());
	}

	public void testGet() throws Exception {
		createNode();
		Node found = session.createQuery().findOne();

		Object pvalue = found.inlist("people").get(0);
		Debug.line(pvalue.getClass(), pvalue);
	}

	public void testComplicate() throws Exception {
		Node node = session.newNode().put("name", "bleujin");
		node.inlist("friend").push(MapUtil.chainMap().put("name", "novision"));
		node.inlist("friend").push(MapUtil.chainMap().put("name", "pm1200"));

		((InNode) node.inlist("friend").get(0)).inner("address").put("city", "seoul");

		session.commit();

		session.createQuery().find().debugPrint(PageBean.ALL);

		Node found = session.createQuery().findOne();
		InNode novision = found.inlist("friend").createQuery().findOne();
		assertEquals("seoul", novision.inner("address").get("city"));
	}

	public void testIndex() throws Exception {
		Node node = session.newNode().put("name", "bleujin");
		node.inlist("friend").push(MapUtil.chainMap().put("name", "novision"));
		node.inlist("friend").push(MapUtil.chainMap().put("name", "pm1200"));

		InNode inode = node.inlist("friend").createQuery().eq("name", "pm1200").findOne();
		assertEquals(1, inode.getIndex());

		assertEquals(0, node.inlist("friend").createQuery().eq("name", "novision").findOne().getIndex());
	}


}
