package net.ion.radon.repository;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;

public class TestApplyHandler extends TestBaseRepository {

	public void testDebugHandler() throws Exception {
		
		ApplyHander handler = new ApplyHander() {
			public Object handle(NodeCursor ac) {
				while(ac.hasNext()){
					Debug.debug(ac.next()) ;
				}
				return ac.count();
			}
		};

		createSample() ;
		String mapFunction = "function(){ emit(this._id, {self:this});}" ;
		String reduceFunction = "";
		String finalFunction = "function(key, reduced){var doc={} ; doc.key = key ; for(var i in reduced) { var inter = reduced.self; doc['id'] = inter._id ; }  return doc ; }";
		Object count = session.createQuery().apply(mapFunction, reduceFunction, finalFunction, CommandOption.BLANK, handler) ;
		assertEquals(2, count) ;
	}
	
	
	private void createSample() {
		session.newNode().put("name", "bleujin").put("address", "seoul").inlist("friend")
		.push(MapUtil.chainMap().put("name", "novision").put("age", 20)) 
		.push(MapUtil.chainMap().put("name", "pm1200").put("age", 30)) 
		.push(MapUtil.chainMap().put("name", "iihi").put("age", 25)) ;
		
		session.newNode().put("name", "hero").put("address", "seoul").inlist("friend")
		.push(MapUtil.chainMap().put("name", "baegi").put("age", 20)) 
		.push(MapUtil.chainMap().put("name", "minato").put("age", 25))
		.push(MapUtil.chainMap().put("name", "air").put("age", 30)) ;
		
		session.commit() ;
	}
		
}
