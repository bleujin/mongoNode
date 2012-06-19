package net.ion.radon.repository;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;

public class TestGetRelation extends TestBaseRepository {

	public void testRegular() throws Exception {

		String str = "hi {name} {ab.bc} hm..";
		Map<String, ? extends Object> vals = MapUtil.chainKeyMap().put("name", "bleujin").put("greeting", "hi").put("ab.bc", "...").toMap();

		Pattern p = Pattern.compile("\\{[a-zA-Z][a-zA-Z0-9.]*\\}");
		Matcher m = p.matcher(str);

		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, ObjectUtil.toString(vals.get(StringUtil.substringBetween(m.group(), "{", "}"))));
		}
		m.appendTail(sb);
		Debug.line(sb) ;
	}

	public void testGetValuable() throws Exception {
		Node code = session.newNode().setAradonId("config", "code").inner("ncode").put("kor", "korean").put("eng", 4).getParent() ;
		
		Node bleujin = session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin").put("nation", "kor").toRelation("nation", code.selfRef()).inner("code").put("ncode", "kor").getParent() ;
		Node hero = session.newNode().setAradonId("emp", "hero").put("name", "hero").put("nation", "eng").toRelation("nation", code.selfRef()) ;
		
		session.commit() ;
		
		assertEquals("kor", bleujin.get("nation")) ;
		assertEquals("korean", bleujin.get("#nation.ncode.kor")) ;
		assertEquals("korean", bleujin.get("#nation.ncode.{nation}")) ;
		assertEquals("korean", bleujin.get("#nation.ncode.{code.ncode}")) ;
		
		assertEquals(4, hero.get("#nation.ncode.{nation}")) ;
	}
}




