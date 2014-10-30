package edu.uta.mysyllabi.core;

import java.util.HashMap;

import edu.uta.mysyllabi.datatypes.Instructor;
import edu.uta.mysyllabi.datatypes.SchoolSemester;
import edu.uta.mysyllabi.datatypes.TimeOfDay;
import edu.uta.mysyllabi.datatypes.WeeklyMeeting;

public class Course {
	private String localId;
	private String cloudId;
	private String name;
	private String title;
	private String section;
	private String school;
	private SchoolSemester semester;
	private WeeklyMeeting meeting = new WeeklyMeeting();
	private Instructor instructor = new Instructor();
	//private ArrayList<Instructor> teachingAssistantList;
	//private ArrayList<String> websiteList;
	
	public static final String MAP_KEY_NAME = "name";
	public static final String MAP_KEY_SECTION = "section";
	public static final String MAP_KEY_INSTRUCTOR = "instructor";
	public static final String MAP_KEY_MEETING = "meeting";
	
	public static final String COURSE_NAME = "name";
	public static final String COURSE_SECTION = "section";
	public static final String COURSE_TITLE = "title";
	public static final String COURSE_SCHOOL = "school";
	public static final String COURSE_SEMESTER = "semester";
	
	public static final String INSTRUCTOR_FIRST_NAME = "first_name";
	public static final String INSTRUCTOR_LAST_NAME = "last_name";
	public static final String INSTRUCTOR_EMAIL = "email";
	public static final String INSTRUCTOR_PHONE = "phone";
	public static final String INSTRUCTOR_OFFICE = "office";
	
	public static final String MEETING_LOCATION = "location";
	public static final String MEETING_START = "meeting_start";
	public static final String MEETING_DURATION = "meeting_duration";
	public static final String MEETING_DAYS = "meeting_days";
	
	public Course(String localId, String cloudId) {
		this.localId = localId;
		this.cloudId = cloudId;
	}
	
	public Course(HashMap<String, String> map) {
		this.name = map.get(COURSE_NAME);
		this.section = map.get(COURSE_SECTION);
		this.title = map.get(COURSE_TITLE);
		this.school = map.get(COURSE_SCHOOL);
		this.semester = new SchoolSemester(map.get(COURSE_SEMESTER));
		
		this.instructor.setFirstName(map.get(INSTRUCTOR_FIRST_NAME));
		this.instructor.setLastName(map.get(INSTRUCTOR_LAST_NAME));
		this.instructor.setEmailAddress(map.get(INSTRUCTOR_EMAIL));
		this.instructor.setPhoneNumber(map.get(INSTRUCTOR_PHONE));
		this.instructor.setOfficeId(map.get(INSTRUCTOR_OFFICE));
		
		try {
			this.meeting.setStartTime(Integer.parseInt(map.get(MEETING_START)));
			this.meeting.setDuration(Integer.parseInt(map.get(MEETING_DURATION)));
			this.meeting.setDaysOfWeek(map.get(MEETING_DAYS));
			this.meeting.setLocation(map.get(MEETING_LOCATION));
		} catch (NumberFormatException exception) {
			// Ignore meeting.
		}
	}
	
	public String getLocalId() {
		return this.localId;
	}
	
	public String getCloudId() {
		return this.cloudId;
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
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSection() {
		return section;
	}
	
	public void setSection(String section) {
		this.section = section;
	}
	
	public String getSchool() {
		return this.school;
	}
	
	public void setSchool(String school) {
		this.school = school;
	}
	
	public SchoolSemester getSemester() {
		return this.semester;
	}
	
	public void setSemester(SchoolSemester semester) {
		this.semester = semester;
	}
	
	public void setSemester(String semester) {
		this.semester = new SchoolSemester(semester);
	}
	
	public Instructor getInstructor() {
		return instructor;
	}
	
	public void setInstructor(Instructor instructor) {
		this.instructor = instructor;
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
	
	public TimeOfDay getMeetingStart() {
		if (meeting == null || meeting.getStartTime() == null) {
			return null;
		}
		return meeting.getStartTime();
	}
	
	public TimeOfDay getMeetingDuration() {
		if (meeting == null) {
			return null;
		}
		return new TimeOfDay(meeting.getDuration());
	}
	
	public String getMeetingDays() {
		if (meeting == null) {
			return null;
		}
		return meeting.getDaysOfWeek();
	}
	
	public WeeklyMeeting getMeeting() {
		return meeting;
	}
	
	public void setMeeting(WeeklyMeeting meeting) {
		this.meeting = meeting;
	}
	
	public HashMap<String,String> getPreviewMap() {
		HashMap<String,String> previewMap = new HashMap<String,String>();
		previewMap.put(MAP_KEY_NAME, this.name);
		previewMap.put(MAP_KEY_SECTION, this.section);
		previewMap.put(MAP_KEY_INSTRUCTOR, this.instructor.getName());
		previewMap.put(MAP_KEY_MEETING, this.meeting.getOccurence());
		return previewMap;
	}

	public String[] getContentKeys() {
		return new String[] {
				COURSE_NAME,
				COURSE_TITLE,
				COURSE_SECTION,
				COURSE_SCHOOL,
				COURSE_SEMESTER,
				INSTRUCTOR_FIRST_NAME,
				INSTRUCTOR_LAST_NAME,
				INSTRUCTOR_PHONE,
				INSTRUCTOR_EMAIL,
				INSTRUCTOR_OFFICE,
				MEETING_START,
				MEETING_DURATION,
				MEETING_DAYS,
				MEETING_LOCATION
		};
	}

	public HashMap<String, String> getContentMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(COURSE_NAME, this.name);
		map.put(COURSE_TITLE, this.title);
		map.put(COURSE_SECTION, this.section);
		map.put(COURSE_SCHOOL, this.school);
		map.put(COURSE_SEMESTER, this.semester.toString());
		
		map.put(INSTRUCTOR_FIRST_NAME, this.instructor.getFirstName());
		map.put(INSTRUCTOR_LAST_NAME, this.instructor.getLastName());
		map.put(INSTRUCTOR_EMAIL, this.instructor.getEmailAddress());
		map.put(INSTRUCTOR_PHONE, this.instructor.getPhoneNumber());
		map.put(INSTRUCTOR_OFFICE, this.instructor.getOfficeId());
		
		if (this.meeting.getStartTime() != null) {
			map.put(MEETING_START, Integer.toString(this.meeting.getStartTime().getTotalMinutes()));
		}
		map.put(MEETING_DURATION, Integer.toString(this.meeting.getDuration()));
		map.put(MEETING_DAYS, this.meeting.getDaysOfWeek());
		map.put(MEETING_LOCATION, this.meeting.getLocation());
		
		return map;
	}
	
}
