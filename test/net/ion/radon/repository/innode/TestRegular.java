package net.ion.radon.repository.innode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;

public class TestRegular extends TestCase {

	public void testDevide() throws Exception {

		String pattern = "\\:([a-zA-Z0-9_\\.\\{\\}]*$)";
		String expr = "${context.1.connid}:{context.1.connid}.connnm.{ind}.0.idx";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(expr);
		while (m.find()) {
			Debug.line(m.group(0)) ;
			Debug.line(m.start(), StringUtil.substring(expr, 0, m.start()), StringUtil.substring(expr, m.start()+1));
		}
	}
}
