package edu.uta.mysyllabi.frontend;

import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.Controller;
import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.core.Instructor;
import edu.uta.mysyllabi.core.WeeklyMeeting;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyCourseController extends MySyllabiActivity {
	private Course course; // course object to be modified
	private Course oldCourse; // course obsoleted by cloud update(s)
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
		this.controller = MySyllabi.getAppController();
		
		/* Load the course object from back-end. */
		this.course = controller.getCourse(getIntent().getStringExtra(KEY_COURSE_ID));
		this.oldCourse = controller.getObsoleteCourse(course.getLocalId());
		if (oldCourse == null) {
			oldCourse = course;
		}
		
		/* Load the course data into the input views. */
		prepareInputViews();
	}
	
	public void prepareInputViews() {
		/* Course Identity */
		prepareText(courseNameView, course.getName(), oldCourse.getName());
		prepareText(courseSectionView, course.getSection(), oldCourse.getSection());
		prepareText(courseTitleView, course.getTitle(), oldCourse.getTitle());
		
		/* Class Meetings */
		prepareText(courseClassroomView, course.getClassroom(), oldCourse.getClassroom());
		if (course.getMeeting().isValid()) {
			prepareText(courseMeetingStartView, course.getMeeting().getStartTime().toString(DateFormat.is24HourFormat(this)),
					oldCourse.getMeeting().getStartTime().toString(DateFormat.is24HourFormat(this)));
			prepareText(courseMeetingEndView, course.getMeeting().getEndTime().toString(DateFormat.is24HourFormat(this)),
					oldCourse.getMeeting().getEndTime().toString(DateFormat.is24HourFormat(this)));
			prepareText(courseMeetingDaysView, course.getMeeting().getDaysOfWeek(), oldCourse.getMeeting().getDaysOfWeek());
		} else {
			course.setMeeting(new WeeklyMeeting());
		}
		courseMeetingStartView.setOnClickListener(new SetTimeDialogFragment(this, course.getMeeting(), false));
		courseMeetingEndView.setOnClickListener(new SetTimeDialogFragment(this, course.getMeeting(), true));
		courseMeetingDaysView.setOnClickListener(new SelectDaysDialogFragment(this, course.getMeeting()));
		
		/* Instructor Information */
		Instructor instructor = course.getInstructor();
		Instructor oldInstructor = oldCourse.getInstructor();
		if (instructor.isValid()) {
			prepareText(instructorFirstNameView, instructor.getFirstName(), oldInstructor.getFirstName());
			prepareText(instructorLastNameView, instructor.getLastName(), oldInstructor.getLastName());
			prepareText(instructorPhoneView, instructor.getPhoneNumber(), oldInstructor.getPhoneNumber());
			prepareText(instructorEmailView, instructor.getEmailAddress(), oldInstructor.getEmailAddress());
			prepareText(instructorOfficeView, instructor.getOfficeId(), oldInstructor.getOfficeId());
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
		
		/* Finish the activity. */
		this.finish();
	}

}
