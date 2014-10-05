package edu.uta.mysyllabi;

import android.support.v7.app.ActionBarActivity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

public class CreateCourse extends ActionBarActivity {
	private int startTime = 0;
	
	public void saveCourse(View view) {
		Syllabus course = new Syllabus();
		course.name = getStringFromView(R.id.course_name);
		course.title = getStringFromView(R.id.course_title);
		String meetingDays = "";
		meetingDays += getCharFromCheckBox(R.id.checkbox_mon);
		meetingDays += getCharFromCheckBox(R.id.checkbox_tue);
		meetingDays += getCharFromCheckBox(R.id.checkbox_wed);
		meetingDays += getCharFromCheckBox(R.id.checkbox_thu);
		meetingDays += getCharFromCheckBox(R.id.checkbox_fri);
		meetingDays += getCharFromCheckBox(R.id.checkbox_sat);
		meetingDays += getCharFromCheckBox(R.id.checkbox_sun);
		
 		try {
 			course.meeting = new WeeklyMeeting(startTime, 
 										   getDuration(), 
 										   meetingDays, 
 										   getStringFromView(R.id.meeting_location));
 		} catch (Exception exception) {
 			course.meeting = null;
 		}
		course.instructor = new Instructor(getStringFromView(R.id.instructor_name));
		
		if (!course.isValid()) {
			return;
		}
		
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		helper.addCourse(course);
		
		// Change to the ViewCourse activity.
		Intent intent = new Intent(this, SelectCourse.class);
		startActivity(intent);
	}
	
    public void showTimePicker(View view) {
    	DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
	
    private char getCharFromCheckBox(int viewId) {
    	CheckBox boxView = (CheckBox)findViewById(viewId);
    	if (boxView.isChecked()) {
    		return 'y';
    	} else {
    		return 'n';
    	}
    }
    
	private String getStringFromView(int viewId){
		EditText textView = (EditText)findViewById(viewId);
		return textView.getText().toString();
	}
	
	private int getDuration() {
		Spinner spinner = (Spinner)findViewById(R.id.meeting_length);
		return (int)spinner.getSelectedItemPosition()*30 + 30;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_course);
		
		Spinner spinner = (Spinner) findViewById(R.id.meeting_length);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.duration_array, 
																			 android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_course, menu);
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
	
	public class TimePickerFragment extends DialogFragment 
		implements TimePickerDialog.OnTimeSetListener {
	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int hour = 12;
			int minute = 0;
			return new TimePickerDialog(getActivity(), this, hour, minute, false);
		}
		
		@Override
		public void onTimeSet(TimePicker timeView, int hour, int minute) {
			TimeOfDay time = new TimeOfDay(hour, minute);
			Button setterButton = (Button)findViewById(R.id.meeting_start);
			setterButton.setText(time.getTime(false));
			startTime = time.getTotalMinutes();
		}
	}
	
}
