package net.ion.radon.repository.vfs;

import java.util.Date;
import java.util.List;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.TestBaseRepository;

public class TestJSONToNode extends TestBaseRepository {

	
	public void testNode() throws Exception {
		
		session.newNode().put("name", "bleu").put("date", new Date()).inner("loc").put("x", 3).put("y", 2) ;
		session.newNode().put("name", "jin").put("date", 3).inner("loc").put("x", 4).put("y", 6) ;
		
		session.newNode().put("name", "hero").inner("loc").put("x", 3).put("y", 4).inner("street").put("juso", "yuksam") ;
		
		session.commit() ;
		session.createQuery().gt("loc.x", 2).descending("loc.y").find().debugPrint(PageBean.ALL) ;
	}
	
	
	public void testInner() throws Exception {
		Node node = session.newNode().put("name", "bleujin") ;
		node.put("loc", "kangnam") ;
		
		assertEquals("kangnam", node.getString("loc")) ;
		
		node.inner("loc").put("x", 3) ;
		node.inner("loc").put("y", 3) ;
		

		
		assertEquals(3, node.get("loc.x")) ;
		assertEquals(3, node.inner("loc").get("x")) ;
	}
	
	
	public void testJSONToNodeSimple() throws Exception {
		JsonObject jso = JsonParser.fromString("{ string:'Hello', int:3, boolean:true, double:3.4, list:[3, 2, 1], loc:{a:'3', b:3}}").getAsJsonObject();
		Node node = toNode(jso);

		assertEquals("Hello", node.getString("string"));
		assertEquals(3L, node.get("int"));
		assertEquals(true, node.get("boolean"));
		assertEquals(3.4D, node.get("double"));
		assertEquals(ListUtil.toList(3, 2, 1).toString(), node.get("list").toString());
		
		assertEquals("3", node.getString("loc.a")) ;
	}
	
	
	public void testComplicateNode() throws Exception {
		JsonObject jso = JsonParser.fromString("{ string:'Hello', int:3, boolean:true, double:3.4, array:['2',3], where:[{x:3,y:4,street:'non'}, {street:'gangnam'}, {juso:{name:3}}]}").getAsJsonObject();
		Node node = toNode(jso);

		assertEquals(true, node.get("array") instanceof List) ;
		assertEquals(2, ((List)node.get("array")).size()) ;
		
		Debug.debug(node.get("where")) ;
		Debug.debug(node.get("where.x"), node.get("where.y")) ;
		
		assertEquals(3, node.get("array", 1)) ;
		assertEquals("2", node.get("array", 0)) ;
		
		assertEquals(3, node.get("where.x")) ;
		assertEquals(4, node.get("where.y")) ;
		assertEquals(3, node.get("where.juso.name")) ;
		
		
	}
	
	

	private Node toNode(JsonObject jso) {
		Node node = session.newNode();

		node.putAll(jso.toMap()) ;
		return node;
	}
}
