package edu.uta.mysyllabi.core;

import java.util.List;
import java.util.Map;

public interface Mappable {
	
	public Map<String, String> getContentMap();
	
	public List<String> getContentKeys();
	
	public void addContent(Map<String, String> contentMap);
	
	public boolean sharesContents(Mappable anotherMappable);
	
	public interface Child {
		
		public Map<String, String> getContentMap(String keyPrefix);
		
		public List<String> getContentKeys(String keyPrefix);
		
		public void addContent(Map<String, String> contentMap, String keyPrefix);
		
	}
	
}
