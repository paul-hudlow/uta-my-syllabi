package edu.uta.mysyllabi.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.uta.mysyllabi.backend.DataContainer;
import edu.uta.mysyllabi.frontend.TimeHolder;

public class Event extends DataContainer implements Listable.Detailed, TimeHolder {
	Date date = new Date();
	private String location;
	private String name;
	//private String gradeCategory;
	private String courseName;
	
	private static final String DATE = "date";
	private static final String LOCATION = "location";
	private static final String NAME = "name";
	
	private static final String[] contentKeys = {
			DATE,
			LOCATION,
			NAME
	};
	
	public Event(String localId, String cloudId) {
		super(localId, cloudId);
	}
	
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
		Calendar newCalendar = Calendar.getInstance();
		newCalendar.setTime(date);
		Calendar oldCalendar = Calendar.getInstance();
		oldCalendar.setTime(this.date);
		oldCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
		this.date = oldCalendar.getTime();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("E, MMM dd", Locale.US);
		return format.format(date);
	}
	
	@Override
	public boolean sharesContents(Mappable another) {
		return this.getContentMap().equals(another.getContentMap());
	}
	
	@Override
	public Map<String, String> getPreviewMap() {
		
		HashMap<String,String> previewMap = new HashMap<String,String>();
		previewMap.put(Listable.PREVIEW_TITLE, this.getName());
		previewMap.put(Listable.PREVIEW_SUBTITLE, this.getCourseName());
		previewMap.put(Listable.PREVIEW_SECOND_LINE, this.getDate() + ", " + new TimeOfDay(date).toString(false));
		previewMap.put(Listable.Detailed.PREVIEW_THIRD_LINE, this.getLocation());
		previewMap.put(Listable.Detailed.PREVIEW_FOURTH_LINE, "");
		return previewMap;
	}

	@Override
	public void setStartTime(TimeOfDay time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR),
					 calendar.get(Calendar.MONTH),
					 calendar.get(Calendar.DAY_OF_MONTH),
					 time.getHour(), time.getMinute());
		date = calendar.getTime();
	}

	@Override
	public void setEndTime(TimeOfDay time) {
		setStartTime(time);
	}

	@Override
	public TimeOfDay getStartTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return new TimeOfDay(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
	}

	@Override
	public TimeOfDay getEndTime() {
		return getStartTime();
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

}
