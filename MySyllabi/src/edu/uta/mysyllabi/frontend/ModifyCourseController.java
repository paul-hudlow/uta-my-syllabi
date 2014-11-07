package edu.uta.mysyllabi.frontend;

import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.datatypes.Instructor;
import edu.uta.mysyllabi.core.Controller;
import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.datatypes.WeeklyMeeting;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

public class ModifyCourseController extends Activity {
	private Course course; // course object to be modified
	private Controller controller; // system controller for back-end operations
	
	/* key corresponding to the Course ID, passed in the Intent data */
	public static final String KEY_COURSE_ID = "course_id";
	
	/* static input views available from the layout file */
	private EditText courseNameView;
	private EditText courseSectionView;
	private EditText courseTitleView;
	private EditText courseClassroomView;
	private TextView courseMeetingStartView;
	private TextView courseMeetingEndView;
	private TextView courseMeetingDaysView;
	private EditText instructorFirstNameView;
	private EditText instructorLastNameView;
	private EditText instructorPhoneView;
	private EditText instructorEmailView;
	private EditText instructorOfficeView;
	// private TextView instructorOfficeHoursStartView;
	// private TextView instructorOfficeHoursDurationView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_course);
		
		/* Assign views from layout to object fields. */
		courseNameView = (EditText) findViewById(R.id.create_course_name);
		courseTitleView = (EditText) findViewById(R.id.modify_course_title);
		courseSectionView = (EditText) findViewById(R.id.create_course_section);
		courseClassroomView = (EditText) findViewById(R.id.modify_classroom);
		courseMeetingStartView = (TextView) findViewById(R.id.modify_meeting_start);
		courseMeetingEndView = (TextView) findViewById(R.id.modify_meeting_end);
		courseMeetingDaysView = (TextView) findViewById(R.id.modify_meeting_days);
		instructorFirstNameView = (EditText) findViewById(R.id.modify_instructor_first_name);
		instructorLastNameView = (EditText) findViewById(R.id.modify_instructor_last_name);
		instructorPhoneView = (EditText) findViewById(R.id.modify_instructor_phone);
		instructorEmailView = (EditText) findViewById(R.id.modify_instructor_email);
		instructorOfficeView = (EditText) findViewById(R.id.modify_instructor_office);
		// instructorOfficeHoursStartView = 
		//		(EditText) findViewById(R.id.modify_instructor_office_hours_start);
		// instructorOfficeHoursDurationView = 
		//		(EditText) findViewById(R.id.modify_instructor_office_hours_duration);
		
		/* Instantiate the system controller. */
		this.controller = new Controller();
		
		/* Load the course object from back-end. */
		this.course = controller.getCourse(getIntent().getStringExtra(KEY_COURSE_ID));
		
		/* Load the course data into the input views. */
		prepareInputViews();
	}
	
	public void prepareInputViews() {
		/* Course Identity */
		courseNameView.setText(course.getName());
		courseTitleView.setText(course.getTitle());
		courseSectionView.setText(course.getSection());
		
		/* Class Meetings */
		courseClassroomView.setText(course.getClassroom());
		if (course.getMeeting().isValid()) {
			courseMeetingStartView.setText(course.getMeeting().getStartTime().toString(DateFormat.is24HourFormat(this)));
			courseMeetingStartView.setTextColor(getResources().getColor(R.color.black));
			courseMeetingEndView.setText(course.getMeeting().getEndTime().toString(DateFormat.is24HourFormat(this)));
			courseMeetingEndView.setTextColor(getResources().getColor(R.color.black));
			courseMeetingDaysView.setText(course.getMeeting().getDaysOfWeek());
			courseMeetingDaysView.setTextColor(getResources().getColor(R.color.black));
		} else {
			course.setMeeting(new WeeklyMeeting());
		}
		courseMeetingStartView.setOnClickListener(new SetTimeDialogFragment(this, course.getMeeting(), false));
		courseMeetingEndView.setOnClickListener(new SetTimeDialogFragment(this, course.getMeeting(), true));
		courseMeetingDaysView.setOnClickListener(new SelectDaysDialogFragment(this, course.getMeeting()));
		
		/* Instructor Information */
		Instructor instructor = course.getInstructor();
		if (instructor.isValid()) {
			instructorFirstNameView.setText(instructor.getFirstName());
			instructorLastNameView.setText(instructor.getLastName());
			instructorPhoneView.setText(instructor.getPhoneNumber());
			instructorEmailView.setText(instructor.getEmailAddress());
			instructorOfficeView.setText(instructor.getOfficeId());
		} else {
			course.setInstructor(new Instructor());
		}
		// instructorOfficeHoursStartView
		//	.setText(course.getInstructor().getOfficeHoursStart());
		// instructorOfficeHoursDurationView
		// 	.setText(course.getInstructor().getOfficeHoursDuration());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.modify_course, menu);
		return true;
	}

	/* default menu handling method */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void modifyCourse(View view) {
		/* Set course name, section, title, and classroom. */
		course.setName(courseNameView.getText().toString());
		
		/* Validate course name. */
		if (!course.nameIsValid()) {
			Toast error = Toast.makeText(this, "Course ID must consist of letters followed by numbers.", Toast.LENGTH_SHORT);
			error.show();
			return;
		}
		
		String section = courseSectionView.getText().toString();
		if (section.length() != 0) {
			course.setSection(section);
		}
		course.setTitle(courseTitleView.getText().toString());
		course.setClassroom(courseClassroomView.getText().toString());
		
		/* Set instructor information. */
		Instructor instructor = course.getInstructor();
		instructor.setFirstName(instructorFirstNameView.getText().toString());
		instructor.setLastName(instructorLastNameView.getText().toString());
		instructor.setOfficeId(instructorOfficeView.getText().toString());
		instructor.setPhoneNumber(instructorPhoneView.getText().toString());
		instructor.setEmailAddress(instructorEmailView.getText().toString());
		
		/* Update back-end course data. */
		controller.updateCourse(course);
		
		/* Start view course activity. */
		Intent intent = new Intent(this, ViewCourseController.class);
		intent.putExtra(ViewCourseController.KEY_COURSE_ID, course.getLocalId());
		this.startActivity(intent);
		this.setResult(RESULT_OK);
		this.finish();
	}

}
