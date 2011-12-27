package net.ion.radon.repository;

import com.mongodb.DBCollection;

public class TestOnlyWorkspace {

	private Workspace workspace ;
	public TestOnlyWorkspace(Workspace workspace){
		this.workspace = workspace ;
	}
	
	public final static TestOnlyWorkspace create(Workspace workspace){
		return new TestOnlyWorkspace(workspace) ;
	}
	
	public DBCollection getCollection(){
		return workspace.innerCollection() ;
	}
}
