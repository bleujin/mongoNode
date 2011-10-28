package net.ion.radon.repository;

import com.mongodb.MapReduceCommand;

public class CommandOption {

	public final static CommandOption BLANK = new CommandOption() ;
	
	private int limit = 0 ;
	private NodeObject sort = NodeObject.create() ;
	private boolean verbose = true ;
	private int skip = 0 ;
	
	private CommandOption(){
		
	}
	
	public static CommandOption create() {
		return new CommandOption();
	}

	
	public CommandOption setLimit(int limit){
		this.limit = limit ;
		return this ;
	}

	public CommandOption ascending(String... props){
		for (String prop : props) {
			sort.put(prop, 1) ;
		}
		return this ;
	}
	
	public CommandOption descending(String... props){
		for (String prop : props) {
			sort.put(prop, -1) ;
		}
		return this ;
	}
	
	public CommandOption setVerbose(boolean verbose){
		this.verbose = verbose ;
		return this ;
	}
	
	void apply(MapReduceCommand command) {
		command.setLimit(limit) ;
		command.setSort(sort.getDBObject()) ;
		command.setVerbose(verbose) ;
	}

	public void setSkip(int n) {
		this.skip = n ;
	}


}
