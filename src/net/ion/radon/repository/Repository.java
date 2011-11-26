package net.ion.radon.repository;

import java.util.Set;

public interface Repository {

	public Workspace getWorkspace(String wname, WorkspaceOption option)  ;

	public Set<String> getWorkspaceNames() ;
}
