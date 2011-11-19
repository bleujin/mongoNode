package net.ion.radon.repository.orm;

import net.ion.radon.repository.Columns;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.TestBaseRepository;

public class TestPeople extends TestBaseRepository{


	private PeopleManager<People> pm ;
	private String wsname = "peoples";
	@Override protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		pm = ManagerFactory.create(session, wsname, PeopleManager.class) ;
		pm.drop() ;
	}
	
	public void testCurrentWorkspace() throws Exception {
		assertEquals(false, wsname.equals(session.getCurrentWorkspaceName())) ;
	}
	
	public void testCreate() throws Exception {
		People p = new People("bleujin") ;
		p.setAge(20) ;
		p.setAddress("seoul") ;
		p.setFavoriateColor("bleu") ;
		NodeResult nr = pm.save(p) ;
		
		assertEquals(1, nr.getRowCount()) ;
		assertEquals(1, session.getWorkspace(wsname).find(session, PropertyQuery.create(), Columns.ALL).count()) ;
		Node found = session.getWorkspace(wsname).findOne(session, PropertyQuery.create(), Columns.ALL) ;
		
		assertEquals("bleujin", found.get("userId")) ;
		assertEquals(20, found.get("age")) ;
		assertEquals("seoul", found.get("address")) ;
		assertEquals("bleu", found.get("fcolor")) ;
	}
	
	
	public void testUpdate() throws Exception {
		pm.save(People.create("bleu", 20, "seoul", "white")) ;
		
		People bleu = pm.findById(new People("bleu")) ;
		bleu.setAge(25) ;
		NodeResult nr = pm.save(bleu) ;
		
		assertEquals(25, bleu.getAge()) ;

		People found = pm.findById(new People("bleu")) ;
		assertEquals(25, found.getAge()) ;
	}
	
	public void testDelete() throws Exception {
		pm.save(People.create("bleu", 20, "seoul", "white")) ;
	}
	
	

}
