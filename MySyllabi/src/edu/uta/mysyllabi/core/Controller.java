package edu.uta.mysyllabi.core;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import edu.uta.mysyllabi.backend.*;

public class Controller {
	
	public interface CallBack {
		
		public void onPostExecute();
		
	}
	
	private LocalDataHelper localHelper = new LocalDataHelper();
	private CloudDataHelper cloudHelper = new CloudDataHelper();
	private SynchronizationHelper syncHelper = new SynchronizationHelper();
	private Synchronizer synchronizer;
	
	public String getLatestSchool() {
		return localHelper.getLatestSchool();
	}
	
	//public void addCourse(String cloudId) {
	//	CloudDataHelper cloudHelper = new CloudDataHelper();
	//	Course cloudCourse = cloudHelper.getCourse(cloudId);
	//	LocalDataHelper localData = new LocalDataHelper();
	//	localData.saveCourse(cloudCourse);
	//}
	
	public void synchronize(CallBack callBack) {
		if (synchronizer == null || synchronizer.getStatus() == AsyncTask.Status.FINISHED) {
			synchronizer = new Synchronizer(callBack);
			synchronizer.execute();
		}
	}
	
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
	
	public void updateCourse(Course course) {
		localHelper.saveFromLocal(course);
		synchronize(null);
		/*if (!course.isLocked()) {
			if (course.getCloudId() == null) {
				course.setCloudId(localData.getCloudId(course.getLocalId()));
			}
			if (course.getCloudId() == null) {
				return;
			}
			CloudDataHelper cloudData = new CloudDataHelper();
			cloudData.updateCourse(course);
		}*/
	}
	
	public String createCourse(Course course) {
		String localId = localHelper.createCourse(course);
		synchronize(null);
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
}