package edu.uta.mysyllabi.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.parse.ParseException;

import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.core.Event;

public class SynchronizationHelper {
	
	LocalDataHelper localHelper = new LocalDataHelper();
	CloudDataHelper cloudHelper = new CloudDataHelper();
	
	public void synchronize() {
		List<Course> courseList = localHelper.getAllCourses();
		Course cloudCourse;
		String cloudId;
		String localId;
		long cloudTime;
		long localTime;
		for (Course localCourse : courseList) {
			if (!localCourse.isLocked()) { // Confirm that course is not locked.
				cloudId = localCourse.getCloudId();
				localId = localCourse.getLocalId();
				localTime = localCourse.getUpdateTime();
				try {
					if (cloudId == null) { // This indicates a course with no cloud counterpart.
						Log.d("SynchronizationHelper", "Creating course " + localCourse.getLocalId() + " on cloud.");
						cloudHelper.createCourse(localCourse);
						localHelper.linkToCloud(localCourse);	
					} else {
						cloudTime = cloudHelper.getUpdateTime(cloudId);
						if (localTime < cloudTime) { // This indicates new changes on the cloud;
							Log.d("SynchronizationHelper", "Updating course " + localCourse.getLocalId() + " from cloud.");
							cloudCourse = cloudHelper.getCourse(cloudId);
							cloudCourse.setLocalId(localCourse.getLocalId());
							localHelper.saveFromCloud(cloudCourse);
							synchronizeEvents(localCourse, cloudCourse.getEvents());
						} else if (localHelper.hasLocalChanges(localId)) { // This indicates new local changes.
							Log.d("SynchronizationHelper", "Updating course " + localCourse.getLocalId() + " on cloud.");
							cloudHelper.updateCourse(localCourse);
							localHelper.linkToCloud(localCourse);
							synchronizeEvents(localCourse, cloudHelper.getEvents(localCourse.getCloudId()));
						} else {
							synchronizeEvents(localCourse, cloudHelper.getEvents(localCourse.getCloudId()));
						}
					}
				} catch (ParseException exception) {
					return; // Give up on synchronization for now.
				}
			}
		}
	}
	
	private void synchronizeEvents(Course localCourse, List<Event> cloudList) throws ParseException {
		
		List<Event> localList = localCourse.getEvents();
		Map<String, Event> cloudMap = new HashMap<String, Event>();
		for (Event nextEvent : cloudList) {
			cloudMap.put(nextEvent.getCloudId(), nextEvent);
		}
		Event cloudEvent;
		for (Event localEvent : localList) {
			if (localEvent.getCloudId() == null) {
				Log.d("SynchronizationHelper", "Creating event " + localEvent.getLocalId() + " on cloud.");
				cloudHelper.createEvent(localEvent, localCourse);
				localHelper.linkToCloud(localEvent);
			} else {
				cloudEvent = cloudMap.get(localEvent.getCloudId());
				cloudMap.remove(localEvent.getCloudId());
				if (localEvent.getUpdateTime() < cloudEvent.getUpdateTime()) {
					Log.d("SynchronizationHelper", "Updating event " + localEvent.getLocalId() + " from cloud.");
					cloudEvent.setLocalId(localEvent.getLocalId());
					localHelper.saveFromCloud(cloudEvent, localCourse.getLocalId());
				} else if (localHelper.hasLocalChanges(localEvent)) {
					Log.d("SynchronizationHelper", "Updating event " + localEvent.getLocalId() + " on cloud.");
					cloudHelper.updateEvent(localEvent);
					localHelper.linkToCloud(localEvent);
				}
			}
		}
		
		for (Event newEvent : cloudMap.values()) {
			localHelper.createEvent(newEvent, localCourse.getLocalId());
		}
	}
	
	
}
