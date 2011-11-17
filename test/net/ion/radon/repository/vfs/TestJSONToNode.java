package net.ion.radon.repository.vfs;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.INode;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.TestBaseRepository;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
		JSONObject jso = JSONObject.fromObject("{ string:'Hello', int:3, boolean:true, double:3.4, list:[3, 2, 1], loc:{a:'3', b:3}}");
		Node node = toNode(jso);

		assertEquals("Hello", node.getString("string"));
		assertEquals(3, node.get("int"));
		assertEquals(true, node.get("boolean"));
		assertEquals(3.4D, node.get("double"));
		assertEquals(ListUtil.toList(3, 2, 1).toString(), node.get("list").toString());

		assertEquals("3", node.getString("loc.a")) ;
	}
	
	
	public void testComplicateNode() throws Exception {
		JSONObject jso = JSONObject.fromObject("{ string:'Hello', int:3, boolean:true, double:3.4, array:['2',3], where:[{x:3,y:4,street:'non'}, {street:'gangnam'}, {juso:{name:3}}]}");
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
	
	

	private Node toNode(JSONObject jso) {
		Node node = session.newNode();

		Iterator<String> kiter = jso.keys();
		while (kiter.hasNext()) {
			String key = kiter.next() ;
			recursiveSave(node, key, jso.get(key)) ;
		}
		return node;
	}
	
	private void recursiveSave(INode node, String key, Object value){
		if (value instanceof JSONArray) {
			JSONArray jsa = (JSONArray)value ;
			Iterator iter = jsa.iterator() ;
			while (iter.hasNext()) {
				recursiveSave(node, key, iter.next()) ;
			}
		} else if (value instanceof JSONObject) {
			InNode inner = node.inner(key) ;
			JSONObject jso = (JSONObject)value ;
			Iterator<String> kiter = jso.keys();
			while (kiter.hasNext()) {
				String ikey = kiter.next() ;
				recursiveSave(inner, ikey, jso.get(ikey)) ;
			}
		} else {
			node.append(key, value);
		}
	}
	
	
	
	
	
	
	
	
	
	
	

	public static String getPretty(String jsonString) {

		final String INDENT = "    ";
		StringBuffer prettyJsonSb = new StringBuffer();

		int indentDepth = 0;
		String targetString = null;
		for (int i = 0; i < jsonString.length(); i++) {
			targetString = jsonString.substring(i, i + 1);
			if (targetString.equals("{") || targetString.equals("[")) {
				prettyJsonSb.append(targetString).append("\n");
				indentDepth++;
				for (int j = 0; j < indentDepth; j++) {
					prettyJsonSb.append(INDENT);
				}
			} else if (targetString.equals("}") || targetString.equals("]")) {
				prettyJsonSb.append("\n");
				indentDepth--;
				for (int j = 0; j < indentDepth; j++) {
					prettyJsonSb.append(INDENT);
				}
				prettyJsonSb.append(targetString);
			} else if (targetString.equals(",")) {
				prettyJsonSb.append(targetString);
				prettyJsonSb.append("\n");
				for (int j = 0; j < indentDepth; j++) {
					prettyJsonSb.append(INDENT);
				}
			} else {
				prettyJsonSb.append(targetString);
			}

		}

		return prettyJsonSb.toString();

	}
}
