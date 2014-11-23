package edu.uta.mysyllabi.frontend;

import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.Controller;
import edu.uta.mysyllabi.core.Course;

public class ModifyEventController extends ActionBarActivity implements
		OnItemSelectedListener {

	private Spinner coursespinner;
	private Controller controller;
	EditText EventDate;
	EditText EventTime;
	Calendar myCalendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_event);
		this.coursespinner = (Spinner) findViewById(R.id.course_spinner);
		this.controller = new Controller();
		this.coursespinner.setOnItemSelectedListener(this);
		loadSpinnerData();

		EventDate = (EditText) findViewById(R.id.select_date);
		EventTime = (EditText) findViewById(R.id.select_time);
		myCalendar = Calendar.getInstance();

		final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateDisplay();
			}

			private void updateDisplay() {
				// TODO Auto-generated method stub

				EventDate.setText(new StringBuilder()
						// Month is 0 based so add 1
						.append(myCalendar.get(Calendar.MONTH) + 1).append("-")
						.append(myCalendar.get(Calendar.DAY_OF_MONTH))
						.append("-").append(myCalendar.get(Calendar.YEAR))
						.append(" "));

			}
		};

		EventDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == R.id.select_date) {
					new DatePickerDialog(ModifyEventController.this, date,
							myCalendar.get(Calendar.YEAR), myCalendar
									.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
					// val=1;
				}
			}

		});

		// TimePicker

		final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourofday, int minute) {
				// TODO Auto-generated method stub
				myCalendar.set(Calendar.HOUR_OF_DAY, hourofday);
				myCalendar.set(Calendar.MINUTE, minute);
				// updateText();
				updateTime();

			}

			private void updateTime() {
				// TODO Auto-generated method stub

				EventTime.setText(new StringBuilder()
						.append(myCalendar.get(Calendar.HOUR_OF_DAY))
						.append(":").append(myCalendar.get(Calendar.MINUTE))
						.append(" "));

			}
		};

		EventTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == R.id.select_time) {
					new TimePickerDialog(ModifyEventController.this, time,
							myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar
									.get(Calendar.MINUTE), true).show();

				}

			}
		});
}

	/**
	 *      * Function to load the spinner data from SQLite database      *
	 */
	private void loadSpinnerData() {
		// database handler
		// DatabaseHandler db = new DatabaseHandler(getApplicationContext());

		// Spinner Drop down elements
		List<Course> coursenames = controller.getAllCourses();

		// Creating adapter for spinner
		ArrayAdapter<Course> dataAdapter = new ArrayAdapter<Course>(
				getBaseContext(), R.layout.spinner_item, coursenames);
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(R.layout.spinner_item);
		// attaching data adapter to spinner
		coursespinner.setAdapter(dataAdapter);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// On selecting a spinner item
		String label = parent.getItemAtPosition(position).toString();
		Toast.makeText(parent.getContext(), "You selected: " + label,
				Toast.LENGTH_LONG).show();
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

}
