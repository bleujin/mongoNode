package net.ion.radon.repository.innode;

import net.ion.radon.repository.NodeObject;

public class BetweenFilter implements InNodeFilter {

	private String path;
	private GreaterThanFilter gfilter ;
	private LessThanFilter lfilter ;

	private BetweenFilter(String path, Object from, Object to) {
		this.path = path;
		this.gfilter = GreaterThanFilter.create(path, from);
		this.lfilter = LessThanFilter.create(path, to);
	}

	public final static BetweenFilter create(String path, Object from, Object to) {
		return new BetweenFilter(path, from, to);
	}

	public boolean isTrue(NodeObject no) {
		return gfilter.isTrue(no) && lfilter.isTrue(no) ;
	}

}
