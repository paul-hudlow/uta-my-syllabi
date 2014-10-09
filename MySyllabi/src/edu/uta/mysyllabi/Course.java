package edu.uta.mysyllabi;

public class Course {
	private int datumId; // identifier used by MySyllabi
	protected String name; // identifier used by school
	protected String title;
	protected WeeklyMeeting meeting;
	protected Instructor instructor;
	
	/* no-argument constructor for user-created courses */
	public Course() {
		super();
	}
	
	public void saveOnLocal() {
		LocalDataHelper localHelper = new LocalDataHelper();
		localHelper.saveCourse(this);
	}
	
	public void saveOnCloud() {
		CloudDataHelper cloudHelper = new CloudDataHelper();
		cloudHelper.saveCourse(this);
	}
	
	public void save() {
		this.saveOnLocal();
		this.saveOnCloud();
	}
	
	public int getDatumId() {
		return this.datumId;
	}
	
	public static int[] getAllKeys() {
		// TODO implement method
		return null;
	}
}
