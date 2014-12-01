package edu.uta.mysyllabi.core;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import edu.uta.mysyllabi.backend.*;

public class Controller {
	
	private LocalDataHelper localHelper = new LocalDataHelper();
	private CloudDataHelper cloudHelper = new CloudDataHelper();
	private SynchronizationHelper syncHelper = new SynchronizationHelper();
	private Synchronizer synchronizer;
	
	/* The CallBack interface provides the simplest possible callback functionality. */
	public interface CallBack {
		public void onPostExecute();
	}
	
	/* Retrieves the latest school selection from the local database. */
	public String getLatestSchool() {
		return localHelper.getLatestSchool();
	}
	
	/* Starts a synchronization with the cloud only if one it not already in progress. */
	public void synchronize(CallBack callBack) {
		if (synchronizer == null || synchronizer.getStatus() == AsyncTask.Status.FINISHED) {
			synchronizer = new Synchronizer(callBack);
			synchronizer.execute();
		}
	}
	
	public List<Course> getAllCourses() {
		return localHelper.getAllCourses();
	}
	
	/* The Synchronizer class executes the cloud synchronization code in a background thread
	 * and then executes the CallBack if one is provided. */
	protected class Synchronizer extends AsyncTask<Void, Void, Void> {

		private CallBack callBack;
		
		public Synchronizer(CallBack callBack) {
			this.callBack = callBack;
		}
		
		@Override
		protected Void doInBackground(Void... argument) {
			syncHelper.synchronize();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void argument) {
			if (callBack != null) {
				callBack.onPostExecute();
			}
		}
		
	}
	
	public void saveEvent(Event event, String courseId) {
		if (event.getLocalId() != null) {
			localHelper.saveFromLocal(event, courseId);
		} else {
			localHelper.createEvent(event, courseId);
		}
	}
	
	/* Saves new course data to the local database and initiates a synchronization. */
	public void updateCourse(Course course) {
		localHelper.saveFromLocal(course);
		synchronize(null);
	}
	
	/* Creates a new course in the local database and initiates a synchronization. */
	public String createCourse(Course course) {
		String localId = localHelper.createCourse(course);
		synchronize(null);
		return localId;
	}
	
	/* Retrieves a Course object from the local database. */
	public Course getCourse(String localId) {
		LocalDataHelper localData = new LocalDataHelper();
		return localData.getCourse(localId);
	}
	
	/* Retrieves all course IDs from the local database. */
	public List<String> getCourseList() {
		LocalDataHelper localData = new LocalDataHelper();
		return localData.getCourseKeys();
	}
	
	public Course getObsoleteCourse(String localId) {
		return localHelper.getObsoleteCourse(localId);
	}
	
	public List<Course> getUpdatedCourses() {
		return localHelper.getAllUpdatedCourses();
	}
	
	public void deleteCourse(String localId) {
		localHelper.deleteCourse(localId);
	}
	
	public String[] getSchools(String state) {
		return cloudHelper.getSchoolList(state);
	}
	
	public ArrayList<Course> findCourses(String courseName, String courseSection, 
			String school, String semester) {
		if (courseName.length() < 1) {
			return new ArrayList<Course>();
		}
		if (courseSection.length() < 1) {
			courseSection = null;
		}
		ArrayList<Course> courseList = cloudHelper.getCourseList(school, semester, courseName, courseSection);
		
		return courseList;
	}

	public List<Event> getEvents(String courseId) {
		Course course = localHelper.getCourse(courseId);
		return course.getEvents();
	}
	
	public List<Event> getAllEvents() {
		List<Event> allEvents = new ArrayList<Event>();
		List<Course> courseList = getAllCourses();
		List<Event> courseEvents;
		for (Course nextCourse : courseList) {
			courseEvents = nextCourse.getEvents();
			if (courseEvents != null) {
				allEvents.addAll(courseEvents);
			}
		}
		return allEvents;
		
	}

	public Event getEvent(String localId) {
		return localHelper.getEvent(localId);
	}

	public Event getObsoleteEvent(String localId) {
		return localHelper.getObsoleteEvent(localId);
	}
}