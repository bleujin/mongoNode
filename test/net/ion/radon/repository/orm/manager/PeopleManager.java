package net.ion.radon.repository.orm.manager;

import java.util.List;

import net.ion.radon.core.PageBean;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.orm.GenericManager;
import net.ion.radon.repository.orm.bean.People;

public class PeopleManager extends GenericManager<People> {

	public PeopleManager(Session session) {
		super(session) ;
	}

	public List<People> findByAddress(String address) {
		return createQuery().eq("address", address).toList(PageBean.TEN);
	}
	
	
	public People createPeople( String id, int age, String address, String favcolor) {
		People newPeople = loadInstance(id) ;
		newPeople.setUserId(id) ;
		newPeople.setAge(age) ;
		newPeople.setAddress(address) ;
		newPeople.setFavoriateColor(favcolor) ;
		
		return newPeople ;
	}
}
