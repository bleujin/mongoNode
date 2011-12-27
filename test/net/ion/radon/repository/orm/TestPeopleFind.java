package net.ion.radon.repository.orm;

import java.lang.reflect.Constructor;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.TestBaseRepository;
import net.ion.radon.repository.orm.bean.People;
import net.ion.radon.repository.orm.manager.PeopleManager;

import org.apache.commons.beanutils.ConstructorUtils;

public class TestPeopleFind extends TestBaseRepository {

	private PeopleManager pm;

	@Override protected void setUp() throws Exception {
		super.setUp();
		session.changeWorkspace("peoples");
		session.dropWorkspace();
		pm = new PeopleManager(session);
		pm.createQuery().remove() ;
	}

	public void xtestConstructor() throws Exception {
		Constructor[] cons = People.class.getConstructors() ;
		for (Constructor con : cons) {
			Class[] paramclz = con.getParameterTypes() ;
			for (Class clz : paramclz) {
				
			}
		}
		boolean acce = ConstructorUtils.getAccessibleConstructor(People.class, new Class[0]) != null;
		assertEquals(true, acce) ;
	}
	
	public void testToNode() throws Exception {
		pm.createPeople("bleu", 20, "seoul", "white").save();
		pm.createPeople("hero", 30, "busan", "black").save();
		pm.createPeople("jin", 40, "busan", "red").save();
		pm.createPeople("bee", 29, "busan", "red").save();

		People bleu = pm.findById("bleu");
		assertEquals("bleu", bleu.getId());
		assertEquals(20, bleu.getAge());
		assertEquals("seoul", bleu.getAddress());
		assertEquals("white", bleu.getFavoriateColor());

		List<People> peoples = pm.findByAddress("seoul");
		assertEquals(1, peoples.size());
		People f = peoples.get(0);

		assertEquals("bleu", f.getId());
		assertEquals(20, f.getAge());

		List<People> busanPeople = pm.createQuery().eq("address", "busan").gt("age", 20).ascending("age").descending("userId").toList(PageBean.create(2, 1));
		for (People peo : busanPeople) {
			Debug.debug(peo);
		}
	}
}