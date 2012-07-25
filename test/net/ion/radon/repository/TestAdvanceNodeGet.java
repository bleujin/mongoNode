package net.ion.radon.repository;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;

public class TestAdvanceNodeGet extends TestBaseRepository {

	public void testBlankGet() throws Exception {
		Node node = session.newNode().put("name", "bleujin");
		assertEquals(true, node.get("") == null);
	}

	// path expr !/nation.ncode.{nation} or !wsname:/nation.ncode.{nation}
	// aid expr $config:nation.ncode.{nation} or $wsname:config:nation.ncode.{nation}

	public void testByPath() throws Exception {
		session.newNode("nation").setAradonId("config", "nation").put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent();
		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor");
		session.commit();

		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne();
		assertEquals("korea", found.get("!/nation.{nation}"));
	}

	public void testPathWorkspace() throws Exception {
		Node nation = session.newNode("nation").setAradonId("config", "nation").put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent();
		session.changeWorkspace("bleujin");
		session.dropWorkspace();

		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor").put("wsname", nation.getWorkspaceName());
		session.commit();

		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne();
		assertEquals("korea", found.get("!{wsname}:/nation.{nation}"));
	}

	public void testPathWorkspace2() throws Exception {
		Node nation = session.newNode("nation").setAradonId("config", "nation").put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent();
		session.changeWorkspace("bleujin");
		session.dropWorkspace();

		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor").put("path", "/nation").put("wsname", nation.getWorkspaceName());
		session.commit();

		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne();
		assertEquals("korea", found.get("!{wsname}:{path}.{nation}"));
	}

	public void testByAradonId() throws Exception {
		Node nation = session.newNode("nation").setAradonId("config", "nation").put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent();
		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor").put("path", "/nation").put("wsname", nation.getWorkspaceName());
		session.commit();

		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne();
		assertEquals("korea", found.get("$config:nation.{nation}"));
	}

	public void testByAradonIdWorkspace() throws Exception {
		Node nation = session.newNode("nation").setAradonId("config", "nation").put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent();
		session.changeWorkspace("bleujin");
		session.dropWorkspace();

		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor").put("path", "/nation").put("wsname", nation.getWorkspaceName());
		session.commit();

		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne();
		assertEquals("korea", found.get("$abcd:config:nation.{nation}"));
		assertEquals("korea", found.getString("$abcd:config:nation.{nation}"));
	}

	public void testByAradonIdWorkspace2() throws Exception {
		Node nation = session.newNode("nation").setAradonId("config", "nation").put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent();
		session.changeWorkspace("bleujin");
		session.dropWorkspace();

		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor").put("gid", "config").put("aid", "nation").put("wsname", nation.getWorkspaceName());
		session.commit();

		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne();
		assertEquals("korea", found.get("${wsname}:{gid}:{aid}.{nation}"));
	}

	public void testByAradonIdNumeric() throws Exception {
		Node nation = session.newNode("nation").setAradonId("config", 3).put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent();

		session.changeWorkspace("bleujin");
		session.dropWorkspace();

		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor").put("agroup", "config").put("auid", 3).put("wsname", nation.getWorkspaceName());
		session.commit();

		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne();
		assertEquals("korea", found.get("${wsname}:{agroup}:{auid}.{nation}"));
	}

	public void testWhenNotExist() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.commit() ;
		
		Node found = session.createQuery().findOne() ;
		assertEquals("bleujin", found.getString("name")) ;
		assertEquals(0, found.getAsInt("ne")) ;
		assertEquals(0, found.getAsInt("$number:num1.count")) ;

		session.newNode().setAradonId("number", "num1").put("count", 2) ;
		session.commit() ;

		found = session.createQuery().eq("name", "bleujin").findOne() ;
		assertEquals(2, found.getAsInt("$number:num1.count")) ;
		
	}
	
	public void testInListNodeExpression() throws Exception {

		session.newNode().put("connnm", "MyOracle").setAradonId("dev_oracle", "dev_oracle");

		session.newNode().put("repid", "test").put("connid", "dev_oracle").inlist("context").push(MapUtil.chainMap().put("connid", "dev_oracle")).push(MapUtil.chainMap().put("connid", "dev_oracle"));
		session.commit();

		Node found = session.createQuery().eq("repid", "test").findOne();

		assertEquals("MyOracle", found.get("${context.1.connid}:{context.1.connid}.connnm"));
		assertEquals("MyOracle", found.get("${context.0.connid}:{context.1.connid}.connnm"));
	}

	public void testComplate() throws Exception {
		session.newNode().put("connnm", "MyOracle").setAradonId("grp", "grp_3");
		session.newNode().put("name", "1").put("groupid", "grp").put("aid", 3);
		session.newNode().put("name", "2").put("groupid", "grp").put("aid", 3L);
		session.commit();

		Node name1 = session.createQuery().eq("name", "1").findOne();
		assertEquals("MyOracle", name1.get("${groupid}:{groupid}_{aid}.connnm"));

		Node name2 = session.createQuery().eq("name", "2").findOne();
		assertEquals("MyOracle", name2.get("${groupid}:{groupid}_{aid}.connnm"));
	}
	
	public void testNumericInt() throws Exception {
		session.newNode().setAradonId("grp", 3).put("type", "int");
		session.newNode().put("name", "finder").put("int", 3);
		session.commit() ;
		
		Node finder = session.createQuery().eq("name", "finder").findOne();
		assertEquals("int", finder.get("$grp:{int}.type"));
	}
	
	public void testNumericLong() throws Exception {
		session.newNode().setAradonId("grp", 3L).put("type", "long");
		session.newNode().put("name", "finder").put("int", 3L);
		session.commit() ;
		
		Node finder = session.createQuery().eq("name", "finder").findOne();
		assertEquals("long", finder.get("$grp:{int}.type"));
	}

	public void testNumericConvert() throws Exception {
		session.newNode().setAradonId("grp", 3L).put("type", "long");
		session.newNode().put("name", "finder").put("int", 3);
		session.commit() ;
		
		Node finder = session.createQuery().eq("name", "finder").findOne();
		assertEquals("long", finder.get("$grp:{int}.type"));
	}
	
	public void testNumericConvert2() throws Exception {
		session.newNode().setAradonId("grp", 3).put("type", "int");
		session.newNode().put("name", "finder").put("int", 3L);
		session.commit() ;
		
		Node finder = session.createQuery().eq("name", "finder").findOne();
		assertEquals("int", finder.get("$grp:{int}.type"));
	}
	

	public void testType() throws Exception {
		session.newNode().put("int", Integer.MAX_VALUE).put("long", Long.MAX_VALUE).put("num", 1L * Integer.MAX_VALUE);
		session.commit();

		assertEquals(true, session.createQuery().eq("int", Integer.MAX_VALUE).existNode());
		assertEquals(true, session.createQuery().eq("long", Long.MAX_VALUE).existNode());
		assertEquals(true, session.createQuery().eq("num", Integer.MAX_VALUE).existNode());
		assertEquals(true, session.createQuery().eq("num", 1L * Integer.MAX_VALUE).existNode());
	}

	
	
	
	
}
