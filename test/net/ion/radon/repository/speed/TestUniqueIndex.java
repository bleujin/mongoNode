package net.ion.radon.repository.speed;

import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.TestBaseRepository;
import junit.framework.TestCase;


public class TestUniqueIndex extends TestBaseRepository{

	public void testAradonId() throws Exception {
		session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin") ;
		session.newNode().setAradonId("emp", "bleujin").put("name", "hero") ;
		session.commit() ;
		
		NodeResult nr = session.getAttribute(NodeResult.class.getCanonicalName(), NodeResult.class) ;
		assertEquals(true, StringUtil.isNotBlank(nr.getErrorMessage())) ;
		assertEquals(1, session.createQuery().find().count()) ;
	}
	
	
	
	
	
	
}
