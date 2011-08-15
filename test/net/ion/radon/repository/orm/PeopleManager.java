package net.ion.radon.repository.orm;

import java.util.List;

import net.ion.radon.core.PageBean;
import net.ion.radon.repository.AbstractManager;

public class PeopleManager<T extends AbstractORM> extends AbstractManager<T> {

	public List<People> findByAddress(String address) {
		return super.createQuery().eq("address", address).gt("age", 19).find().toList(PageBean.TEN, People.class);
	}

}
