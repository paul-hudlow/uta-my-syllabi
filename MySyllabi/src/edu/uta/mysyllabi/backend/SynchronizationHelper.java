package edu.uta.mysyllabi.backend;

import java.util.List;

import android.os.AsyncTask;

import com.parse.ParseException;

import edu.uta.mysyllabi.core.Course;

public class SynchronizationHelper {
	
	private Synchronizer synchronizer = new Synchronizer();
	
	public void synchronize() {
		if (synchronizer.getStatus() == AsyncTask.Status.PENDING) {
			synchronizer.execute();
		} else if (synchronizer.getStatus() == AsyncTask.Status.FINISHED) {
			synchronizer = new Synchronizer();
			synchronizer.execute();
		}
	}
	
	protected class Synchronizer extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			LocalDataHelper localHelper = new LocalDataHelper();
			CloudDataHelper cloudHelper = new CloudDataHelper();
			List<Course> courseList = localHelper.getAllCourses();
			Course cloudCourse;
			String cloudId;
			long localTime, cloudTime;
			for (Course localCourse : courseList) {
				cloudId = localCourse.getCloudId();
				localTime = localCourse.getUpdateTime();
				if (cloudId != null) {
					try {
						cloudTime = cloudHelper.getUpdateTime(cloudId);
						if (localTime < cloudTime) {
							cloudCourse = cloudHelper.getCourse(cloudId);
							cloudCourse.setLocalId(localCourse.getLocalId());
							localHelper.saveCourse(cloudCourse, false);
						} else if (localTime > cloudTime) {
							if (localCourse.getCloudId() != null) {
								cloudHelper.updateCourse(localCourse);
							}
						}
					} catch (ParseException exception) {
						exception.printStackTrace();
					}
				} else {
					cloudHelper.createCourse(localCourse);
				}
			}
			return null;
		}
		
	}
	
}
