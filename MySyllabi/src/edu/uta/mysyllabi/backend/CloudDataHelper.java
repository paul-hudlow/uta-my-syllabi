package edu.uta.mysyllabi.backend;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.datatypes.Instructor;
import edu.uta.mysyllabi.datatypes.WeeklyMeeting;

public class CloudDataHelper {
	

	public static String saveCourse(Course course) {
		
		final ParseObject syllabi = new ParseObject("syllabi");
		
		//String cloudId = course.getCloudId();
		syllabi.put("CourseName", course.getName());
	//	syllabi.put("CourseTitle", course.getTitle());
		syllabi.put("Section", course.getSection());
		syllabi.put("School", course.getSchool());
		syllabi.put("Semester", course.getSemester().toString());
		
		Instructor instructor = course.getInstructor();
	    if (instructor != null) {
	    	syllabi.put("InstFName", instructor.getName());
	    	syllabi.put("InstLName", instructor.getLastName());
	    	syllabi.put("InstOfficeId", instructor.getOfficeHours());
	    	syllabi.put("InstPhoneNo", instructor.getPhoneNumber());
	    	syllabi.put("InstEmail", instructor.getEmailAddress());
	    }
	    
	    WeeklyMeeting meeting = course.getMeeting();
	    if (meeting != null && meeting.getStartTime() != null) {
	    	syllabi.put("MeetingStartTime", meeting.getStartTime().getTotalMinutes());
	    	syllabi.put("MeetingDuration", meeting.getDuration());
	    	syllabi.put("MeetingDays", meeting.getDaysString());
	    	syllabi.put("MeetingLocation", meeting.getLocation());
	    }
	    
	    syllabi.saveInBackground();
		return "21";
	}

	public static Course getCourse(String key) {
		Course cloudCourse = new Course(null, null);
		// TODO implement method
		return cloudCourse;
	}
	
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
	
	public ArrayList<String> getCourseList(String schoolName) {
			final ArrayList<String> courseListArray =  new ArrayList<String>();

			ParseQuery<ParseObject> courseQuery = ParseQuery.getQuery("syllabi");
			courseQuery.whereEqualTo("school", schoolName);
			courseQuery.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(List<ParseObject> courseList, ParseException e) {
					if (e == null) {
			            Log.d("course", "Retrieved " + courseList.size() + 	" CourseName");
			            for(int i = 0; i < courseList.size(); i++)
			            {
			            	courseListArray.add(((ParseObject) courseList).getString("CourseName"));
			            }
					} else {
						Log.d("course", "Error: " + e.getMessage());
					}
				
				}
				

			});
			
			return courseListArray;
			
			}
		}

