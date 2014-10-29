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
	

	
	private static String[] array = null ;
	private static List<ParseObject> courseObjects = null;
	
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
	
	
	
	public static String[] getCourseList(String schoolName, String reqFieldName) {
			ParseQuery<ParseObject> courseQuery = ParseQuery.getQuery("syllabi");
			
			courseQuery.whereEqualTo("School", schoolName);
			
			
			
			try {
				courseObjects = courseQuery.find();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			array = new String[courseObjects.size()];
			int index = 0;
			for(Object value : courseObjects)
			{
				array[index] = (String) ((ParseObject) value).getString(reqFieldName);
				index++;
			}
					return array;
			}
	
	public static String getElementsOffParse(String courseNo, String element)
	{
		String value = null;
		
		for(Object value1 : courseObjects)
		{
			if(value1.equals(courseNo))
			value = (String) ((ParseObject) value1).getString(element);
		}
		return value;
			
		
	}
	
	}

