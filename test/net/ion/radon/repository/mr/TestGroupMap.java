package net.ion.radon.repository.mr;


import junit.framework.TestCase;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;

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
		
		try {
			
		RepositoryCentral rc = RepositoryCentral.create("61.250.201.78", 27017, "ICS_MONGO") ;
		Session session = rc.testLogin("ics_content_4ec49bc4aa2ca5f57158ca94") ;
		
//		session.createQuery().find().debugPrint(PageBean.ALL) ;
		
		String mapFunction = "function(){ emit(this.catid + this.artid, {self:this});}" ;

		String finalFunction = "function(key, reduced) {" +
		" if (reduced.self) {" +
		" 	 return reduced.self ;" +
		" } else {" +
		" 	return reduced ;" +
		" }" +
		"}";
		
		StringBuilder sb = new StringBuilder();
		sb.append("function Reduce(key, values) { ");
		sb.append("    var doc = {} ; ");
		sb.append("    for (var i in values) {  ");
		sb.append("        var cont = values[i].self ;  ");
		sb.append("        if(cont) {  ");
		sb.append("            if (cont.afieldid) {  "); 
		sb.append("                doc[cont.afieldid] = {} ;  ");
		sb.append("                doc[cont.afieldid].afieldid = cont.afieldid ;  ");
		sb.append("                doc[cont.afieldid].typecd = cont.typecd ;  ");
		sb.append("                if(cont.typecd == 'Number' || cont.typecd == 'Currency') {  ");
		sb.append("                    doc[cont.afieldid].value = new NumberLong(cont.value) ;  ");
		sb.append("                } else {  ");
		sb.append("                    doc[cont.afieldid].value = cont.value ;  ");
		sb.append("                }  ");
		sb.append("            } else {  ");
		sb.append("                doc.sync_transaction_sequence = cont.sync_transaction_sequence;  ");
		sb.append("                doc.catid = cont.catid;  ");
		sb.append("                doc.artid = new NumberLong(cont.artid);  ");
		sb.append("                doc.modserno = new NumberLong(cont.modserno);  ");
		sb.append("                doc.action = cont.action;  ");
		sb.append("                doc.expireday = cont.expireday;  ");
		sb.append("                doc.operday = cont.operday;  ");
		sb.append("                doc.creday = cont.creday;  ");
		sb.append("                doc.modday = cont.modday;  ");
		sb.append("                doc.artsubject = cont.artsubject;  ");
		sb.append("                doc.artcont = cont.artcont;  ");
		sb.append("                doc.keyword = cont.keyword;  ");
		sb.append("                doc.reguserid = cont.reguserid;  ");
		sb.append("                doc.moduserid = cont.moduserid;  ");
		sb.append("                doc.thumbnail = cont.thumbnail;  ");
		sb.append("                doc.statuscd = cont.statuscd;  ");
		sb.append("            }  ");
		sb.append("        } else {  ");
		sb.append("            var value = values[i];  ");
		sb.append("            for(var key in value) {  ");
		sb.append("                doc[key] = value[key];  ");
		sb.append("            }  ");
		sb.append("        }  ");
		sb.append("    }  ");
		sb.append("    return doc;  ");
		sb.append("}  ");
		

		//NodeCursor nc = session.createQuery().or(PropertyQuery.create("artid", 1172124), PropertyQuery.create("artid", 1172144)).mapreduce(mapFunction, sb.toString(), finalFunction) ;
		//NodeCursor nc = session.createQuery().eq("artid", 1172144).mapreduce(mapFunction, sb.toString(), finalFunction) ;
		NodeCursor nc = session.createQuery().mapreduce(mapFunction, sb.toString(), finalFunction) ;

		nc.debugPrint(PageBean.ALL) ;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
