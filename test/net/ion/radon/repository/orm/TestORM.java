package net.ion.radon.repository.orm;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.TestBaseRepository;

public class TestORM extends TestBaseRepository{

	public void testUse() throws Exception {
		PeopleManager<People> pm = MaangerFactory.create(session, "myworkspace", PeopleManager.class) ;
		session.getCurrentWorkspace().drop() ;
		
		
		initPerson(pm) ;
		
		People bleu = pm.findById(new People("bleu")) ;
		assertEquals("bleu", bleu.getId()) ;
		assertEquals(20, bleu.getAge()) ;
		assertEquals("seoul", bleu.getAddress()) ;
		assertEquals("white", bleu.getFavoriateColor()) ;
		

		Node node = pm.toNode(bleu) ;
		assertEquals("bleu", node.getName()) ; 
		assertEquals("bleu", node.get("userId")) ; 
		assertEquals("seoul", node.get("address")) ; 
		assertEquals("white", node.get("fcolor")) ; 
		assertEquals(20, node.get("age")) ; 
		

		List<People> peoples = pm.findByAddress("seoul") ;
		for (People peo : peoples) {
			Debug.debug(peo) ;
		}
		
		
		Debug.line() ;
		List<People> busanPeople = pm.createQuery().eq("address", "busan").gt("age", 20).ascending("age").descending("userId").find().toList(PageBean.create(2, 1), People.class) ;
		for (People peo : busanPeople) {
			Debug.debug(peo) ;
		}
		
	}
	
	
	private void initPerson(PeopleManager<People> pm) {
		pm.save(People.create("bleu", 20, "seoul", "white")) ;
		pm.save(People.create("hero", 30, "busan", "black")) ;
		pm.save(People.create("jin", 40, "busan", "red")) ;
		pm.save(People.create("bee", 29, "busan", "red")) ;
	}
	
	
	
	
	
	
}
