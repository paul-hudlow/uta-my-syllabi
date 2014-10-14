package edu.uta.mysyllabi.core;

import edu.uta.mysyllabi.backend.*;

public class Controller {
	
	public void addCourse(String cloudId) {
		Course cloudCourse = CloudDataHelper.getCourse(cloudId);
		LocalDataHelper.saveCourse(cloudCourse);
	}
	
	public void createCourse(Course course, boolean onCloud) {
		if (onCloud) {
			String cloudId = CloudDataHelper.saveCourse(course);
			course.addCloudId(cloudId);
		}
		LocalDataHelper.saveCourse(course);
	}
	
	public Course getCourse(String localId) {
		return LocalDataHelper.getCourse(localId);
	}
	
	public String[] getSchools(String state) {
		return CloudDataHelper.getSchoolList(state);
	}
	
}
