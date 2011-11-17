package net.ion.radon.repository.ics;

import net.ion.framework.util.Debug;
import net.ion.framework.util.express.ExpressUtils;
import net.ion.framework.util.express.PostfixExpress;
import net.ion.radon.repository.TestBaseRepository;

public class TestAradonQuery extends TestBaseRepository{

	
	public void xtestPostFix() throws Exception {
		String str = "aaa>2&&ff=3&&!(sfff=ff||sfff=2)&&vvv==xx&&(xx<33||vvv<=43)&&catid=vvvv";
		PostfixExpress pfe = ExpressUtils.toPostfixSearchExpress(str) ;

//		Debug.line(pfe.getExpressions()) ;
		Debug.debug(session.createQuery().aquery(str).getQuery()) ;
	}
	
	
	
	public void testExecQuery() throws Exception {
		session.newNode().put("name", "bleujin").put("age", 20).put("address", "seoul") ;
		session.commit() ;
		
		String str = "name='bleujin'&&age=20&&address='seoul'";
		
	//	Debug.line(session.createQuery().aquery(str)) ;
		assertEquals(1, session.createQuery().aquery(str).find().count()) ; 
		
	}
	
	
	public void testTypeConvert() throws Exception {
		session.newNode().put("type", "string").put("val", "30") ;
		session.commit() ;
		
		
		assertEquals(1, session.createQuery().eq("type", "string").lt("val", "4").count()) ; 
		assertEquals(1, session.createQuery().eq("type", "string").gt("val", "222").count()) ;
		
	}
	
	
	
}
