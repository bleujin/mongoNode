package net.ion.radon.repository.mr;


import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.ApplyHander;
import net.ion.radon.repository.CommandOption;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.TestBaseRepository;

/**
 * <p>Title: TestGroupMap.java</p>

 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: I-ON Communications</p>
 * <p>Date : 2011. 10. 26.</p>
 * @author novision
 * @version 1.0
 */

public class TestGroupMap extends TestCase {

	public void testFirst() throws Exception {
		RepositoryCentral rc = RepositoryCentral.create("61.250.201.78", 27017) ;
		Session session = rc.testLogin("ICS_MONGO", "ics_content_bleujin") ;
		
//		session.createQuery().find().debugPrint(PageBean.ALL) ;
		
		String mapFunction = "function(){ emit(this.catid + this.artid, {self:this});}" ;
		String reduceFunction = "function(key, values){ " +
				"var doc = {'afields':[]} ;  " +
				"  doc.artid = key ; " +
				"  doc.afieldcount = values.length - 1; " +
				"  for (var i in values) {" +
				"	 var cont = values[i].self ;   " +
				"	 if (cont.afieldid) { " +
				" 	   doc.afields.push(cont);" +
				"	 } else {" +
				" 	   doc.catid = cont.catid ;" +
				" 	   doc.artid = cont.artid ;" +
				" 	   doc.action = cont.action ;" +
				" 	   doc.expireday = cont.expireday ;" +
				" 	   doc.operday = cont.operday ;" +
				" 	   doc.creday = cont.creday ;" +
				" 	   doc.modday = cont.modday ;" +
				" 	   doc.artsubject = cont.artsubject ;" +
				" 	   doc.artcont = cont.artcont ;" +
				" 	   doc.keyword = cont.keyword ;" +
				" 	   doc.reguserid = cont.reguserid ;" +
				" 	   doc.moduserid = cont.moduserid ;" +
				" 	   doc.thumbnail = cont.thumbnail ;" +
				"    }" + 
				"  }  " +
				"return doc ;   } ";
		String finalFunction = "function(key, reduced) {" +
				" if (reduced.self) {" +
				" 	 return reduced.self ;" +
				" } else {" +
				" 	return reduced ;" +
				" }" +
				"}";
		NodeCursor nc = session.createQuery().or(PropertyQuery.create("artid", 1172124), PropertyQuery.create("artid", 1172144)).mapreduce(mapFunction, reduceFunction, finalFunction) ;
		
		nc.debugPrint(PageBean.ALL) ;
	}
	
}
