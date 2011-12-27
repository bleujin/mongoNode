package net.ion.radon.repository.orm.bean;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.radon.repository.orm.NodeORM;
import net.ion.radon.repository.orm.IDMethod;

@IDMethod(workspaceName="peoples", groupId="article", keyPropId="articleNo", managerClz=Article.class)
public class Article extends NodeORM{

	private static final long serialVersionUID = 905196802522340992L;

	public Article(int artno){
		super(artno) ;
	}
	
	public int getArtNo(){
		return (Integer)getUid() ;
	}
	
	public void setSubject(String subject){
		this.put("subject", subject) ;
	}
	
	public void setContent(String regUserId){
		this.put("regUserId", regUserId) ;
	}

	public void setRegister(User register) {
		this.inner("user", register) ;
	}

	public String getSubject() {
		return super.getString("subject");
	}

	public User getRegister() {
		return this.inner("user", User.class);
	}

	public void addComment(Comment cmt) {
		this.push("comments", cmt) ;
	}

	public List<Comment> getComments() {
		return this.inlist("comments", Comment.class);
	}
	
	
}


