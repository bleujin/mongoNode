package net.ion.radon.repository;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;

public class TestApplyHandler extends TestBaseRepository {

	public void testDebugHandler() throws Exception {
		
		ApplyHander handler = new ApplyHander() {
			public Object handle(NodeCursor ac) {
				int count = 0 ;
				while(ac.hasNext()){
					Debug.debug(ac.next()) ;
					count++ ;
				}
				return count;
			}
		};

		createSample() ;
		String mapFunction = "function(){ var parent = this ; this.friend.forEach(function(p) {  emit(p.name, {self:p, pname:parent.name}); } );}" ;
		String reduceFunction = "function(key, values){var doc={} ; var pnames = []; var index = 0 ; doc.key = key ; values.forEach(function(val){ doc['self'] = val.self ;  pnames[index++] = val.pname}) ; doc['pname'] = pnames; return doc ; }";
		String finalFunction = "function(key, value){var doc={}; doc['key'] = key; doc['self'] = value.self; doc['pname'] = value.pname;  return doc }";
		Object count = session.createQuery().apply(mapFunction, reduceFunction, finalFunction, CommandOption.BLANK, handler) ;
		assertEquals(4, count) ;
	}
	
	
	private void createSample() {
		session.newNode().put("name", "bleujin").put("address", "seoul").inlist("friend")
		.push(MapUtil.chainKeyMap().put("name", "novision").put("age", 20)) 
		.push(MapUtil.chainKeyMap().put("name", "pm1200").put("age", 30)) 
		.push(MapUtil.chainKeyMap().put("name", "iihi").put("age", 25)) ;
		
		session.newNode().put("name", "hero").put("address", "seoul").inlist("friend")
		.push(MapUtil.chainKeyMap().put("name", "iihi").put("age", 25)) 
		.push(MapUtil.chainKeyMap().put("name", "minato").put("age", 25))
		.push(MapUtil.chainKeyMap().put("name", "pm1200").put("age", 30)) ;
		
		session.commit() ;
	}
		
}
