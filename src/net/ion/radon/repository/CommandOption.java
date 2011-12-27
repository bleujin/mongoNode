package net.ion.radon.repository;

import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceCommand.OutputType;

public class CommandOption {

	public final static CommandOption BLANK = new CommandOption() ;
	
	private int limit = 0 ;
	private int skip = 0 ;
	private NodeObject sort = NodeObject.create() ;
	private boolean verbose = true ;
	private OutputType outputType ;
	private final String outputCollection ;
	
	private CommandOption(){
		this(MapReduceCommand.OutputType.INLINE, null) ;
	}
	
	private CommandOption(OutputType outputType, String outputCollction) {
		this.outputType = outputType ;
		this.outputCollection = (outputCollction  != null) ? outputCollction.toLowerCase() : null;
	}

	public static CommandOption create() {
		return new CommandOption();
	}

	public static CommandOption create(OutputType outputType, String outputCollection) {
		return new CommandOption(outputType, outputCollection);
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

	public OutputType getOutputType(){
		return outputType ;
	}
	
	public String getOutputCollection(){
		return outputCollection ;
	}

	public void setOutputType(OutputType outputType) {
		this.outputType = outputType ;
	}
}
