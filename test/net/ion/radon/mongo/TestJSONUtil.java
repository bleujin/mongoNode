package net.ion.radon.mongo;

import java.util.Date;

import com.mongodb.DBObject;

import net.ion.framework.util.Debug;
import net.ion.radon.repository.InnerNodeImpl;
import net.ion.radon.repository.NodeObject;
import net.ion.radon.repository.util.JSONUtil;
import net.sf.json.JSONObject;
import junit.framework.TestCase;

public class TestJSONUtil extends TestCase {

	public void testConvert() throws Exception {
		JSONObject json = JSONObject .fromObject("{name:'bleujin',1:2, address:{city:'seoul',street:[1, 2, 3], col:{val:'A'}},color:['red','blue','white']}");
		json.put("date", new Date());

		DBObject dbo = JSONUtil.toDBObject(json);
		NodeObject no = NodeObject.load(dbo);

		assertEquals("bleujin", no.getString("name"));
		assertEquals("seoul", no.getString("address.city"));
		assertEquals("A", no.getString("address.col.val"));

		Debug.debug("A", no.get("color"), no.get("color").getClass());
	}

}
