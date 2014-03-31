package net.ion.repository.mongo;

import net.ion.repository.mongo.node.NodeCommon;

public interface ISession<T extends NodeCommon<T>> {

	public T pathBy(Fqn fqn);

	public T root()  ;

	public boolean exists(String fqn) ;

	public T pathBy(String fqn);

	public Credential credential();

	public Workspace workspace() ;
}
