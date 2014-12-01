package edu.uta.mysyllabi.frontend;

import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.Controller;
import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.core.Event;

public class ModifyEventController extends MySyllabiActivity implements
		OnItemSelectedListener {

	public static final String KEY_EVENT_ID = "event_id";
	private Event event;
	private Event oldEvent;
	private Course course;
	private Spinner courseSpinner;
	private Controller controller;
	private TextView EventDate;
	private TextView EventTime;
	private EditText LocationView;
	private EditText TitleView;
	private Calendar calendar = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_event);
		this.courseSpinner = (Spinner) findViewById(R.id.course_spinner);
		this.controller = MySyllabi.getAppController();
		this.courseSpinner.setOnItemSelectedListener(this);
		loadSpinnerData();

		EventDate = (TextView) findViewById(R.id.select_date);
		EventTime = (TextView) findViewById(R.id.select_time);
		LocationView = (EditText) findViewById(R.id.event_location);
		TitleView = (EditText) findViewById(R.id.event_type);

		final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				event.setDate(calendar.getTime());
				EventDate.setText(event.getDate());
				EventDate.setTextColor(getResources().getColor(R.color.black));
			}
			
		};

		EventDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == R.id.select_date) {
					new DatePickerDialog(ModifyEventController.this, date,
							calendar.get(Calendar.YEAR), 
							calendar.get(Calendar.MONTH), 
							calendar.get(Calendar.DAY_OF_MONTH)).show();
				}
			}

		});

		String eventId = getIntent().getStringExtra(KEY_EVENT_ID);
		if (eventId != null) {
			this.event = controller.getEvent(eventId);
		} else {
			this.event = new Event(null, null);
		}
		this.oldEvent = controller.getObsoleteEvent(event.getLocalId());
		if (oldEvent == null) {
			oldEvent = event;
		}
		
		prepareViews();
		
	}

	/**
	 * Function to load the spinner data from SQLite database.
	 */
	private void loadSpinnerData() {
		List<Course> coursenames = controller.getAllCourses();

		// Creating adapter for spinner
		ArrayAdapter<Course> dataAdapter = new ArrayAdapter<Course>(
				getBaseContext(), R.layout.spinner_item, coursenames);
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(R.layout.spinner_item);
		// attaching data adapter to spinner
		courseSpinner.setAdapter(dataAdapter);
	}
	
	private void prepareViews() {
		EventTime.setOnClickListener(new SetTimeDialogFragment(this, event, false));
		if (event.getLocalId() != null) {
			prepareText(EventTime, event.getStartTime().toString(false), oldEvent.getStartTime().toString(false));
			prepareText(EventDate, event.getDate(), oldEvent.getDate());
			prepareText(LocationView, event.getLocation(), oldEvent.getLocation());
			prepareText(TitleView, event.getName(), oldEvent.getName());
		}		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// On selecting a spinner item
		this.course = (Course)parent.getItemAtPosition(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_modify_event, menu);
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
	
	public void saveEvent(View view) {
		event.setName(TitleView.getText().toString());
		event.setLocation(LocationView.getText().toString());
		controller.saveEvent(event, course.getLocalId());
		controller.synchronize(null);
		this.finish();
	}

}
