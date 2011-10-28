package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.PATH;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import net.ion.framework.util.MapUtil;


public class ISequence {

	private static Map<String, ISequence> SEQ_MAP = MapUtil.newMap(); 
	
	private Workspace workspace  ;
	private String seqId ;
	private String seqNodeId ;
	private int cacheCount = DEFAULT_CACHE_COUNT;
	private static final int DEFAULT_CACHE_COUNT = 10;
	private final String PROP_ID = "seq";
	private AtomicLong currVal ; 
	private AtomicLong cacheLimit ;

	private ISequence(Workspace workspace, String seqId) {
		this.workspace = workspace ;
		this.seqId = seqId ;
		init();
	}

	private void init() {
		Node result =  workspace.findOne(PropertyQuery.create(PATH, "/" + this.seqId), Columns.ALL);
		if(result == null){
			result = workspace.newNode(seqId);
			result.put(PROP_ID, 0L);
			workspace.save(result) ;
		}
		
		this.seqNodeId = result.getIdentifier()  ;
		this.currVal =  new AtomicLong(Long.valueOf(result.getAsInt(PROP_ID))) ;
		this.cacheLimit = new AtomicLong(this.currVal.get()) ;
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
		return currVal.get() ;
	}

	public synchronized void reset() {
		workspace.findAndUpdate(PropertyQuery.createById(seqNodeId), MapUtil.create(PROP_ID, 0L)) ;
		this.currVal = new AtomicLong() ;
		this.cacheLimit = new AtomicLong(0) ;
	}

	public synchronized long nextVal() {
		if (currVal.get() >= cacheLimit.get()) {
			for (int i = 0; i < cacheCount; i++) {
				cacheLimit.incrementAndGet() ;
			}
			workspace.inc(PropertyQuery.createById(seqNodeId), PROP_ID, cacheCount) ;
			return nextVal() ;
		} else {
			return this.currVal.incrementAndGet() ;
		}
	}

	public int getCacheCount(){
		return cacheCount ;
	}
	
	long getCacheRemained() {
		return cacheLimit.get() - currVal.get() ;
	}
	
	
}
