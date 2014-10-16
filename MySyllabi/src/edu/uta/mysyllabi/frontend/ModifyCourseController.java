package edu.uta.mysyllabi.frontend;

import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.Controller;
import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.datatypes.TimeOfDay;
import edu.uta.mysyllabi.datatypes.WeeklyMeeting;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.TimePickerDialog;

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
	private TextView courseMeetingDurationView;
	private TextView courseMeetingDaysView;
	private EditText instructorFirstNameView;
	private EditText instructorLastNameView;
	private EditText instructorPhoneView;
	private EditText instructorEmailView;
	private EditText instructorOfficeView;
	private TextView instructorOfficeHoursStartView;
	private TextView instructorOfficeHoursDurationView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_course);
		
		/* Assign views from layout to object fields. */
		courseNameView = (EditText) findViewById(R.id.create_course_name);
		// courseTitleView = (EditText) findViewById(R.id.modify_course_title);
		courseSectionView = (EditText) findViewById(R.id.create_course_section);
		courseClassroomView = (EditText) findViewById(R.id.modify_classroom);
		courseMeetingStartView = (TextView) findViewById(R.id.modify_meeting_start);
		courseMeetingDurationView = (TextView) findViewById(R.id.modify_meeting_duration);
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
		// courseTitleView.setText(course.getTitle());
		courseSectionView.setText(course.getSection());
		
		/* Class Meetings */
		courseClassroomView.setText(course.getClassroom());
		if (course.getMeetingStart() != null) {
			courseMeetingStartView.setText(course.getMeetingStart());
		}
		courseMeetingStartView.setOnClickListener(new SetTimeDialogFragment(this, new WeeklyMeeting(), false));
		// courseMeetingDurationView.setText(course.getMeetingDuration());
		courseMeetingDurationView.setOnClickListener(new SetTimeDialogFragment(this, new WeeklyMeeting(), true));
		if (course.getMeetingDays() != null) {
			courseMeetingDaysView.setText(course.getMeetingDays());
		}
		courseMeetingDaysView.setOnClickListener(new SelectDaysDialogFragment(this, new WeeklyMeeting()));
		
		/* Instructor Information */
		try {
			instructorFirstNameView.setText(course.getInstructor().getFirstName());
			instructorLastNameView.setText(course.getInstructor().getLastName());
			instructorPhoneView.setText(course.getInstructor().getPhoneNumber());
			instructorEmailView.setText(course.getInstructor().getEmailAddress());
			instructorOfficeView.setText(course.getInstructor().getOfficeId());
		} catch (NullPointerException exception) {
			// If no instructor exists, ignore.
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
