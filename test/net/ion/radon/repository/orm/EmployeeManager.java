package net.ion.radon.repository.orm;

import java.util.List;

import net.ion.radon.core.PageBean;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.Session;

@IDMethod(workspaceName="peoples", groupId="employee", keyPropId="empNo", managerClz=Employee.class)
public class EmployeeManager<T extends AbstractORM> extends AbstractManager<T> {

	public EmployeeManager(Session session) {
		super(session) ;
	}

	public List<T> findByAddress(String address) {
		return find(PropertyQuery.create().eq("address", address)).toList(PageBean.TEN);
	}


}