package net.ion.radon.repository;

import java.util.Date;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;

public class TestExtendNode extends TestBaseRepository{

	public void xsetUp() throws Exception {
		session.newNode().put("name", "bleujin").put("age", 20).inner("address").put("city", "seoul").put("street", 32) ;
		session.newNode().put("name", "hero").put("age", 15).inner("address").put("city", "seoul").put("street", 32) ;
		session.commit() ;
		
		session.createQuery().eq("address.city", "seoul").ascending("age", "name").find().debugPrint(PageBean.ALL) ;
	}
	
	
	public void testFind() throws Exception {
		Node article = session.newNode().setAradonId("artid", 300).put("artid", 300).put("subject", "1234") ; ;
		article.inlist("comments").push(MapUtil.<String, Object>chainMap().put("index", 0).put("reguser", "bleujin").put("regDate", new Date()).toMap()) ;
		article.inlist("comments").push(MapUtil.<String, Object>chainMap().put("index", 1).put("reguser", "hero").put("regDate", new Date()).toMap()) ;
		article.inlist("comments").push(MapUtil.<String, Object>chainMap().put("index", 2).put("reguser", "111").put("regDate", new Date()).toMap()) ;
		article.inlist("comments").push(MapUtil.<String, Object>chainMap().put("index", 3).put("reguser", "111").put("regDate", new Date()).toMap()) ;
		article.inlist("comments").push(MapUtil.<String, Object>chainMap().put("index", 4).put("reguser", "333").put("regDate", new Date()).toMap()) ;
		
		session.commit() ;
		
		Node findOne = session.createQuery().aradonGroupId("artid", 300).findOne() ;
		Debug.debug(findOne.inlist("comments").createQuery().between("index", 3, 4).find()) ;
		
		Debug.debug(findOne.getIdentifier(), session.createQuery().id(findOne.getIdentifier())) ;
	}
	
	public void testName() throws Exception {
		Node bleujin = session.newNode("bleujin").put("name", "bleujin") ;
		bleujin.createChild("abc").put("name", "abc") ;
		
		session.commit() ;
		Debug.debug(session.createQuery().path("bleujin").findOne().getChild().limit(10).toList(PageBean.ALL) ) ;
	}
	
}
