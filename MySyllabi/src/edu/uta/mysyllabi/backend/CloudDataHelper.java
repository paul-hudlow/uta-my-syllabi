package edu.uta.mysyllabi.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.os.AsyncTask;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.core.Course;

public class CloudDataHelper {
	
	private static final String COURSE_TABLE = "Course";
	
	public CloudDataHelper() {
		Parse.initialize(MySyllabi.getAppContext(), 
				"tDHQuyM07LOHaIEzPJPrMP2EKCC0j3ik6mmTQ9Xp",
				"ZtXxrCSSp90ca4pmWGbLGanXEarRFR6BtPIwSVXM");
	}
	
	/* Creates new course on the cloud or updates an existing course. */
	public void saveCourse(Course course) {
		if (course.getCloudId() == null) { // Check whether a cloud ID is available.
			if (course.isOnCloud()) { // Check if there should be a cloud ID.
				/* Update course object from local database. */
				LocalDataHelper reCheckHelper = new LocalDataHelper();
				Course refreshedCourse = reCheckHelper.getCourse(course.getLocalId());
				if (refreshedCourse.getCloudId() != null) {
					saveCourse(refreshedCourse);
				}
				return;
			}
			new CourseSaver().execute(course); // Create new course on cloud with custom save behavior.
		} else {
			/* Update course on cloud with Parse-provided method. */
			ParseObject cloudCourse = courseToParse(course);
			cloudCourse.saveInBackground();
		}
	}
	
	/* Saves a new course to the cloud in a background thread. This is necessary in order to
	 * return the cloud ID to the local database afterwards. */
	protected class CourseSaver extends AsyncTask<Course, Void, Void> {
		
		@Override
		protected Void doInBackground(Course... params) {
			if (params.length != 1) {
				throw new IllegalArgumentException("Must have exactly one Course argument!");
			}
			Course course = params[0];
			
			/* Save the course to the cloud. */
			ParseObject cloudCourse = courseToParse(course);
			try {
				cloudCourse.save();
			} catch (ParseException e) {
				return null;
			}
			/* Save the automatically returned ID to the local database for later reference. */
		    LocalDataHelper localHelper = new LocalDataHelper();
		    localHelper.addCloudId(course.getLocalId(), cloudCourse.getObjectId()); 
		    
			return null;
		}
		
	}
	
	/* Convert a Course object to a ParseObject object. */
	private static ParseObject courseToParse(Course courseObject) {
		final ParseObject parseObject = new ParseObject(COURSE_TABLE);
		
		if (courseObject.getCloudId() != null) {
			parseObject.setObjectId(courseObject.getCloudId());
		}
		
		HashMap<String, String> courseMap = courseObject.getContentMap();
		String[] keys = Course.getContentKeys();
		String nextValue;
		for (int i = 0; i < keys.length; i++) {
			nextValue = courseMap.get(keys[i]);
			if (nextValue != null) {
				parseObject.put(keys[i], nextValue);
			}
		}
	    
	    return parseObject;
	}
	
	/* Convert a ParseObject object to a Course object. */
	private Course parseToCourse(ParseObject parseObject) {
		
		Set<String> keys = parseObject.keySet();
		
		/* Build a hash map with course contents. */
		HashMap<String, String> courseMap = new HashMap<String, String>();
		String nextValue;
		for (String nextKey : keys) {
			nextValue = parseObject.getString(nextKey);
			if (nextValue != null) {
				courseMap.put(nextKey, nextValue);
			}
		}
		
		/* Use hash map to create new Course object. */
	    Course courseObject = new Course(courseMap);
	    courseObject.setCloudId(parseObject.getObjectId());
		
	    return courseObject;
	}

	public static Course getCourse(String key) {
		Course cloudCourse = new Course(null, null);
		// TODO implement method
		return cloudCourse;
	}
	
	/* Dummy method. */
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
	
	public ArrayList<Course> getCourseList(String schoolName, String semester, 
			String courseName, String courseSection) {
		
		/* Set up ParseQuery object. */
		ParseQuery<ParseObject> courseQuery = ParseQuery.getQuery(COURSE_TABLE);
			
		if (schoolName == null || courseName == null) {
			return new ArrayList<Course>();
		}

		/* Set query parameters. */
		courseQuery.whereEqualTo(Course.COURSE_SCHOOL, schoolName);
		courseQuery.whereEqualTo(Course.COURSE_SEMESTER, semester);
		courseQuery.whereStartsWith(Course.COURSE_NAME, courseName);

		if (courseSection != null) {
			courseQuery.whereEqualTo(Course.COURSE_SECTION, courseSection);
		}
		
		/* Send query to Parse. */
		List<ParseObject> parseList;
		try {
			parseList = courseQuery.find();
		} catch (ParseException e) {
			e.printStackTrace();
			return new ArrayList<Course>();
		}
				
		/* Populate list by converting ParseObject objects to Course objects. */
		ArrayList<Course> courseList = new ArrayList<Course>();
		for (ParseObject parseCourse : parseList) {
			courseList.add(parseToCourse(parseCourse));
		}
		return courseList;
	}
	
}