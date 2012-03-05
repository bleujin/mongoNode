package net.ion.radon.repository;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;

public class TestGetRegularExpression extends TestCase {

	public void testSimple() throws Exception {
		assertEquals("!abc", transRemainExpr("{abc}.bcd").getUId());
		assertEquals("bcd", transRemainExpr("{abc}.bcd").getProps());

		assertEquals(1, transRemainExpr("{111}.b.c.d").getUId());
		assertEquals("b.c.d", transRemainExpr("{111}.b.c.d").getProps());
		
		
		assertEquals(1, transRemainExpr("{1.b.c}.b.c.d").getUId());
		assertEquals("b.c.d", transRemainExpr("{1.b.c}.b.c.d").getProps());

		assertEquals("!2", transRemainExpr("{2.b.c}.b.c.d").getUId());
		assertEquals("b.c.d", transRemainExpr("{2.b.c}.b.c.d").getProps());

		assertEquals("abc", transRemainExpr("abc.bcd").getUId());
		assertEquals("bcd", transRemainExpr("abc.bcd").getProps());
	}
	
	public void testAdvance() throws Exception {
		assertEquals("!2_!2", transRemainExpr("{2.b.c}_{2.b.c}.b.c.d").getUId());
		assertEquals("b.c.d", transRemainExpr("{2.b.c}_{2.b.c}.b.c.d").getProps());

		assertEquals("1_!2", transRemainExpr("{1.b.c}_{2.b.c}.b.c.d").getUId());
		assertEquals("b.c.d", transRemainExpr("{1.b.c}_{2.b.c}.b.c.d").getProps());

		assertEquals("!d", transRemainExpr("{d}.{a}").getUId());
		assertEquals("!a", transRemainExpr("{d}.{a}").getProps());

		assertEquals(1, transRemainExpr("{1}.{a}").getUId());
		assertEquals("!a", transRemainExpr("{1}.{a}").getProps());
	}
	
	public void testAdv2() throws Exception {
		assertEquals("!d", transRemainExpr("{d.c}.{a}").getUId());
		assertEquals("!a", transRemainExpr("{d.c}.{a}").getProps());

		assertEquals("d", transRemainExpr("d.{a}").getUId());
		assertEquals("!a", transRemainExpr("d.{a}").getProps());
		
		
	}
	public void testPattern() throws Exception {
		Matcher m = remainPattern.matcher("{d.c}.{a}&{c}") ;
		m.find() ;
		assertEquals("{d.c}", m.group(1)) ;
		assertEquals("{a}&{c}", m.group(m.groupCount())) ;
	}

	public void testAdvPattern() throws Exception {
		Matcher m = remainPattern.matcher("{e.c}{d.c}.a.b") ;
		m.find() ;
		assertEquals("{e.c}{d.c}", m.group(1)) ;
		assertEquals("a.b", m.group(m.groupCount())) ;

		m = remainPattern.matcher("{e.c}_{d.c}.a.b") ;
		m.find() ;
		assertEquals("{e.c}_{d.c}", m.group(1)) ;
		assertEquals("a.b", m.group(m.groupCount())) ;

	}

	private static Pattern remainPattern = Pattern.compile("(([^.{}]*(\\{[a-zA-Z0-9_\\.]*\\})?)*)\\.([\\S]*)");
	private static Pattern p = Pattern.compile("\\{[a-zA-Z0-9_\\.]*\\}");

	private RemainResult transRemainExpr(String key) {
		if (!key.contains("{"))
			return new RemainResult(StringUtil.substringBefore(key, "."), StringUtil.substringAfter(key, "."));
		
		Matcher ma = remainPattern.matcher(key) ;
		if (! ma.find()) {
			throw new IllegalArgumentException("illegal expression : " + key) ;
		}
		
		Serializable uid = transUIDExpr(ma.group(1)) ;
		String propIds = transRegular(ma.group(ma.groupCount())) ;
		
		return new RemainResult(uid, propIds);
	}

	private Serializable transUIDExpr(String key) {
		Matcher m = p.matcher(key);
		StringBuffer sb = new StringBuffer();
		
		List<Serializable> foundValues = ListUtil.newList() ;
		while (m.find()) {
			Serializable value = get(StringUtil.substringBetween(m.group(), "{", "}"));
			foundValues.add(value) ;
			m.appendReplacement(sb, ObjectUtil.toString(value));
		}
		m.appendTail(sb);
		
		if (foundValues.size() == 0) {
			return key ;
		} else if (foundValues.size() == 1) {
			return foundValues.get(0) ;
		} else {
			return sb.toString() ;
		}
	}
	
	private String transRegular(String key) {
		if (!key.contains("{"))
			return key;

		Matcher m = p.matcher(key);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			Serializable value = get(StringUtil.substringBetween(m.group(), "{", "}"));
			m.appendReplacement(sb, ObjectUtil.toString(value));
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	

	private Serializable get(String str) {
		if (str.startsWith("1")) return Integer.parseInt(String.valueOf(str.charAt(0))) ;
		return "!" + StringUtil.defaultIfEmpty(StringUtil.substringBefore(str, "."), str)  ;
	}
}


