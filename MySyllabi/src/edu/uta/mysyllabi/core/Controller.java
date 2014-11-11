package edu.uta.mysyllabi.core;

import java.util.ArrayList;
import java.util.List;

import edu.uta.mysyllabi.backend.CloudDataHelper;
import edu.uta.mysyllabi.backend.LocalDataHelper;

public class Controller {
	
	public String getLatestSchool() {
		return new LocalDataHelper().getLatestSchool();
	}
	
	//public void addCourse(String cloudId) {
	//	CloudDataHelper cloudHelper = new CloudDataHelper();
	//	Course cloudCourse = cloudHelper.getCourse(cloudId);
	//	LocalDataHelper localData = new LocalDataHelper();
	//	localData.saveCourse(cloudCourse);
	//}
	
	public void updateCourse(Course course) {
		LocalDataHelper localData = new LocalDataHelper();
		localData.saveCourse(course, false);
		if (!course.isLocked()) {
			if (course.getCloudId() == null) {
				course.setCloudId(localData.getCloudId(course.getLocalId()));
			}
			if (course.getCloudId() == null) {
				return;
			}
			CloudDataHelper cloudData = new CloudDataHelper();
			cloudData.updateCourse(course);
		}
	}
	
	public String createCourse(Course course) {
		LocalDataHelper localData = new LocalDataHelper();
		String localId = localData.saveCourse(course, false);
		course.setLocalId(localId);
		
		if (!course.isLocked()) {
			CloudDataHelper cloudHelper = new CloudDataHelper();
			cloudHelper.createCourse(course);
		}
		
		return localId;
	}
	
	public Course getCourse(String localId) {
		LocalDataHelper localData = new LocalDataHelper();
		return localData.getCourse(localId);
	}
	
	public List<String> getCourseList() {
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
		
		return courseList;
	}
}