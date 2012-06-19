package net.ion.radon.repository;

public abstract class SessionProtectedQuery implements SessionQuery{

	private static final long serialVersionUID = 1961135385940974561L;
	
	protected abstract Workspace getWorkspace() ;
	protected abstract PropertyFamily getSort() ;
	protected abstract Session getSession() ;

}
