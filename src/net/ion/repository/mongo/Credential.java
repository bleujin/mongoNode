package net.ion.repository.mongo;

import java.io.PrintStream;

public class Credential {

	public static final Credential ADMIN = new Credential("admin", "nimda") ;
	public static final Credential EMANON = new Credential("emanon", "emanon") ;
	
	private final String accessKey ;
	private String secretKey ;
	private PrintStream tracer = System.out ;

	public Credential(String accessKey, String secretKey){
		this.accessKey = accessKey ;
		this.secretKey = secretKey ;
	}
	
	public String accessKey() {
		return accessKey;
	}
	
	public String secretKey(){
		return secretKey ;
	}
	
	public Credential clearSecretKey() {
		this.secretKey = null ;
		return this;
	}

	public PrintStream tracer(){
		return tracer ;
	}
	
	public Credential tracer(PrintStream print) {
		this.tracer = print ;
		return this ;
	}
}
