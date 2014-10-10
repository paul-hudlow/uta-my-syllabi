package edu.uta.mysyllabi;

import java.util.Random;

import android.text.format.Time;

public class Course {
	private int id;
	
	public Course() {
		super();
		Time time = new Time();
		Random random = new Random(time.second);
		id = random.nextInt()%10000; // Dummy random id.
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return "CSE " + id;
	}
	
	public String getTitle() {
		return "Software Engineering";
	}
	
	public void saveOnLocal() {
		// Dummy method.
	}
	
	public void saveOnCloud() {
		// Dummy method.
	}
	
	public void save() {
		this.saveOnLocal();
		this.saveOnCloud();
	}
	
	public static String[] getSchoolList(String state) {
		String[] schoolList = {"University of Texas at Arlington", "University of Texas at Dallas", "University of Arkansas"};
		return schoolList;
	}
	
	public static int[] getAllKeys() {
		// TODO implement method
		int[] dummy = {1, 2, 3, 4}; // Dummy keys.
		return dummy;
	}
	
	public static Course getById(int id) {
		return new Course(); // Dummy course.
	}
	
}
