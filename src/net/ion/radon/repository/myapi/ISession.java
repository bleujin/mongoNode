package net.ion.radon.repository.myapi;


public interface ISession {

	public IWorkspace getWorkspace(String workspaceName) ;
	public INode findNodeById(String oid) ;

}
