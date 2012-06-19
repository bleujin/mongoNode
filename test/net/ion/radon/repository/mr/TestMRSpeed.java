package net.ion.radon.repository.mr;

import java.util.Collections;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.RandomUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Columns;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.TestBaseRepository;
import net.ion.radon.repository.util.JSONMessage;

import org.apache.commons.lang.RandomStringUtils;

public class TestMRSpeed extends TestBaseRepository {

	public void testMapSortOptimize() throws Exception {
		createDummyData(1000);

		String params = "var _params = " + PropertyFamily.create().put("sortkey", "sort2").put("skip", 0).put("limit", 2).put("order", 1).toJSONString() + "; ";
		params += "_params.comfn = function(f1, f2) { " + "if (f1[_params.sortkey] > f2[_params.sortkey]) return 1 * _params.order; " + "else if (f1[_params.sortkey] < f2[_params.sortkey]) return -1 * _params.order; " + "else return 0; " + "}; ";
		params += "" +
			" Array.prototype.myEach = function(fun, thisp){ " +
			"    if (typeof fun != 'function') throw new TypeError(); " +
			"    var result = true ; " +
			"    for (var i = 0; i < this.length; i++){ " +
			"      if (i in this) { " +
			"	      var varResult = fun.call(thisp, this[i], i, this); " +
			"	      if (varResult == false) { " +
			"		      result = varResult ; " +
			"		      break ; " +
			"	      } " +
			"      } " +
			"    } " +
			"    return result ; " +
			" }; " +
			" Array.prototype.topSort = function(limit, compareFn){ " +
			"		var myResult = [] ; " +
			"		var maxValue ; " +
			"		Array.prototype.myEach.call(this, function(sourceEle, sourceIndex, sourceArray) { " +
			"			var notInRange = myResult.myEach(function(element, index, array){ " +
			"				if (compareFn(sourceEle, element) <= 0 && index <= limit) { " +
			"					myResult.splice(index, 0, sourceEle); " +
			"					return false ; " +
			"				} " +
			"			}, myResult) ; " +
			"			if (notInRange && myResult.length < limit) {myResult.push(sourceEle) ;} " +
			"			myResult = myResult.slice(0, limit) ; " +
			"		}, this) ; " +
			"		return myResult ; " +
			" }; " ;
		
		String map = "function(){ " +
				" " + params +
				" var myResult = this.post_list.topSort(_params.limit, _params.comfn) ; " +
				" emit(this._id, {plist:myResult, post:this.post, pcount: new NumberLong(this.post_list.length) });}";

		long start = System.nanoTime();
		NodeCursor nc = session.createQuery().mapreduce(map, "", "");
		nc.debugPrint(PageBean.ALL);
		Debug.line((System.nanoTime() - start) / 1000000);
	}
	
	public void testFilter() throws Exception {
		createDummyData(10000);

		String params = "var _params = " + PropertyFamily.create().put("sortkey", "sort2").put("skip", 0).put("limit", 2).put("order", 1).toJSONString() + "; ";
		params += "_params.comfn = function(f1, f2) { " + "if (f1[_params.sortkey] > f2[_params.sortkey]) return 1 * _params.order; " + "else if (f1[_params.sortkey] < f2[_params.sortkey]) return -1 * _params.order; " + "else return 0; " + "}; ";
		params += "_params.filter = function(element, index, array) { return index % 2 == 0; }; ";
		params += "" +
				" Array.prototype.filter = function(fun, thisp) {" +
				" var len = this.length; " + 
				"    if (typeof fun != 'function') throw new TypeError(); " + 
				"    var res = []; " + 
				"    for (var i = 0; i < len; i++){ " + 
				"      if (i in this){ " + 
				"        var val = this[i];  " + 
				"        if (fun.call(thisp, val, i, this)) res.push(val); " + 
				"      } " + 
				"    } " + 
				"    return res; " + 
				"  };" ;
		String map = "function(){ " +
				" " + params +
				" var myresult = Array.prototype.filter.call(this.post_list, _params.filter, this.post_list) ; " + 
				" Array.prototype.sort.call(myresult, _params.comfn); " +
				" myresult = Array.prototype.slice.call(myresult, _params.skip, _params.limit); " + 
				" emit(this._id, {post_list:myresult, postid:this.postid});}";

		long start = System.nanoTime();
		NodeCursor nc = session.createQuery().mapreduce(map, "", "");
		nc.debugPrint(PageBean.ALL);
		Debug.line((System.nanoTime() - start) / 1000000);
	}

	public void testMap() throws Exception {
		createDummyData(1000);

		String params = "var _params = " + PropertyFamily.create().put("sortkey", "sort2").put("skip", 0).put("limit", 2).put("order", 1).toJSONString() + "; ";
		params += "_params.comfn = function(f1, f2) { " + "if (f1[_params.sortkey] > f2[_params.sortkey]) return 1 * _params.order; " + "else if (f1[_params.sortkey] < f2[_params.sortkey]) return -1 * _params.order; " + "else return 0; " + "}; ";

		String map = "function(){ " +
				" " + params +
				" Array.prototype.sort.call(this.post_list, _params.comfn); " +
				" var sortedpost = Array.prototype.slice.call(this.post_list, _params.skip, _params.limit); " + 
				" emit(this._id, {post_list:sortedpost, postid:this.postid});}";

		long start = System.nanoTime();
		
		NodeCursor nc = session.createQuery().mapreduce(map, "", "");
		nc.debugPrint(PageBean.ALL);
		Debug.line((System.nanoTime() - start) / 1000000);
	}

	
	public void testRecentSlice() throws Exception {
		createDummyData(10000);
		long start = System.nanoTime();
		NodeCursor nc = session.createQuery().find(Columns.append().slice("post_list", -10, 10));
		nc.debugPrint(PageBean.ALL);
		Debug.line('?', (System.nanoTime() - start) / 1000000);
		
	}
	
	public void testReduce() throws Exception {
		createDummyData(10000);

		String map = "function(){ emit(this._id, {post_list:this.post_list, post:this.post, postid:this.postid});}";
		String finalFunction = makeSampleFinalFunction();

		long start = System.nanoTime();
		NodeCursor nc = session.createQuery().mapreduce(map, "", finalFunction);
		Debug.line(System.nanoTime() - start);

		nc.debugPrint(PageBean.ALL);
	}

	public void testCollectionSort() throws Exception {
		List<String> list = ListUtil.newList();

		int size = 10000;
		for (int i = 0; i < size; i++) {
			list.add(RandomStringUtils.random(200));
		}

		long start = System.nanoTime();
		Collections.sort(list);
		Debug.line(System.nanoTime() - start);
	}

	private String makeSampleFinalFunction() {
		String params = "var _params = " + PropertyFamily.create().put("sortkey", "sort1").put("skip", 0).put("limit", 10).put("order", -1).toJSONString() + "; ";
		params += "_params.comfn = function(f1, f2) { " + "if (f1[_params.sortkey] > f2[_params.sortkey]) return 1 * _params.order; " + "else if (f1[_params.sortkey] < f2[_params.sortkey]) return -1 * _params.order; " + "else return 0; " + "}; ";
		return "function(key, values) { " + params + " var doc = values; Array.prototype.sort.call(doc.post_list, _params.comfn); doc.post_list = Array.prototype.slice.call(doc.post_list, _params.skip, _params.limit); return doc; }";
	}

	private void createDummyData(int size) throws Exception {
		Node dummy = session.newNode().put("post", "http://localhost/post");
		for (int i = 1; i <= size; i++) {
			JSONMessage jm =  JSONMessage.create().put("postId", new ObjectId().toString())
				.put("parentid", 0).put("content", (i + RandomUtil.nextRandomString(40)))
				.put("sort1", System.nanoTime()).put("sort2", RandomUtil.nextInt(size)) ;
			
			dummy.inlist("post_list").push(jm.toJSON().toMap());
		}
		session.commit();
	}

}
