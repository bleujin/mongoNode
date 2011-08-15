package net.ion.radon.repository;

import java.io.File;
import java.util.Map;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.sf.json.JSONObject;

public class TestTempNode extends TestBaseRepository{


	
	public void testOtherSelect() throws Exception {
		Node bleujin = session.newNode("bleujin") ;
		bleujin.put("id", "bleujin") ;
		bleujin.put("greeting", "hello") ;
		bleujin.put("loc", "seoul") ;

		session.commit() ;
		
		JSONObject jso = JSONObject.fromObject("{age:20, name:'hello', greeting:'hi!'}") ;
		session.mergePath("/bleujin", jso) ;
		
		session.commit() ;
		session.createQuery().find().debugPrint(PageBean.ALL) ;
	}
	
	
	
	public void testLastModified() throws Exception {
		assertEquals(0, session.getRoot().getLastModified()) ;

		Node bleujin = session.newNode("bleujin") ;
		long created = bleujin.getLastModified() ;
		
		session.commit() ;
		Thread.sleep(1000) ;
		
		bleujin.put("new", "value") ;
		session.commit();
		long modified = bleujin.getLastModified() ;
		
		
		assertEquals(true, modified > 900 + created) ;
	}
	
	
	public void testChild() throws Exception {
		Node dept = session.newNode("dept") ;
		dept.createChild("dev").put("key", "val") ;
		
		session.commit() ;
		
		Node found = session.createQuery().findByPath("/dept") ;
		
		found.getChild().debugPrint(PageBean.ALL) ;
		found.getChild().debugPrint(PageBean.ALL) ;
	}
	
}
