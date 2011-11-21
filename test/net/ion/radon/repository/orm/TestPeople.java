package net.ion.radon.repository.orm;

import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.TestBaseRepository;

public class TestPeople extends TestBaseRepository {

	private PeopleManager<People> pm;
	private String wsname ;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		pm = new PeopleManager<People>(session);
		pm.removeAll();
		this.wsname = PeopleManager.class.getAnnotation(IDMethod.class).workspaceName() ;
	}

	public void testCurrentWorkspace() throws Exception {
		assertEquals(false, wsname.equals(session.getCurrentWorkspaceName()));
	}
	
	public void testCreate() throws Exception {
		People p = People.create("bleujin", 20, "seoul", "bleu");
		assertEquals(1, pm.save(p));

		assertEquals(1, pm.find(PropertyQuery.create()).count()) ;
		
		pm.findById("bleujin") ;
		
	}

	public void testUpdate() throws Exception {
		pm.save(People.create("bleu", 20, "seoul", "white"));

		People bleu = pm.findById("bleu");
		bleu.setAge(25);
		pm.save(bleu);

		assertEquals(25, bleu.getAge());

		People found = pm.findById("bleu");
		assertEquals(25, found.getAge());
	}

	
	public void testDelete() throws Exception {
		pm.save(People.create("bleu", 20, "seoul", "white"));
		assertEquals(1, pm.find(PropertyQuery.create()).count()) ;
		
		assertEquals(1, pm.remove("bleu")) ;
		assertEquals(0, pm.find(PropertyQuery.create()).count()) ;
	}

	
	public void testList() throws Exception {
		pm.save(People.create("bleu", 20, "seoul", "white"));
		pm.save(People.create("jin", 25, "pusan", "red"));
		pm.save(People.create("hero", 30, "inchon", "blue"));
		
		BeanCursor<People> bc = pm.find(PropertyQuery.create()) ;
		assertEquals(3, bc.count()) ;
	}
	
	
}