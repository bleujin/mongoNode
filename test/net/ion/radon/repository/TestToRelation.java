package net.ion.radon.repository;

import org.apache.commons.collections.Closure;
import org.apache.commons.lang.ArrayUtils;

import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.relation.IRelation;

public class TestToRelation extends TestBaseRepository {

	public void testAradonId() throws Exception {
		Node freeboard = session.newNode().setAradonId("board", "free").put("proposal", "free");
		session.commit();

		Node article = session.newNode().setAradonId("article", 1).put("subject", "bleujin").put("article", 1);
		article.toRelation("boardinfo", freeboard.selfRef());

		IRelation rel = article.relation("boardinfo");
		assertEquals(true, rel.fetch(0) != null);

		Node found = rel.fetch(0);
		assertEquals("free", found.getString("proposal"));
	}

	public void testCaseInSensitive() throws Exception {
		Node board = session.newNode().setAradonId("board", "free").put("proposal", "free");

		Node article = session.newNode().setAradonId("article", 1).put("subject", "bleujin").put("article", 1);
		article.toRelation("BOARD", board.selfRef());
		session.commit();

		IRelation rel = session.createQuery().aradonGroupId("article", 1).findOne().relation("board");
		assertEquals(true, rel.fetch(0) != null);

		Node found = rel.fetch(0);
		assertEquals("free", found.getString("proposal"));
	}

	public void testId() throws Exception {
		Node freeboard = session.newNode().put("proposal", "free");

		Node article = session.newNode().setAradonId("article", 1).put("subject", "bleujin").put("article", 1);
		article.toRelation("BOARD", NodeRef.create(freeboard));
		session.commit();

		IRelation rel = article.relation("board");
		assertEquals(true, rel.fetch(0) != null);

		Node found = rel.fetch(0);
		assertEquals("free", found.getString("proposal"));

	}

	public void testPath() throws Exception {
		Node board = session.newNode("board").put("proposal", "free");

		Node article = session.newNode().setAradonId("article", 1).put("subject", "bleujin").put("article", 1);
		article.toRelation("parent", board.selfRef());
		session.commit();

		IRelation rel = article.relation("parent");
		assertEquals(true, rel.fetch(0) != null);

		Node found = rel.fetch(0);
		assertEquals("free", found.getString("proposal"));
	}

	public void testRelationGet() throws Exception {
		Node b = session.newNode("board").setAradonId("obj", "board").put("proposal", "free").put("create", 2011).inner("reg").put("userid", "my").getParent();

		session.newNode().setAradonId("article", 1).put("subject", "bleujin").put("article", 1).toRelation("parent", b.selfRef()).inner("user").put("id", "bleujin");
		session.commit();

		Node found = session.createQuery().eq("article", 1).findOne();

		assertEquals("bleujin", found.get("user.ID"));
		assertEquals("free", found.get("#parent.proposal"));
		assertEquals(2011, found.get("#parent.create"));
		assertEquals("my", found.get("#parent.reg.userid"));
		assertEquals(null, found.get("#parent.unknown.userid"));
	}

	public void testUnknownRelation() throws Exception {
		session.newNode().setAradonId("article", 1).put("subject", "bleujin").put("article", 1).inner("user").put("id", "bleujin");
		session.commit();

		Node found = session.createQuery().eq("article", 1).findOne();
		assertEquals(null, found.get("#unknown.reg.userid"));
	}

	
	public void testRelationProperty() throws Exception {
		Node code = session.newNode().setAradonId("config", "code").inner("ncode").put("kor", "korean").put("eng", "english").getParent() ;
		Node bleujin = session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kor").toRelation("nation", code.selfRef()) ;
		session.commit() ;
		
		
		assertEquals("kor", bleujin.get("nation")) ;
		assertEquals("english", bleujin.get("#nation.ncode.eng")) ;
	}
	
	public void testCache() throws Exception {
		Node code = session.newNode().setAradonId("config", "code").inner("ncode").put("kor", "korean").put("eng", "english").getParent() ;
		Node bleujin = session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kor").toRelation("nation", code.selfRef()) ;
		Node hero = session.newNode().setAradonId("emp", "hero").put("name", "hero").put("nation", "eng").toRelation("nation", code.selfRef()) ;
		
		session.commit() ;

		NodeCursor nc = session.createQuery().aradonGroup("emp").find() ;
		nc.each(PageBean.ALL, new Closure() {
			public void execute(Object _node) {
				Node node = (Node) _node ;
				assertEquals(true, ArrayUtils.contains(new String[]{"korean","english"}, node.get("#nation.ncode.{nation}"))) ;
			}
		}) ;
	}
	
	public void testCacheNotFound() throws Exception {
		Node code = session.newNode().setAradonId("config", "code").inner("ncode").put("kor", "korean").put("eng", "english").getParent() ;
		Node bleujin = session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kor").toRelation("nation", code.selfRef()) ;
		Node hero = session.newNode().setAradonId("emp", "hero").put("name", "hero").put("nation", "eng").toRelation("nation", code.selfRef()) ;
		
		session.commit() ;
		
		session.createQuery().aradonGroupId("config", "code").remove() ;

		NodeCursor nc = session.createQuery().aradonGroup("emp").find() ;
		nc.each(PageBean.ALL, new Closure() {
			public void execute(Object _node) {
				Node node = (Node) _node ;
				assertEquals(true, node.get("#nation.ncode.{nation}") == null) ;
			}
		}) ;
	}
	

	public void testNodeColumn() throws Exception {
		Node code = session.newNode("code").setAradonId("config", "code").inner("ncode").put("kor", "korean").put("eng", "english").getParent() ;
		
		Node bleujin = session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kor").toRelation("nation", code.selfRef()) ;
		Node hero = session.newNode().setAradonId("emp", "hero").put("name", "hero").put("nation", "eng").toRelation("nation", code.selfRef()) ;
		session.commit() ;
		
		NodeCursor nc = session.createQuery().aradonGroup("emp").find() ;
		
		Rows rows = NodeRows.createByCursor(Queryable.Fake, nc, NodeColumns.create("name", "nation", "#nation.ncode.{nation} nstring")) ;
		Debug.line(rows) ;
		
	}

}









