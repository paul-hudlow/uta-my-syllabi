package edu.uta.mysyllabi.backend;

import edu.uta.mysyllabi.core.Course;

public class CloudDataHelper {
	
	public static String saveCourse(Course course) {
		// TODO implement method
		return null;
	}
	
	public static Course getCourse(String key) {
		Course cloudCourse = new Course();
		// TODO implement method
		return cloudCourse;
	}
	
	public static String[] getSchoolList(String state) {
		String[] schools = {"University of Texas at Arlington", "University of Arkansas", "Tarrant County Community College"};
		return schools;
	}

}
