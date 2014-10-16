package edu.uta.mysyllabi.core;

import java.util.ArrayList;

import edu.uta.mysyllabi.backend.*;
import edu.uta.mysyllabi.datatypes.Instructor;
import edu.uta.mysyllabi.datatypes.WeeklyMeeting;

public class Controller {
	
	public void addCourse(String cloudId) {
		Course cloudCourse = CloudDataHelper.getCourse(cloudId);
		LocalDataHelper.saveCourse(cloudCourse);
	}
	
	public void createCourse(Course course, boolean onCloud) {
		if (onCloud) {
			String cloudId = CloudDataHelper.saveCourse(course);
			course.setCloudId(cloudId);
		}
		LocalDataHelper.saveCourse(course);
	}
	
	public Course getCourse(String localId) {
		return LocalDataHelper.getCourse(localId);
	}
	
	public String[] getSchools(String state) {
		return CloudDataHelper.getSchoolList(state);
	}
	
	public ArrayList<Course> findCourses(String courseId, String courseSection, String school, String semester) {
		
		/* Create a dummy list. */
		ArrayList<Course> courseList = new ArrayList<Course>();
		Course nextCourse;
		
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
		courseList.add(nextCourse);
		
		return courseList;
	}
}