package net.ion.radon.repository.mr;

import junit.framework.TestCase;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.CommandOption;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;

import com.mongodb.MapReduceCommand.OutputType;

public class TestOutputType extends TestCase{

	private Session session ;
	@Override protected void setUp() throws Exception {
		super.setUp();
		RepositoryCentral rc = RepositoryCentral.testCreate() ;
		session= rc.testLogin("testoutputtype") ;
		session.dropWorkspace() ;
		
		// session.changeWorkspace("myjob").dropWorkspace() ;
		createSample() ;
	}
	
	public void testDebugHandler() throws Exception {
		String mapFunction = "function(){ var parent = this ; this.friend.forEach(function(p) {  emit(p.name, {self:p, count:1}); } );}" ;
		String reduceFunction = "function(key, values){var doc={} ; var count = 0 ; doc.key = key ; values.forEach(function(val){ count += val.count; }) ; doc['count'] = count; return doc ; }";
		String finalFunction = "function(key, value){var doc={}; doc['fname'] = key ; doc['count'] = value.count;  return doc }";
		
		NodeCursor nc = session.createQuery().mapreduce(mapFunction, reduceFunction, finalFunction, CommandOption.create(OutputType.REPLACE, "myjob")) ;
		nc.debugPrint(PageBean.ALL) ;
	}

	
	private void createSample() {
		session.newNode().put("name", RandomUtil.nextRandomString(8)).put("address", "seoul").inlist("friend")
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
