package net.ion.radon.repository;

import java.util.Map;
import java.util.Vector;

import net.ion.framework.util.MapUtil;


public class ISequence {


	private static Map<String, ISequence> SEQ_MAP = MapUtil.newMap(); 
	
	private Workspace workspace  ;
	private String seqId ;
	private Node seqNode ;
	private Vector<Long> stack = new Vector<Long>(); 
	private int cacheCount = DEFAULT_CACHE_COUNT;
	private static final int DEFAULT_CACHE_COUNT = 10;
	private final String PROP_ID = "seq";
	private long currVal = 0; 

	private ISequence(Workspace workspace, String seqId) {
		this.workspace = workspace ;
		this.seqId = seqId ;
		this.seqNode = findNode();
	}

	private Node findNode() {
		Node result =  workspace.findByPath("/" + this.seqId);
		if(result == null){
			result = workspace.newNode(seqId);
			result.put(PROP_ID, 0);
			workspace.save(result) ;
		}
		this.currVal = currVal() ;
		return result ;
	}

	synchronized static ISequence createOrLoad(Workspace workspace, String prefix, String id) {
		String seqId = prefix + "_" + id;
		
		if(SEQ_MAP.containsKey(seqId) )
			return SEQ_MAP.get(seqId);
		else{
			ISequence seq = new ISequence(workspace, seqId);
			SEQ_MAP.put(seqId, seq);
			return seq;
		}
	}
	
	
	public void setCacheCount(int cacheCount){
		this.cacheCount = cacheCount > 1 ? cacheCount : 1 ;
	}
	
	public long currVal() {
		return currVal ;
	}

	public synchronized void reset() {
		seqNode.put(PROP_ID, 0L) ;
		stack.clear() ;
		this.currVal = 0L ;
		save();
	}

	private void save() {
		workspace.save(seqNode) ;
	}

	public synchronized long nextVal() {
		if (stack.isEmpty()) {
			final long currVal = currVal();
			seqNode.put(PROP_ID, currVal + cacheCount) ;
			for (int i = 1; i <= cacheCount; i++) {
				stack.add(currVal + i) ;
			}
			save();
			return nextVal() ;
		} else {
			this.currVal = stack.remove(0) ;
			return this.currVal ;
		}
	}

	public int getCacheCount(){
		return cacheCount ;
	}
	
	int getCacheRemained() {
		return stack.size() ;
	}
	
	
}
