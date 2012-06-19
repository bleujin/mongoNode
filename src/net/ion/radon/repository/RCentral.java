package net.ion.radon.repository;

public interface RCentral {

	public Session login(String defaultWorkspaceName) ;

	public Session login(String dbName, String defaultWorkspaceName) ;
}
