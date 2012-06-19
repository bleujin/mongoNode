package net.ion.radon.repository.minato;

import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;
import junit.framework.TestCase;

public class TestRepo extends TestCase {

	protected Session session;

	@Override
	protected void setUp() throws Exception {
		RepositoryCentral rc = new RepositoryCentral("61.250.201.117", 10505, "test_fluffy", "fluffy", "vmffjvl");
		session = rc.login("test");
		dataClear();
	}

	protected void dataClear() {
		session.dropWorkspace();
		session.clear();
	}

}
