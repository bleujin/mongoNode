package net.ion.radon.repository.myapi;

import com.mongodb.DB;

public interface ICredential {
	public String getUniqueId() ;

	public boolean isBlank();

	public boolean isAuthenticated(DB db);
}

