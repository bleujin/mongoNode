package net.ion.radon.repository.vfs;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.radon.AradonServer;
import net.ion.radon.InfinityThread;
import net.ion.radon.Options;
import net.ion.radon.TestAradon;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;

public class TestSimul extends TestAradon{

	public void testFirst() throws Exception {
		Session session = RepositoryCentral.testCreate().testLogin("wname") ;
		List<Node> nodes = session.createQuery().find().toList(PageBean.ALL) ;
		for (Node node :nodes) {
			Debug.debug(node.toMap()) ;
		}
	}
	
	public void testStart() throws Exception {
		
		Session session = RepositoryCentral.testCreate().testLogin("wname") ;
		session.dropWorkspace() ;
		Node bleujin = session.newNode("bleujin") ;
		bleujin.createChild("child").put("greeting", "hi").put("age", 20) ;
		session.commit() ;
		
		Aradon aradon = new AradonServer(new Options(new String[]{"-config:resource/config/plugin-system-vfs.xml"})).start() ;
		new InfinityThread().startNJoin() ;
	}

}
