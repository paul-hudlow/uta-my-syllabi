package edu.uta.mysyllabi.core;

import java.util.ArrayList;
import java.util.Random;

import edu.uta.mysyllabi.datatypes.Instructor;
import edu.uta.mysyllabi.datatypes.SchoolSemester;
import edu.uta.mysyllabi.datatypes.WeeklyMeeting;

import android.text.format.Time;

public class Course {
	private final String localId;
	private String cloudId;
	public SchoolSemester semester;
	public String name;
	public String title;
	public WeeklyMeeting meeting;
	public Instructor instructor;
	public ArrayList<Instructor> teachingAssistantList;
	public ArrayList<String> websiteList;
	
	public Course() {
		super();
		Time time = new Time();
		Random random = new Random(time.second);
		localId = Integer.toString(random.nextInt()%10000); // Dummy random id.
	}
	
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
	
	public void addCloudId(String cloudId) throws IllegalStateException {
		if (this.cloudId == null) {
			this.cloudId = cloudId;
		} else {
			throw new IllegalStateException("Course already has a cloud id!");
		}
	}
}
