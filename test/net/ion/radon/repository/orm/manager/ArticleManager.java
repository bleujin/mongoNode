package net.ion.radon.repository.orm.manager;

import java.util.List;

import net.ion.radon.core.PageBean;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.orm.GenericManager;
import net.ion.radon.repository.orm.NodeORM;
import net.ion.radon.repository.orm.IDMethod;
import net.ion.radon.repository.orm.bean.Article;
import net.ion.radon.repository.orm.bean.Comment;
import net.ion.radon.repository.orm.bean.People;

public class ArticleManager extends GenericManager<Article> {

	public ArticleManager(Session session) {
		super(session) ;
	}

	public List<Article> findByRegister(String regUserId) {
		return createQuery().eq("regUserId", regUserId).toList(PageBean.TEN);
	}


	public void addComment(Comment comment){
		
	}

}
