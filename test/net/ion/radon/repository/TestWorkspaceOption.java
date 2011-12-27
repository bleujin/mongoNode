package net.ion.radon.repository;

import com.mongodb.MapReduceCommand.OutputType;

import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;

public class TestWorkspaceOption extends TestBaseRepository {

	
	public void testWithOption() throws Exception {
		session.changeWorkspace("mywork") ;
		session.dropWorkspace() ;
		session.changeWorkspace("mywork", WorkspaceOption.NONE) ;
		
		session.newNode().setAradonId("emp", "bleujin") ;
		session.newNode().setAradonId("emp", "hero") ;
		
		session.commit() ;
		assertEquals(2, session.createQuery().find().count()) ;
		assertEquals(1, session.getCurrentWorkspace().getIndexInfo().size()) ;
		
		assertEquals(2, session.createQuery("mywork", WorkspaceOption.NONE).find().count()) ;
	}
	

	
	public void testMapReduce() throws Exception {
		String taraget = "mytestoutput";
		session.changeWorkspace(taraget, WorkspaceOption.NONE).dropWorkspace() ;
		session.changeWorkspace("abcd") ;
		createSample();
		
		String mapFunction = "function(){ emit(this.name, {self:this});}" ;
		String reduceFunction = "function(key, doc){var doc={}; return doc ;}" ;
		
		NodeCursor nc = session.createQuery().mapreduce(mapFunction, "", "", CommandOption.create(OutputType.MERGE, taraget)) ;
		assertEquals(2, nc.toList(PageBean.ALL).size()) ;
		session.changeWorkspace(taraget).createQuery().find().debugPrint(PageBean.ALL) ;
	}
	
	
	private void createSample() {
		session.newNode().put("name", "bleujin").put("address", "seoul").inlist("friend")
		.push(MapUtil.chainKeyMap().put("name", "novision").put("age", 20)) 
		.push(MapUtil.chainKeyMap().put("name", "pm1200").put("age", 30)) 
		.push(MapUtil.chainKeyMap().put("name", "iihi").put("age", 25)) ;
		
		session.newNode().put("name", "hero").put("address", "seoul").inlist("friend")
		.push(MapUtil.chainKeyMap().put("name", "baegi").put("age", 20)) 
		.push(MapUtil.chainKeyMap().put("name", "minato").put("age", 25))
		.push(MapUtil.chainKeyMap().put("name", "air").put("age", 30)) ;
		
		session.commit() ;
		
		assertEquals(2, session.createQuery().eq("address", "seoul").find().count()) ;
	}
}
