package edu.uta.mysyllabi.backend;

import edu.uta.mysyllabi.core.Course;

public class CloudDataHelper {
	
	public static String saveCourse(Course course) {
		// TODO implement method
		return null;
	}
	
	public static Course getCourse(String key) {
		Course cloudCourse = new Course(null, null);
		// TODO implement method
		return cloudCourse;
	}
	
	public static String[] getSchoolList(String state) {
		if (state.equals(new String("Texas"))) {
			String[] schools = {"University of Texas at Arlington", 
								"University of Texas at Dallas", 
								"Tarrant County Community College"};
			return schools;
		} else {
			String[] schools = {"Not a Cool One"};
			return schools;
		}
	}

}
