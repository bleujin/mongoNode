package net.ion.radon.repository;

import org.apache.commons.lang.ArrayUtils;

public class TestAdvanceNodeGet extends TestBaseRepository{

	public void testBlankGet() throws Exception {
		Node node = session.newNode().put("name", "bleujin") ;
		assertEquals(true, node.get("") == null) ;
	}
//		path expr  !/nation.ncode.{nation} or !wsname:/nation.ncode.{nation} 
//		aid  expr   $config:nation.ncode.{nation} or $wsname:config:nation.ncode.{nation}

	public void testByPath() throws Exception {
		session.newNode("nation").setAradonId("config", "nation").put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent() ;
		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor") ;
		session.commit() ;
		
		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne() ;
		assertEquals("korea", found.get("!/nation.{nation}")) ;
	}
	
	
	public void testPathWorkspace() throws Exception {
		Node nation = session.newNode("nation").setAradonId("config", "nation").put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent() ;
		session.changeWorkspace("bleujin") ;
		session.dropWorkspace() ;
		
		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor").put("wsname", nation.getWorkspaceName()) ;
		session.commit() ;
		
		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne() ;
		assertEquals("korea", found.get("!{wsname}:/nation.{nation}")) ;
	}
	
	public void testPathWorkspace2() throws Exception {
		Node nation = session.newNode("nation").setAradonId("config", "nation").put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent() ;
		session.changeWorkspace("bleujin") ;
		session.dropWorkspace() ;
		
		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor").put("path", "/nation").put("wsname", nation.getWorkspaceName()) ;
		session.commit() ;
		
		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne() ;
		assertEquals("korea", found.get("!{wsname}:{path}.{nation}")) ;
	}
	
	
	public void testByAradonId() throws Exception {
		Node nation = session.newNode("nation").setAradonId("config", "nation").put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent() ;
		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor").put("path", "/nation").put("wsname", nation.getWorkspaceName()) ;
		session.commit() ;
		
		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne() ;
		assertEquals("korea", found.get("$config:nation.{nation}")) ;
	}
	
	public void testByAradonIdWorkspace() throws Exception {
		Node nation = session.newNode("nation").setAradonId("config", "nation").put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent() ;
		session.changeWorkspace("bleujin") ;
		session.dropWorkspace() ;
		
		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor").put("path", "/nation").put("wsname", nation.getWorkspaceName()) ;
		session.commit() ;
		
		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne() ;
		assertEquals("korea", found.get("$abcd:config:nation.{nation}")) ;
	}
	
	public void testByAradonIdWorkspace2() throws Exception {
		Node nation = session.newNode("nation").setAradonId("config", "nation").put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent() ;
		session.changeWorkspace("bleujin") ;
		session.dropWorkspace() ;
		
		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor").put("aid", "config:nation").put("wsname", nation.getWorkspaceName()) ;
		session.commit() ;
		
		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne() ;
		assertEquals("korea", found.get("${wsname}:{aid}.{nation}")) ;
	}
	
	public void testByAradonIdNumeric() throws Exception {
		Node nation = session.newNode("nation").setAradonId("config", 3).put("kr", "korea").put("en", "usa").inner("my").put("num", 3).getParent() ;
		session.changeWorkspace("bleujin") ;
		session.dropWorkspace() ;
		
		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kr").put("lang", "kor").put("agroup", "config").put("auid", 3).put("wsname", nation.getWorkspaceName()) ;
		session.commit() ;
		
		Node found = session.createQuery().aradonGroupId("emp", "bleujin").findOne() ;
		assertEquals("korea", found.get("${wsname}:{agroup}:{auid}.{nation}")) ;
	}
	
	
	
	
}
