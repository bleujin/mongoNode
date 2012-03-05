package net.ion.radon.repository.innode;

import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.TestBaseRepository;

public class TestInListQueryNode extends TestBaseRepository{


	public void testDepth() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.commit() ;
		session.createQuery().inlist("person").push(JsonParser.fromString("{name:'bleujin',1:2, address:{city:'seoul',street:[1, 2, 3], col:{val:'A'}},color:['red','blue','white']}").getAsJsonObject().toMap()) ;
		
		session.createQuery().find().debugPrint(PageBean.ALL) ;
		Node node = session.createQuery().findOne() ;
		assertEquals("bleujin", node.inlist("person").createQuery().findOne().get("name")) ;
		assertEquals("seoul", node.inlist("person").createQuery().findOne().get("address.city")) ;
	}

	public void testCaseInSensitive() throws Exception {
		session.newNode().put("name", "bleujin") ;
		session.newNode().put("name", "hero");
		session.commit() ;
		
		session.createQuery().inlist("Greeting").push(MapUtil.create("ENG", "hello")) ;

		assertEquals(1, session.createQuery().eq("name", "bleujin").findOne().inlist("greeting").createQuery().find().size()) ;
		assertEquals(1, session.createQuery().eq("name", "bleujin").findOne().inlist("Greeting").createQuery().find().size()) ;
		Debug.debug(session.createQuery().eq("name", "bleujin").findOne().inlist("Greeting").createQuery().findOne()) ;
		assertEquals("hello", session.createQuery().eq("name", "bleujin").findOne().inlist("Greeting").createQuery().findOne().getString("Eng")) ;
	}
	
	
	
	public void testEleMatch() throws Exception {
		createSample();
		NodeCursor nc = session.createQuery().inlist("friend").findElement(PropertyQuery.create().gte("age", 40));
		
		assertEquals(1, nc.count()) ;
		Node found = nc.next()  ;
		assertEquals("hero", found.getString("name")) ;
		
		Node f2 = session.createQuery().eleMatch("friend", PropertyQuery.create().gte("age", 40)).findOne();
		assertEquals("hero", f2.getString("name")) ;
	}
	
	
	private void createSample() {
		Node bleujin = session.newNode().put("name", "bleujin") ;
		bleujin.inlist("friend").push(MapUtil.chainKeyMap().put("name", "novision").put("age", 20)) ;
		bleujin.inlist("friend").push(MapUtil.chainKeyMap().put("name", "iihi").put("age", 30)) ;
		
		Node hero = session.newNode().put("name", "hero") ;
		hero.inlist("friend").push(MapUtil.chainMap().put("name", "pm1200").put("age", 40)) ;
		
		Node jin = session.newNode().put("name", "jin") ;
		session.commit() ;
	}
}
