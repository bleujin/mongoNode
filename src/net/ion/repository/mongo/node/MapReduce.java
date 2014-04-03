package net.ion.repository.mongo.node;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceCommand.OutputType;

import net.ion.framework.util.StringUtil;
import net.ion.repository.mongo.mr.RowJob;

public class MapReduce {

	private ReadChildren rchildren;
	private String mapFn = "function(){ emit(this._id, {self:this});}" ;
	private String reduceFn = "" ;
	private String finalFn = "" ;

	private MapReduce(ReadChildren rchildren) {
		this.rchildren = rchildren ;
	}

	public static MapReduce create(ReadChildren rchildren) {
		return new MapReduce(rchildren);
	}

	public MapReduce mapFn(String mapFn) {
		this.mapFn = mapFn ;
		return this;
	}

	public <T> T eachRow(RowJob<T> rowJob) {
		return rchildren.session().collection().mapReduce(this, rchildren, rowJob) ;
	}

	public MapReduce reduceFn(String reduceFn) {
		this.reduceFn = reduceFn ;
		return this;
	}

	public String finalFn() {
		return finalFn ;
	}
	
	public String mapFn(){
		return mapFn ;
	}
	
	public String reduceFn(){
		return reduceFn ;
	}

	public MapReduce finalFn(String finalFn) {
		this.finalFn = finalFn ;
		return this;
	}

	<T> T runCommand(DBCollection collection, DBObject filters, RowJob<T> rowJob) {
		MapReduceCommand command = new MapReduceCommand(collection, mapFn(), reduceFn(), null, OutputType.INLINE, filters) ;
		if (StringUtil.isNotBlank(finalFn()))
			command.setFinalize(finalFn());
//		command.setLimit(limit) ;
//		command.setSort(sort.getDBObject()) ;
//		command.setVerbose(true) ;
		
		return rowJob.handle(collection.mapReduce(command)) ; 

	}

}
