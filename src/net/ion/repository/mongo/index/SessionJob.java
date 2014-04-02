package net.ion.repository.mongo.index;

import net.ion.repository.mongo.Workspace;

public interface SessionJob {
	public void run(Workspace workspace) ;
}
