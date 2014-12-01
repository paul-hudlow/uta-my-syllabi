package edu.uta.mysyllabi.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uta.mysyllabi.core.Mappable;

public abstract class DataContainer implements Mappable {
	private String localId;
	private String cloudId;
	private boolean locked = false;
	private long timeUpdated = 0L;
	
	public DataContainer(String localId, String cloudId) {
		this.localId = localId;
		this.cloudId = cloudId;
	}
	
	public String getLocalId() {
		return this.localId;
	}
	
	public String getCloudId() {
		return this.cloudId;
	}
	
	public long getUpdateTime() {
		return timeUpdated;
	}
	
	public void setUpdateTime(long timeUpdated) {
		this.timeUpdated = timeUpdated;
	}
	
	public void setLocalId(String localId) {
		if (this.localId == null) {
			this.localId = localId;
		} else {
			throw new IllegalStateException("Course already has a local id!");
		}
	}
	
	public void setCloudId(String cloudId) throws IllegalStateException {
		if (this.cloudId == null) {
			this.cloudId = cloudId;
		} else {
			throw new IllegalStateException("Course already has a cloud id!");
		}
	}
	
	public Map<String, String> getDifferenceMap(DataContainer anotherContainer) {
		Map<String, String> nativeMap = this.getContentMap();
		Map<String, String> foreignMap = anotherContainer.getContentMap();
		Map<String, String> differenceMap = new HashMap<String, String>();
		List<String> keys = getContentKeys();
		String nativeValue, foreignValue;
		for (String nextKey : keys) {
			nativeValue = nativeMap.get(nextKey);
			foreignValue = foreignMap.get(nextKey);
			if (foreignValue != null) {
				if (!foreignValue.equals(nativeValue)) {
					differenceMap.put(nextKey, foreignValue);
				}
			} else if (nativeValue != null) {
				differenceMap.put(nextKey, "");
			}
		}
		return differenceMap;		
	}
	
	@Override
	public boolean sharesContents(Mappable another) {
		return this.getContentMap().equals(another.getContentMap());
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;	
	}
	
}
