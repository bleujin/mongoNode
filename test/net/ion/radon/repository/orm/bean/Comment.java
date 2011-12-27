package net.ion.radon.repository.orm.bean;

import net.ion.radon.repository.orm.InnerNodeORM;

public class Comment extends InnerNodeORM{

	public Comment() {
	}

	public static Comment create(String regUserId, String message) {
		Comment cmt = new Comment();
		cmt.put("regUserId", regUserId) ;
		cmt.put("message", message) ;
		return cmt;
	}

}
