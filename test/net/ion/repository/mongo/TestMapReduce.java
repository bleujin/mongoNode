package net.ion.repository.mongo;

import java.util.Iterator;

import net.ion.framework.util.Debug;
import net.ion.repository.mongo.mr.RowJob;

import com.mongodb.DBObject;
import com.mongodb.MapReduceOutput;

public class TestMapReduce extends TestBaseReset{

	public void testCreateMapReduce() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/customers/1").property("name", "RAM").property("age", 20) ;
				wsession.pathBy("/customers/2").property("name", "CHANDRAN").property("age", 26) ;
				wsession.pathBy("/customers/3").property("name", "VINOD").property("age", 24) ;
				wsession.pathBy("/customers/4").property("name", "SRIKUMAR").property("age", 30) ;
				wsession.pathBy("/customers/5").property("name", "SURAJ").property("age", 12) ;
				wsession.pathBy("/customers/6").property("name", "VINITHA").property("age", 32) ;
				wsession.pathBy("/customers/7").property("name", "NIRMAL").property("age", 23) ;
				wsession.pathBy("/customers/8").property("name", "RAGHAV").property("age", 10) ;
				wsession.pathBy("/customers/9").property("name", "SAYOOJ").property("age", 33) ;
				return null;
			}
		}) ;
		
		String mapFn = ""
				+ "function(){ var category ; "
				+ " if (this.age >= 21) category = 'Major' ; "
				+ " else category = 'Minor' ; "
				+ " "
				+ " emit(category, {name:this.name, sum:1});"
				+ "}" ;
		
		String reduceFn = ""
				+ "function(key, values){"
				+ " var result = {name:'', sum : 0} ; "
				+ " values.forEach(function(doc){ "
				+ "		result.sum += doc.sum ;"
				+ "		result.name += doc.name + ',' ;"
				+ "	});"
				+ " return result ;"
				+ "}" ;
		
		String finalFn = "" ;
		
		session.pathBy("/customers").children().mapreduce().mapFn(mapFn).finalFn(finalFn).reduceFn(reduceFn).eachRow(new RowJob<Void>(){
			public Void handle(MapReduceOutput output) {
				Iterator<DBObject> iter = output.results().iterator() ;
				while(iter.hasNext()){
					Debug.line(iter.next());
				}
				return null ;
			}
		}) ;
	}
	
}
