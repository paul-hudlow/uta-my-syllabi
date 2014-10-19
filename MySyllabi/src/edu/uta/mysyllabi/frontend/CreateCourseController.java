package edu.uta.mysyllabi.frontend;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.Controller;
import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.datatypes.SchoolSemester;

import android.support.v7.app.ActionBarActivity;
import android.widget.AdapterView.OnItemSelectedListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

public class CreateCourseController extends ActionBarActivity 
									implements TextWatcher, OnItemSelectedListener {
	private TextView schoolButton;
	private Spinner semesterSpinner;
	private EditText courseName;
	private EditText courseSection;
	private ListView courseList;
	private Controller controller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_course);
		
		this.schoolButton = (TextView) findViewById(R.id.create_course_school);
		this.semesterSpinner = (Spinner) findViewById(R.id.create_course_semester);
		this.courseName = (EditText) findViewById(R.id.create_course_name);
		this.courseSection = (EditText) findViewById(R.id.create_course_section);
		this.courseList = (ListView) findViewById(R.id.create_course_list);
		this.controller = new Controller();
		
		this.courseName.addTextChangedListener(this);
		this.courseSection.addTextChangedListener(this);
		this.semesterSpinner.setOnItemSelectedListener(this);
		
		/* Create an new ArrayAdapter for school semester. */
		ArrayAdapter<SchoolSemester> spinnerAdapter = 
				new ArrayAdapter<SchoolSemester>(getBaseContext(), R.layout.spinner_item);
		spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
		
		/* Start one semester back and populate the adapter with exactly four options. */
		SchoolSemester semester = SchoolSemester.getCurrent().getPrevious();
		for (int i = 0; i < 4; i++) {
			spinnerAdapter.add(semester);
			semester = semester.getNext();
		}
		
		/* Apply the adapter and set the Spinner to start on the current semester. */
		semesterSpinner.setAdapter(spinnerAdapter);
		semesterSpinner.setSelection(1);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* Inflate the menu; this adds items to the action bar if it is present. */
		getMenuInflater().inflate(R.menu.create_course, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	public void selectSchool(View view) {
		/* Open the school selection dialog. */
		DialogFragment dialog = new SelectSchoolDialogFragment();
		dialog.show(getFragmentManager(), "school");
	}
	
	public void createCourse(View view) {
		if (courseName.getText().toString().length() < 1) {
			Toast error = Toast.makeText(this, "Please enter valid Course ID", Toast.LENGTH_SHORT);
			error.show();
			return;
		} 
		
		if (schoolButton.getText().toString().equals(getResources().getString(R.string.hint_course_school))) {
			Toast error = Toast.makeText(this, "Please select a school", Toast.LENGTH_SHORT);
			error.show();
			return;
		}
		
		Course course = new Course(null, null);
		course.setName(courseName.getText().toString());
		String section = courseSection.getText().toString();
		if (section.length() != 0) {
			course.setSection(section);
		}
		course.setSchool(schoolButton.getText().toString());
		course.setSemester((SchoolSemester) semesterSpinner.getSelectedItem());
		
		String courseId = controller.createCourse(course, false);
		
		Intent intent = new Intent(this, ViewCourseController.class);
		intent.putExtra(ViewCourseController.KEY_COURSE_ID, courseId);
		intent.putExtra(ViewCourseController.KEY_MODIFY_COURSE, true);
		this.startActivity(intent);
		this.setResult(RESULT_OK);
		this.finish();
	}
	
	public void updateCourseSearch() {
		ArrayList<Course> cloudList = controller.findCourses(courseName.getText().toString(),
				courseSection.getText().toString(), schoolButton.getText().toString(),
				semesterSpinner.getSelectedItem().toString());
		ArrayList<HashMap<String,String>> mapList = new ArrayList<HashMap<String,String>>();
		
		for (Course nextCourse : cloudList) {
			mapList.add(nextCourse.getPreviewMap());
		}
		
		String[] courseElements = {Course.MAP_KEY_NAME, Course.MAP_KEY_MEETING, Course.MAP_KEY_INSTRUCTOR};
		int[] viewElements = {R.id.course_item_name, 
				R.id.course_item_meeting, R.id.course_item_instructor};
		SimpleAdapter listViewAdapter = new SimpleAdapter(this, mapList, 
				R.layout.course_item, courseElements, viewElements);
		this.courseList.setAdapter(listViewAdapter);
		
	}
	
	public String getSchool() {
		return this.schoolButton.getText().toString();
	}
	
	public void setSchool(String school) {
		this.schoolButton.setText(school);
		this.schoolButton.setTextColor(getResources().getColor(R.color.black));
	}
	
	public SchoolSemester getSemester() {
		return (SchoolSemester) this.semesterSpinner.getSelectedItem();
	}
	
	public String getCourseName() {
		return this.courseName.getText().toString();
	}
	
	public String getCourseSection() {
		return this.courseSection.getText().toString();
	}
		
	@Override
	public void afterTextChanged(Editable view) {
		updateCourseSearch();
	}
	
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	// TODO Auto-generated method stub
			
	}

	@Override
	public void onItemSelected(AdapterView<?> parentView, View view, 
			int position, long id) {
		updateCourseSearch();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
			
	}
	
	public class SelectSchoolDialogFragment extends DialogFragment 
											implements OnItemSelectedListener {
		private CreateCourseController activity;
		private Spinner stateSpinner;
		private Spinner schoolSpinner;
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			/* Get a builder to prepare the dialog. */
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			/* Inflate the predefined layout. */
		    LayoutInflater inflater = getActivity().getLayoutInflater();
		    View inflatedView = inflater.inflate(R.layout.dialog_select_school, null);
		    
			/* Save the Spinner views for convenient access. */
			stateSpinner = (Spinner) inflatedView.findViewById(R.id.dialog_select_school_state);
			schoolSpinner = (Spinner) inflatedView.findViewById(R.id.dialog_select_school);
		    
			/* Create an new ArrayAdapter from predefined U.S. state string array. */
			ArrayAdapter<CharSequence> spinnerAdapter;
			spinnerAdapter = ArrayAdapter.createFromResource(CreateCourseController.this, 
							R.array.us_states, R.layout.spinner_item);
			spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
			
			/* 
			 * Apply the adapter to the Spinner and add this instance of
			 * SelectSchoolDialogFragment as the selection listener.
			 * */
			stateSpinner.setAdapter(spinnerAdapter);
			stateSpinner.setOnItemSelectedListener(this);
		    
		    builder.setView(inflatedView)
		    		.setTitle("Select School")
		    // Add action buttons
		    		.setPositiveButton(R.string.okay, 
		        		   new SelectSchoolListener(activity, schoolSpinner))
		        	.setNegativeButton(R.string.cancel, new CancelDialogListener());	
			
			return builder.create();
		}
		
		@Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        try {
	        	this.activity = (CreateCourseController) activity;
	        } catch (ClassCastException exception) {
	        	throw new ClassCastException("Must be CreateCourseController activity!");
	        }
	        
	    }

		@Override
		public void onItemSelected(AdapterView<?> parentView, View view, 
				int position, long id) {
			/* Get school list for selected state. */
			String state = parentView.getItemAtPosition(position).toString();
			String[] schoolList = controller.getSchools(state);
			
			/* Create and populate ArrayAdapter for school Spinner. */
			ArrayAdapter<String> spinnerAdapter;
			spinnerAdapter = new ArrayAdapter<String>(CreateCourseController.this, 
					R.layout.spinner_item, schoolList);
			//spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
			
			/* Apply the adapter to the spinner. */
			schoolSpinner.setAdapter(spinnerAdapter);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
			// Do nothing.
		}
		
		protected class SelectSchoolListener implements DialogInterface.OnClickListener {
			private CreateCourseController activity;
			private Spinner schoolSpinner;
			
			public SelectSchoolListener(CreateCourseController activity, Spinner schoolSpinner) {
				this.activity = activity;
				this.schoolSpinner = schoolSpinner;
			}
			
            public void onClick(DialogInterface dialog, int id) {
                String schoolName = schoolSpinner.getSelectedItem().toString();
                activity.setSchool(schoolName);
            }
        }
		
		protected class CancelDialogListener implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int id) {
                SelectSchoolDialogFragment.this.getDialog().cancel();
            }
        }
		
	}
	
}
