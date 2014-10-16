package edu.uta.mysyllabi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.uta.mysyllabi.datatypes.Instructor;
import edu.uta.mysyllabi.datatypes.SchoolSemester;
import edu.uta.mysyllabi.datatypes.TimeOfDay;
import edu.uta.mysyllabi.datatypes.WeeklyMeeting;

import android.text.format.Time;

public class Course {
	private String localId;
	private String cloudId;
	private String name;
	private String title;
	private String section;
	private String school;
	private SchoolSemester semester;
	private WeeklyMeeting meeting;
	private Instructor instructor;
	private ArrayList<Instructor> teachingAssistantList;
	private ArrayList<String> websiteList;
	
	public static final String MAP_KEY_NAME = "name";
	public static final String MAP_KEY_SECTION = "section";
	public static final String MAP_KEY_INSTRUCTOR = "instructor";
	public static final String MAP_KEY_MEETING = "meeting";
	
	public Course(String localId, String cloudId) {
		this.localId = localId;
		this.cloudId = cloudId;
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
	
	public String getMeetingStart() {
		if (meeting == null || meeting.getStartTime() == null) {
			return null;
		}
		return meeting.getStartTime().toString();
	}
	
	public int getMeetingDuration() {
		if (meeting == null) {
			return 0;
		}
		return meeting.getDuration();
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
}
