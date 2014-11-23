package edu.uta.mysyllabi.core;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Event implements Mappable,Listable.Detailed {
	String localId;
	String cloudId;
	Date date;
	String location;
	String name;
	String gradeCategory;
	
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
		contentMap.put(NAME, this.name);
		contentMap.put(LOCATION, this.location);
		contentMap.put(DATE, Long.toString(this.date.getTime()));
		return contentMap;
	}
	@Override
	public List<String> getContentKeys() {
		LinkedList<String> keyList = new LinkedList<String>();
		for (int i = 0; i < contentKeys.length; i++) {
			keyList.add(contentKeys[i]);
		}
		return keyList;
	}
	@Override
	public void addContent(Map<String, String> contentMap) {
		setDate(contentMap.get(DATE));
		setLocation(contentMap.get(LOCATION));
		setName(contentMap.get(NAME));
	}
	
	public void setName(String name) {
		if (name != null) {
			this.name = name;
		}
	}
	
	public void setLocation(String location) {
		if (location != null) {
			this.location = location;
		}
	}
	public void setDate(String milliseconds) {
		if (milliseconds != null) {
			try {
				this.date = new Date(Long.parseLong(milliseconds));
			} catch (NumberFormatException exception) {
				this.date = null;
			}
		}
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public String getDate() {
		return this.date.toString();
	}
	public String getLocalId() {
		// TODO Auto-generated method stub
		
		return "quiz 4";
	}
	
	@Override
	public Map<String, String> getPreviewMap() {
		
		HashMap<String,String> previewMap = new HashMap<String,String>();
		previewMap.put(Listable.PREVIEW_TITLE, this.getName());
		previewMap.put(Listable.PREVIEW_SUBTITLE, "CSE 1234");
		previewMap.put(Listable.PREVIEW_SECOND_LINE, this.getDate());
		previewMap.put(Listable.Detailed.PREVIEW_THIRD_LINE, this.getLocation());
		previewMap.put(Listable.Detailed.PREVIEW_FOURTH_LINE, "");
		return previewMap;
	}
}
