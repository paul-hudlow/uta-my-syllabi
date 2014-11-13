package edu.uta.mysyllabi.backend;

import java.util.List;

import com.parse.ParseException;

import edu.uta.mysyllabi.core.Course;

public class SynchronizationHelper {
	
	public void synchronize() {
		LocalDataHelper localHelper = new LocalDataHelper();
		CloudDataHelper cloudHelper = new CloudDataHelper();
		List<Course> courseList = localHelper.getAllCourses();
		Course cloudCourse;
		String cloudId, localId;
		long localTime, cloudTime;
		for (Course localCourse : courseList) {
			if (!localCourse.isLocked()) { // Confirm that course is not locked.
				cloudId = localCourse.getCloudId();
				localId = localCourse.getLocalId();
				localTime = localCourse.getUpdateTime();
				try {
					if (cloudId == null) { // This indicates a course with no cloud counterpart.
						cloudHelper.createCourse(localCourse);
						localHelper.linkToCloud(localCourse);	
					} else {
						cloudTime = cloudHelper.getUpdateTime(cloudId);
						if (localTime < cloudTime) { // This indicates new changes on the cloud;
							cloudCourse = cloudHelper.getCourse(cloudId);
							cloudCourse.setLocalId(localCourse.getLocalId());
							localHelper.saveFromCloud(cloudCourse);
						} else if (!localHelper.hasLocalChanges(localId)) { // This indicates new local changes.
							cloudHelper.updateCourse(localCourse);
							localHelper.linkToCloud(localCourse);
						}
					}
				} catch (ParseException exception) {
					return; // Give up on synchronization for now.
				}
			}
		}

	}
	
}
