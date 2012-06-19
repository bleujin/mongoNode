package net.ion.radon.repository.minato;

import java.util.List;

import net.ion.radon.core.PageBean;

public class TestNodeToObj extends TestRepo {

	public void testNodeToObj() throws Exception {
		createSampleData(1);
		
		Comment found = session.createQuery().findOne(Comment.class);
		
		assertNotNull(found);
		assertEquals("title1", found.getTitle());
		assertEquals("content1", found.getContent());
	}

	public void testNodeToList() throws Exception {
		createSampleData(20);
		
		List<Comment> rtn = session.createQuery().find().toList(Comment.class, PageBean.TEN);

		assertEquals(10, rtn.size());
	}
	
	private void createSampleData(int loop) {
		for (int i = 1; i <= loop; i++) {
			session.newNode().put("title", "title" + i).put("content", "content" + i);
		}
		session.commit();
	}
	
	public void testNullToObj() throws Exception {
		Comment comment = session.createQuery().findOne(Comment.class);
		assertNull(comment);
	}
	
}

class Comment {
	String title;
	String content;
	long created;
	
	private Comment() {
		this.created = System.currentTimeMillis();
	}
	public String getTitle() {
		return title;
	}
	public String getContent() {
		return content;
	}
	public long getCreated() {
		return created;
	}
	public String toString() {
		return "title : " + title + ", content : " + content + ", created : " + created;
	}
}
