package edu.uta.mysyllabi.core;

import java.util.ArrayList;

import edu.uta.mysyllabi.backend.CloudDataHelper;
import edu.uta.mysyllabi.backend.LocalDataHelper;
import edu.uta.mysyllabi.datatypes.Instructor;
import edu.uta.mysyllabi.datatypes.WeeklyMeeting;

public class Controller {
	
	public void addCourse(String cloudId) {
		Course cloudCourse = CloudDataHelper.getCourse(cloudId);
		LocalDataHelper localData = new LocalDataHelper();
		localData.saveCourse(cloudCourse);
	}
	
	public void updateCourse(Course course) {
		LocalDataHelper localData = new LocalDataHelper();
		CloudDataHelper cloudData = new CloudDataHelper();
		cloudData.saveCourse(course);
		localData.saveCourse(course);
	}
	
	public String createCourse(Course course, boolean onCloud) {
		LocalDataHelper localData = new LocalDataHelper();
		String localId = localData.saveCourse(course);
		course.setLocalId(localId);
		
		CloudDataHelper cloudHelper = new CloudDataHelper();
		if (onCloud) {
			cloudHelper.saveCourse(course);
		}
		
		return localId;
	}
	
	public Course getCourse(String localId) {
		LocalDataHelper localData = new LocalDataHelper();
		return localData.getCourse(localId);
	}
	
	public ArrayList<String> getCourseList() {
		LocalDataHelper localData = new LocalDataHelper();
		return localData.getCourseKeys();
	}
	
	public void deleteCourse(String localId) {
		LocalDataHelper localData = new LocalDataHelper();
		localData.deleteCourse(localId);
	}
	
	public String[] getSchools(String state) {
		return CloudDataHelper.getSchoolList(state);
	}
	
	public ArrayList<Course> findCourses(String courseName, String courseSection, 
			String school, String semester) {
		if (courseName.length() < 1) {
			return new ArrayList<Course>();
		}
		if (courseSection.length() < 1) {
			courseSection = null;
		}
		CloudDataHelper cloudHelper = new CloudDataHelper();
		ArrayList<Course> courseList = cloudHelper.getCourseList(school, semester, courseName, courseSection);
		
		
		/*Course nextCourse;
		
		nextCourse = new Course(null,null);
		nextCourse.setName("CSE 5324");
		nextCourse.setInstructor(new Instructor("Jeff", "Lei"));
		nextCourse.setMeeting(new WeeklyMeeting(60*11, 80, "nynynnn", "ERB 129"));
		nextCourse.setSection("002");
		courseList.add(nextCourse);
		
		nextCourse = new Course(null,null);
		nextCourse.setName("CSE 5320");
		nextCourse.setInstructor(new Instructor("Dennis", "Frailey"));
		nextCourse.setMeeting(new WeeklyMeeting(60*13, 180, "nnnnynn", "WH 209"));
		nextCourse.setSection("001");
		courseList.add(nextCourse);
		
		nextCourse = new Course(null,null);
		nextCourse.setName("CSE 2311");
		nextCourse.setInstructor(new Instructor("Bob", "Weems"));
		nextCourse.setMeeting(new WeeklyMeeting(60*10, 50, "ynynynn", "NH 106"));
		nextCourse.setSection("001");
		courseList.add(nextCourse);
		
		nextCourse = new Course(null,null);
		nextCourse.setName("CSE 5324");
		nextCourse.setInstructor(new Instructor("Jeff", "Lei"));
		nextCourse.setMeeting(new WeeklyMeeting(60*11, 80, "nynynnn", "ERB 129"));
		nextCourse.setSection("002");
		courseList.add(nextCourse);
		
		nextCourse = new Course(null,null);
		nextCourse.setName("CSE 5320");
		nextCourse.setInstructor(new Instructor("Dennis", "Frailey"));
		nextCourse.setMeeting(new WeeklyMeeting(60*13, 180, "nnnnynn", "WH 209"));
		nextCourse.setSection("001");
		courseList.add(nextCourse);
		
		nextCourse = new Course(null,null);
		nextCourse.setName("CSE 2311");
		nextCourse.setInstructor(new Instructor("Bob", "Weems"));
		nextCourse.setMeeting(new WeeklyMeeting(60*10, 50, "ynynynn", "NH 106"));
		nextCourse.setSection("001");
		courseList.add(nextCourse);*/
		
		return courseList;
	}
}