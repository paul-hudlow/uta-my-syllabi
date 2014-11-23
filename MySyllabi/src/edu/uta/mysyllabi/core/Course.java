package edu.uta.mysyllabi.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Course implements Mappable, Listable {
	private String localId;
	private String cloudId;
	private String nameLetters;
	private String nameNumber;
	private boolean nameValidity = false;
	private String title;
	private String section;
	private String school;
	private String website;
	private SchoolSemester semester = SchoolSemester.getCurrent();
	private WeeklyMeeting meeting = new WeeklyMeeting();
	private Instructor instructor = new Instructor();
	private Instructor teachingAssistant = new Instructor();
	private boolean locked = false;
	private long timeUpdated = 0L;
	private List<Event> eventList;
	
	public static final String MAP_KEY_NAME = "name";
	public static final String MAP_KEY_SECTION = "section";
	public static final String MAP_KEY_INSTRUCTOR = "instructor";
	public static final String MAP_KEY_MEETING = "meeting";
	
	public static final String COURSE_NAME = "name";
	public static final String COURSE_SECTION = "section";
	public static final String COURSE_SCHOOL = "school";
	public static final String COURSE_SEMESTER = "semester";
	private final String COURSE_TITLE = "title";
	private final String COURSE_WEBSITE = "website";
	
	private final String INSTRUCTOR_PREFIX = "instructor_";
	private final String TEACHING_ASSISTANT_PREFIX = "ta_";
	private final String MEETING_PREFIX = "meeting_";
	
	private final String[] contentKeys = {
		COURSE_NAME,
		COURSE_SECTION,
		COURSE_TITLE,
		COURSE_SCHOOL,
		COURSE_SEMESTER,
		COURSE_WEBSITE
	};
	
	public Course(String localId, String cloudId) {
		this.localId = localId;
		this.cloudId = cloudId;
	}
	
	public void addContent(Map<String, String> map) {
		setName(map.get(COURSE_NAME));
		setSection(map.get(COURSE_SECTION));
		setTitle(map.get(COURSE_TITLE));
		setSchool(map.get(COURSE_SCHOOL));
		setWebsite(map.get(COURSE_WEBSITE));
		setSemester(map.get(COURSE_SEMESTER));
		
		this.instructor.addContent(map, INSTRUCTOR_PREFIX);
		this.teachingAssistant.addContent(map, TEACHING_ASSISTANT_PREFIX);
		this.meeting.addContent(map, MEETING_PREFIX);
	}

	public Map<String, String> getContentMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(COURSE_NAME, this.getName());
		map.put(COURSE_TITLE, this.title);
		map.put(COURSE_SECTION, this.section);
		map.put(COURSE_SCHOOL, this.school);
		map.put(COURSE_WEBSITE, this.website);
		map.put(COURSE_SEMESTER, this.semester.toString());

		map.putAll(instructor.getContentMap(INSTRUCTOR_PREFIX));
		map.putAll(teachingAssistant.getContentMap(TEACHING_ASSISTANT_PREFIX));
		map.putAll(meeting.getContentMap(MEETING_PREFIX));
		
		return map;
	}
	
	public Map<String, String> getDifferenceMap(Course anotherCourse) {
		Map<String, String> nativeMap = this.getContentMap();
		Map<String, String> foreignMap = anotherCourse.getContentMap();
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
	
	public List<String> getContentKeys() {
		LinkedList<String> keyList = new LinkedList<String>();
		for (int i = 0; i < contentKeys.length; i++) {
			keyList.add(contentKeys[i]);
		}
		keyList.addAll(this.meeting.getContentKeys(MEETING_PREFIX));
		keyList.addAll(this.instructor.getContentKeys(INSTRUCTOR_PREFIX));
		return keyList;
	}
	
	public boolean sharesContents(Course another) {
		return this.getContentMap().equals(another.getContentMap());
	}
	
	public HashMap<String,String> getPreviewMap() {
		HashMap<String,String> previewMap = new HashMap<String,String>();
		previewMap.put(MAP_KEY_NAME, this.getName());
		previewMap.put(MAP_KEY_SECTION, this.section);
		previewMap.put(MAP_KEY_INSTRUCTOR, this.instructor.getName());
		previewMap.put(MAP_KEY_MEETING, this.meeting.getOccurrence());
		return previewMap;
	}
	
	public void setEvents(List<Event> events) {
		this.eventList = events;		
	}
	
	public List<Event> getEvents() {
		return this.eventList;
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
	
	public String getName() {
		return nameLetters + " " + nameNumber;
	}
	
	public void setName(String name) {
		if (name == null) {
			return;
		}
		this.nameValidity = true;
		name = name.replace(" ", "");
		StringBuilder letters = new StringBuilder();
		int i = 0;
		while (i < name.length() && Character.isLetter(name.charAt(i))) {
			letters.append(name.charAt(i));
			i++;
		}
		if (i == name.length() || i == 0) {
			this.nameValidity = false;
		}
		this.nameLetters = letters.toString();
		
		StringBuilder number = new StringBuilder();
		while (i < name.length() && Character.isDigit(name.charAt(i))) {
			number.append(name.charAt(i));
			i++;
		}
		if (i < name.length()) {
			this.nameValidity = false;
		}
		this.nameNumber = number.toString();
	}
	
	public boolean nameIsValid() {
		return nameValidity;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		if (title != null) {
			this.title = title;
		}
	}
	
	public String getSection() {
		return section;
	}
	
	public void setSection(String section) {
		if (section != null) {
			this.section = section;
		}
	}
	
	public String getSchool() {
		return this.school;
	}
	
	public void setSchool(String school) {
		if (school != null) {
			this.school = school;
		}
	}
	
	public SchoolSemester getSemester() {
		return this.semester;
	}
	
	public void setSemester(SchoolSemester semester) {
		this.semester = semester;
	}
	
	public void setSemester(String semester) {
		if (semester != null) {
			this.semester = new SchoolSemester(semester);
		}
	}
	
	public void setWebsite(String website) {
		if (website != null) {
			this.website = website;
		}
	}
	
	public Instructor getInstructor() {
		return instructor;
	}
	
	public void setInstructor(Instructor instructor) {
		this.instructor = instructor;
	}
	
	public Instructor getTeachingAssistant() {
		return teachingAssistant;
	}
	
	public void setTeachingAssistant(Instructor teachingAssistant) {
		this.teachingAssistant = teachingAssistant;
	}
	
	public String getClassroom() {
		if (meeting == null) {
			return null;
		}
		return meeting.getLocation();
	}
	
	public void setClassroom(String classroom) {
		meeting.setLocation(classroom);
	}
	
	public WeeklyMeeting getMeeting() {
		return meeting;
	}
	
	public void setMeeting(WeeklyMeeting meeting) {
		this.meeting = meeting;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;	
	}
	
}