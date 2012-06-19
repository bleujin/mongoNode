package net.ion.radon.repository;

import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;

public class StopWatch {

	private long start ;
	private String name ;
	private static int indent = 4 ;
	
	public StopWatch(){
		this("DEFAULT") ;
	} 
	public StopWatch(String name){
		start(name) ;
	} 
	
	private void start(String name){
		start = System.nanoTime() ;
		this.name = name ;
	}

	public void end(){
		Debug.debug(getIndent(), name, (System.nanoTime() - start) / 1000000) ;
		indent -- ;
	}

	private String getIndent() {
		return StringUtil.repeat(" ", indent * 4);
	}

	public void current() {
		Debug.debug(getIndent(), "CURRENT", (System.nanoTime() - start) / 1000000) ;
	}
	
}
