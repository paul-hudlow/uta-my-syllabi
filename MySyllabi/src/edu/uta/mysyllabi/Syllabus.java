package edu.uta.mysyllabi;

import android.os.Bundle;

public class Syllabus {
	private Integer datumId; // identifier used in database
	protected String name; // identifier used by school
	protected String title;
	protected WeeklyMeeting meeting;
	protected Instructor instructor;
	
	/* no-argument constructor for externally created courses */
	public Syllabus() {
		super();
	}
	
	public Syllabus(Bundle bundle) {
		datumId = bundle.getInt(DataContract.Syllabi.COLUMN_COURSE_ID);
		name = bundle.getString(DataContract.Syllabi.COLUMN_COURSE_NAME);
		title = bundle.getString(DataContract.Syllabi.COLUMN_COURSE_TITLE);
		instructor = new Instructor(bundle.getString(DataContract.Syllabi.COLUMN_INSTRUCTOR_NAME));
		try {
			meeting = new WeeklyMeeting(bundle.getInt(DataContract.Syllabi.COLUMN_MEETING_START_TIME),
										bundle.getInt(DataContract.Syllabi.COLUMN_MEETING_DURATION),
										bundle.getString(DataContract.Syllabi.COLUMN_MEETING_DAYS),
										bundle.getString(DataContract.Syllabi.COLUMN_MEETING_LOCATION));
		} catch (IllegalArgumentException exception) {
			meeting = null;
		}
	}
	
	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putInt(DataContract.Syllabi.COLUMN_COURSE_ID, datumId);
		bundle.putString(DataContract.Syllabi.COLUMN_COURSE_NAME, name);
		bundle.putString(DataContract.Syllabi.COLUMN_COURSE_TITLE, title);
		bundle.putString(DataContract.Syllabi.COLUMN_INSTRUCTOR_NAME, instructor.getName());
		if (meeting != null) {
			bundle.putInt(DataContract.Syllabi.COLUMN_MEETING_START_TIME, meeting.getStartTime());
			bundle.putInt(DataContract.Syllabi.COLUMN_MEETING_DURATION,  meeting.getDuration());
			bundle.putString(DataContract.Syllabi.COLUMN_MEETING_DAYS, meeting.getDaysOfWeek());
			bundle.putString(DataContract.Syllabi.COLUMN_MEETING_LOCATION, meeting.getLocation());
		}
		return bundle;
	}
	
	public boolean isValid() {
		if (name.length() < 1 || title.length() < 1 || instructor.getName().length() < 1) {
			return false;
		}
		return true;
	}
	
	/* constructor for courses loaded from the database */
	public Syllabus(int id) {
		datumId = id;
	}
	
	public int getDatumId() {
		return this.datumId;
	}
}
