package net.ion.repository.mongo.node;

import com.mongodb.WriteResult;

public class NodeResult {

	private WriteResult wr;
	NodeResult(WriteResult wr) {
		this.wr = wr ;
	}

	public static NodeResult create(WriteResult wr) {
		return new NodeResult(wr);
	}

	public int getRowCount() {
		return wr.getN();
	}

	public String errorMessage() {
		return wr.getError();
	}

}
