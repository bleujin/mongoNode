package net.ion.radon.repository.orm;

import java.lang.reflect.InvocationTargetException;

import net.ion.radon.repository.AbstractManager;
import net.ion.radon.repository.Session;

import org.apache.commons.beanutils.ConstructorUtils;

public class MaangerFactory {

	public static <T extends AbstractManager> T create(Session session, String wsname, Class<T> clz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		session.changeWorkspace(wsname) ;
		T result = clz.cast(ConstructorUtils.invokeConstructor(clz, new Object[0]));
		result.init(session, wsname) ;
		return result ;
	}

}
