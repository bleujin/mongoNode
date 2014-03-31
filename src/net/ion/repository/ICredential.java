package net.ion.repository;

import net.ion.repository.mongo.RepositoryMongo;

public interface ICredential {
	public String getUniqueId() ;

	public boolean isBlank();

	public boolean isAuthenticated(RepositoryMongo rm );
}
