package edu.uta.mysyllabi.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event implements Mappable {
	String localId;
	String cloudId;
	String courseId;
	Date date;
	String location;
	String name;
	
	
	private static final String DATE = "date";
	private static final String LOCATION = "location";
	private static final String NAME = "name";
	
	private static final String[] contentKeys = {
			DATE,
			LOCATION,
			NAME
	};
	
	@Override
	public Map<String, String> getContentMap() {
		Map<String, String> contentMap = new HashMap<String, String>();
		
		return contentMap;
	}
	@Override
	public List<String> getContentKeys() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void addContent(Map<String, String> contentMap) {
		// TODO Auto-generated method stub
		
	}
	
	
}
