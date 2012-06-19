package net.ion.radon.repository.orm;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.MergeQuery;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.TempNode;
import net.ion.radon.repository.TestBaseRepository;
import net.ion.radon.repository.orm.bean.People;
import net.ion.radon.repository.orm.manager.PeopleManager;

public class TestPeople extends TestBaseRepository {

	private PeopleManager pm;
	private String wsname ;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		pm = new PeopleManager(session);
		pm.createQuery().remove();
		this.wsname = pm.getIDMethod().workspaceName() ;
	}

	public void testCurrentWorkspace() throws Exception {
		assertEquals(false, wsname.equals(session.getCurrentWorkspaceName()));
	}
	
	public void testCreate() throws Exception {
		People p = pm.createPeople("bleujin", 20, "seoul", "bleu");
		assertEquals(1, p.save().getRowCount());
		assertEquals(1, pm.createQuery().find().count()) ;
		pm.findById("bleujin") ;
	}

	
	public void testUpdate() throws Exception {
		People p = pm.createPeople("bleu", 20, "seoul", "bleu");
		p.save() ;

		People bleu = pm.findById("bleu");
		assertEquals(20, bleu.getAge()) ;
		bleu.setAge(25);
		NodeResult nr = bleu.save() ;

		assertEquals(25, bleu.getAge());
		
		People found = pm.findById("bleu");
		assertEquals(25, found.getAge());
	}
	
	public void testDelete() throws Exception {
		pm.createPeople("bleu", 20, "seoul", "white").save();
		assertEquals(1, pm.createQuery().find().count()) ;
		
		assertEquals(1, pm.createQuery().remove("bleu")) ;
		assertEquals(0, pm.createQuery().find().count()) ;
	}

	
	public void testList() throws Exception {
		pm.createQuery().remove() ;
		
		pm.createPeople("bleu", 20, "seoul", "white").save();
		pm.createPeople("jin", 25, "pusan", "red").save();
		pm.createPeople("hero", 30, "inchon", "blue").save();
		
		BeanCursor<People> bc = pm.createQuery().find() ;
		bc.debugPrint(PageBean.ALL) ;
		
		assertEquals(3, bc.count()) ;
	}

}