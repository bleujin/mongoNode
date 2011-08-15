package net.ion.radon.repository;

public class ReferenceSourceQuery {
	
	 
	private String findQuery;

	public ReferenceSourceQuery(String findQuery) {
		this.findQuery = findQuery;
	}

	public static ReferenceSourceQuery byAradonGroup(String groupId) {
		return new ReferenceSourceQuery(":aradon:" + groupId);
	}
	
	public String getFindQuery() {
		return findQuery;
	}

	public static ReferenceSourceQuery byReference(String refType) {
		return new ReferenceSourceQuery(":reference:" + refType);
	}

	public static ReferenceSourceQuery byWorkspace(String workspaceName) {
		return new ReferenceSourceQuery(":workspace:" + workspaceName);
	}
	
	
}
