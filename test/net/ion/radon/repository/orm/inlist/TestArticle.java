package net.ion.radon.repository.orm.inlist;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.TestBaseRepository;
import net.ion.radon.repository.orm.bean.Article;
import net.ion.radon.repository.orm.bean.Comment;
import net.ion.radon.repository.orm.bean.User;
import net.ion.radon.repository.orm.manager.ArticleManager;
import junit.framework.TestCase;

public class TestArticle extends TestBaseRepository{

	private ArticleManager am ;
	@Override protected void setUp() throws Exception {
		super.setUp();
		this.am = new ArticleManager(session) ;
		am.createQuery().remove() ;
	}
	
	public void testCreate() throws Exception {
		createSample();

		Article found = am.createQuery().eq("regUserId", "bleujin").ascending("__aradon.uid").find(PageBean.TEN).get(0);
		assertEquals(100, found.getArtNo()) ;
		assertEquals("hello bleujin", found.getString("subject")) ;
		assertEquals("hello bleujin", found.getSubject()) ;
	}

	private void createSample() {
		for (int i : ListUtil.rangeNum(100, 150) ) {
			Article article = am.loadInstance(i) ;
			article.setSubject("hello bleujin") ;
			article.setContent("bleujin") ;
			article.save() ;
		}
	}
	
	public void testInRegister() throws Exception {
		Article article = am.loadInstance(100) ;
		article.setSubject("hello bleujin") ;
		article.setContent("bleujin") ;
		article.setRegister(User.create("bleujin", 20)) ;
		article.save() ;
		
		assertEquals(1, am.createQuery().find().count()) ;
		
		Article found = am.createQuery().findOne() ;
		User reg = found.getRegister() ;
		
		assertEquals("bleujin", reg.getUserName()) ;
		assertEquals(20, reg.getAge()) ;
	}
	
	
	public void testComment() throws Exception {
		Article article = am.loadInstance(100) ;
		article.setSubject("hello bleujin") ;
		article.setContent("bleujin") ;
		
		article.addComment(Comment.create("bleujin", "hi bleujin")) ;
		article.addComment(Comment.create("hero", "hi hero")) ;
		
		List<Comment> cmts = article.getComments() ;
		
		
	}
	
	
	
	
	
	
	
	
}
