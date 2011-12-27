package net.ion.radon.repository.mr;

import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.NodeConstants;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.TestBaseRepository;

public class TestGroup extends TestBaseRepository {


	public void testInListSort() throws Exception {
		createSample();
		
		PropertyFamily initial = PropertyFamily.create().put("sortkey", "name").put("skip", 0).put("limit", 2);
		String compareFn = "var comfn = function(f1, f2){ var order = 1 ;  if (f1[out.sortkey] > f2[out.sortkey]) return 1 * order ; else if(f1[out.sortkey] < f2[out.sortkey]) return -1 * order; else return 0 }" ;
		String reduce = "function(doc, out){ " + compareFn + ";  out.friends = Array.prototype.slice.call(Array.prototype.sort.call(doc.friend, comfn), out.skip, out.limit); }";

		PropertyFamily keys = PropertyFamily.create().put(NodeConstants.ID, true) ;
		NodeCursor nc = session.createQuery().eq("address", "seoul").group(keys, initial, reduce) ;
		assertEquals(2, nc.count()) ;
		nc.debugPrint(PageBean.ALL) ;
		
	}
	
	public void testGroupCount() throws Exception {
		createSample() ;
		
		PropertyFamily initial = PropertyFamily.create().put("count", 0);
		String reduce = "function(doc, out){ out.count++; }";
		NodeCursor nc = session.createQuery().group(PropertyFamily.create("address", true), initial, reduce) ;
		nc.debugPrint(PageBean.ALL) ;
	}
	
	
	private void createSample() {
		session.newNode().put("name", "bleujin").put("address", "seoul").inlist("friend")
		.push(MapUtil.chainKeyMap().put("name", "novision").put("age", 20)) 
		.push(MapUtil.chainKeyMap().put("name", "iihi").put("age", 25))
		.push(MapUtil.chainKeyMap().put("name", "pm1200").put("age", 30)) ;
		
		session.newNode().put("name", "hero").put("address", "seoul").inlist("friend")
		.push(MapUtil.chainKeyMap().put("name", "baegi").put("age", 20)) 
		.push(MapUtil.chainKeyMap().put("name", "minato").put("age", 25))
		.push(MapUtil.chainKeyMap().put("name", "air").put("age", 30)) ;

		session.commit() ;
	}
}
