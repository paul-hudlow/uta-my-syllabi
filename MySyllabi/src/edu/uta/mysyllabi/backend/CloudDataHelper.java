package edu.uta.mysyllabi.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.core.Event;

public class CloudDataHelper {
	
	private static final String COURSE_TABLE = "course_table";
	private static final String EVENT_TABLE = "event_table";
	private static final String COURSE_ID = "course_id";
	
	public CloudDataHelper() {
		Parse.initialize(MySyllabi.getAppContext(), 
				"tDHQuyM07LOHaIEzPJPrMP2EKCC0j3ik6mmTQ9Xp",
				"ZtXxrCSSp90ca4pmWGbLGanXEarRFR6BtPIwSVXM");
	}
	
	public void updateCourse(Course course) throws ParseException {
		if (course.getCloudId() == null) { // Check whether a cloud ID is available.
			throw new IllegalArgumentException("Course object is missing cloud ID!");
		}
		/* Update course on cloud with Parse-provided method. */
		ParseObject cloudCourse = containerToParse(course, COURSE_TABLE);
		cloudCourse.save();
		course.setUpdateTime(cloudCourse.getUpdatedAt().getTime());
	}
	
	public void updateEvent(Event event) throws ParseException {
		if (event.getCloudId() == null) { // Check whether a cloud ID is available.
			throw new IllegalArgumentException("Event object is missing cloud ID!");
		}
		/* Update course on cloud with Parse-provided method. */
		ParseObject cloudCourse = containerToParse(event, EVENT_TABLE);
		cloudCourse.save();
		event.setUpdateTime(cloudCourse.getUpdatedAt().getTime());
	}
	
	// Create new course on cloud with custom save behavior.
	public Course createCourse(Course course) throws ParseException {
		/* Save the course to the cloud. */
		ParseObject cloudCourse = containerToParse(course, COURSE_TABLE);
		cloudCourse.save();
		course.setCloudId(cloudCourse.getObjectId());
		course.setUpdateTime(cloudCourse.getUpdatedAt().getTime());
		return course;
	}

	// Create new course on cloud with custom save behavior.
	public Event createEvent(Event event, Course course) throws ParseException {
		/* Save the course to the cloud. */
		ParseObject cloudCourse = containerToParse(event, EVENT_TABLE);
		cloudCourse.put(COURSE_ID, course.getCloudId());
		cloudCourse.save();
		event.setCloudId(cloudCourse.getObjectId());
		event.setUpdateTime(cloudCourse.getUpdatedAt().getTime());
		return event;
	}
	
	/* Convert a Course object to a ParseObject object. */
	private static ParseObject containerToParse(DataContainer containerObject, String tableName) {
		final ParseObject parseObject = new ParseObject(tableName);
		
		if (containerObject.getCloudId() != null) {
			parseObject.setObjectId(containerObject.getCloudId());
		}
		
		Map<String, String> courseMap = containerObject.getContentMap();
		List<String> keys = containerObject.getContentKeys();
		String nextValue;
		for (String nextKey : keys) {
			nextValue = courseMap.get(nextKey);
			if (nextValue != null) {
				parseObject.put(nextKey, nextValue);
			}
		}
	    
	    return parseObject;
	}
	
	private Map<String, String> getMap(ParseObject parseObject) {
		Set<String> keys = parseObject.keySet();
		
		/* Build a hash map with course contents. */
		HashMap<String, String> map = new HashMap<String, String>();
		String nextValue;
		for (String nextKey : keys) {
			nextValue = parseObject.getString(nextKey);
			if (nextValue != null) {
				map.put(nextKey, nextValue);
			}
		}
		return map;
	}
	
	/* Convert a ParseObject object to a Course object. */
	private Course parseToCourse(ParseObject parseObject) {
		
		/* Use map to create new Course object. */
	    Course courseObject = new Course(null, parseObject.getObjectId());
	    courseObject.setUpdateTime(parseObject.getUpdatedAt().getTime());
	    courseObject.addContent(getMap(parseObject));
		
	    return courseObject;
	}
	
	/* Convert a ParseObject object to an Event object. */
	private Event parseToEvent(ParseObject parseObject) {
		
		/* Use map to create new Event object. */
	    Event eventObject = new Event(null, parseObject.getObjectId());
	    eventObject.setUpdateTime(parseObject.getUpdatedAt().getTime());
	    eventObject.addContent(getMap(parseObject));
		
	    return eventObject;
	}

	public Course getCourse(String cloudId) throws ParseException {
		ParseObject parseCourse = new ParseObject(COURSE_TABLE);
		parseCourse.setObjectId(cloudId);
		parseCourse.fetch();
		Course course = parseToCourse(parseCourse);
		course.setEvents(getEvents(cloudId));
		return course;
	}
	
	public Event getEvent(String cloudId) throws ParseException {
		ParseObject parseCourse = new ParseObject(EVENT_TABLE);
		parseCourse.setObjectId(cloudId);
		parseCourse.fetch();
		return parseToEvent(parseCourse);
	}
	
	public long getUpdateTime(String cloudId) throws ParseException {
		/* Set up ParseQuery object. */
		ParseQuery<ParseObject> courseQuery = ParseQuery.getQuery(COURSE_TABLE);

		/* Set query parameters. */
		ArrayList<String> empty = new ArrayList<String>();
		courseQuery.selectKeys(empty);
		ParseObject emptyCourse;
		emptyCourse = courseQuery.get(cloudId);
		return emptyCourse.getUpdatedAt().getTime();
	}
	
	/* Dummy method. */
	public String[] getSchoolList(String state) {
		if (state.equals(new String("Texas"))) {
			String[] schools = {"University of Texas at Dallas", 
								"University of Texas at Arlington", 
								"Tarrant County Community College"};
			return schools;
		} else if (state.equals(new String("Alabama"))) {
			String[] schools = {"Amridge University", 
								"Alabama State University", 
								"Alabama Agricultural and Mechanical University"};
			return schools;			
		} else {
			String[] schools = {"Other"};
			return schools;
		}
	}
	
	public ArrayList<Event> getEvents(String courseId) {
		
		/* Set up ParseQuery object. */
		ParseQuery<ParseObject> courseQuery = ParseQuery.getQuery(EVENT_TABLE);

		/* Set query parameters. */
		courseQuery.whereEqualTo(COURSE_ID, courseId);
		
		/* Send query to Parse. */
		List<ParseObject> parseList;
		try {
			parseList = courseQuery.find();
		} catch (ParseException e) {
			e.printStackTrace();
			return new ArrayList<Event>();
		}
				
		/* Populate list by converting ParseObject objects to Course objects. */
		ArrayList<Event> eventList = new ArrayList<Event>();
		for (ParseObject parseEvent : parseList) {
			eventList.add(parseToEvent(parseEvent));
		}
		return eventList;
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