package net.ion.radon.repository.innode;

public class NodeSort {
	private String path ;
	private boolean ascending ;
	
	private NodeSort(String path, boolean ascending) {
		this.path = path ;
		this.ascending = ascending ;
	}

	public static NodeSort create(String path, boolean ascending) {
		return new NodeSort(path, ascending);
	}

	public String getPath() {
		return path;
	}
	
	public boolean isAscending() {
		return ascending;
	}
	
}
