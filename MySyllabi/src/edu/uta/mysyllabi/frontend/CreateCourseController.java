package edu.uta.mysyllabi.frontend;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.Controller;
import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.core.SchoolSemester;

import android.support.v7.app.ActionBarActivity;
import android.widget.AdapterView.OnItemSelectedListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
									implements TextWatcher, OnItemSelectedListener, OnItemClickListener {
	/* Create fields for all view objects. */
	private TextView schoolText;
	private Spinner semesterSpinner;
	private EditText courseName;
	private EditText courseSection;
	private ListView courseList;
	private Controller controller;
	private ArrayList<Course> cloudList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_course);
		
		/* Initialize object fields. */
		this.schoolText = (TextView) findViewById(R.id.create_course_school);
		this.semesterSpinner = (Spinner) findViewById(R.id.create_course_semester);
		this.courseName = (EditText) findViewById(R.id.create_course_name);
		this.courseSection = (EditText) findViewById(R.id.create_course_section);
		this.courseList = (ListView) findViewById(R.id.create_course_list);
		this.controller = MySyllabi.getAppController();
		
		/* Set this activity as listener for view content changes. */
		this.courseName.addTextChangedListener(this);
		this.courseSection.addTextChangedListener(this);
		this.semesterSpinner.setOnItemSelectedListener(this);
		this.courseList.setOnItemClickListener(this);
		
		String previousSchool = controller.getLatestSchool();
		if (previousSchool != null) {
			setSchool(previousSchool);
		}
		
		/* Create an new ArrayAdapter for semester spinner. */
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		addCourse(this.cloudList.get(position));
	}
	
	public void addCourse(Course course) {
		String courseId = controller.createCourse(course);
		Intent intent = new Intent(this, ViewCourseController.class);
		intent.putExtra(ViewCourseController.KEY_COURSE_ID, courseId);
		startActivity(intent);
		this.setResult(RESULT_OK);
		this.finish();
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
	
	/* Called when the school selection view is touched. */
	public void selectSchool(View view) {
		
		/* Open the school selection dialog. */
		DialogFragment dialog = new SelectSchoolDialogFragment();
		dialog.show(getFragmentManager(), "school");
	}
	
	/* Called when create course button is touched. */
	public void createCourse(View view) {
		
		/* Validate selected school. */
		String school = schoolText.getText().toString();
		if (school.equals(getResources().getString(R.string.hint_course_school))) {
			Toast error = Toast.makeText(this, "Please select a school", Toast.LENGTH_SHORT);
			error.show();
			return;
		}
		
		/* Enter data into new Course object. */
		Course course = new Course(null, null);
		course.setName(courseName.getText().toString());
		String section = courseSection.getText().toString();
		if (section.length() != 0) {
			course.setSection(section);
		}
		course.setSchool(schoolText.getText().toString());
		course.setSemester((SchoolSemester) semesterSpinner.getSelectedItem());
		
		/* Validate course name. */
		if (!course.nameIsValid()) {
			Toast error = Toast.makeText(this, "Course ID must consist of letters followed by numbers.", Toast.LENGTH_SHORT);
			error.show();
			return;
		}
		
		/* Create new course. */
		String courseId = controller.createCourse(course);
		
		/* Start the view course activity, telling it to forward to the modify course activity. */
		Intent intent = new Intent(this, ModifyCourseController.class);
		intent.putExtra(ViewCourseController.KEY_COURSE_ID, courseId);
		intent.putExtra(ViewCourseController.KEY_MODIFY_COURSE, true);
		this.startActivity(intent);
		this.finish();
	}
	
	private void updateCourseSearch() {
		new CourseSearchUpdater().execute(
				courseName.getText().toString(),
				courseSection.getText().toString(),
				schoolText.getText().toString(), 
				semesterSpinner.getSelectedItem().toString());
	}
	
	private class CourseSearchUpdater extends AsyncTask<String, Void, ArrayList<Course>> {

		@Override
		protected ArrayList<Course> doInBackground(String... params) {
			if (params.length != 4) {
				throw new IllegalArgumentException();
			}
			ArrayList<Course> cloudList = controller.findCourses(
					params[0], params[1], params[2], params[3]);
			return cloudList;
		}
		
		protected void onPostExecute(ArrayList<Course> cloudList) {
			CreateCourseController.this.cloudList = cloudList;
			ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
			for (Course nextCourse : cloudList) {
				mapList.add(nextCourse.getPreviewMap());
			}
			String[] courseElements = { 
					Course.MAP_KEY_NAME,
					Course.MAP_KEY_MEETING, 
					Course.MAP_KEY_INSTRUCTOR };
			int[] viewElements = { 
					R.id.course_item_name, 
					R.id.course_item_meeting,
					R.id.course_item_instructor};
			SimpleAdapter listViewAdapter = new SimpleAdapter(CreateCourseController.this, mapList,
					R.layout.course_item, courseElements, viewElements);
			CreateCourseController.this.courseList.setAdapter(listViewAdapter);
	    }
		
	}
	
	protected void setSchool(String school) {
		this.schoolText.setText(school);
		this.schoolText.setTextColor(getResources().getColor(R.color.black));
	}
	
	/* Update search when text is changed. */
	@Override
	public void afterTextChanged(Editable view) {
		updateCourseSearch();
	}
	
	/* Required for TextWatcher interface. */
	@Override
	public void beforeTextChanged(CharSequence a, int b, int c, int d) {}
	@Override
	public void onTextChanged(CharSequence a, int b, int c, int d) {}

	/* Update course search when spinner value is changed. */
	@Override
	public void onItemSelected(AdapterView<?> parentView, View view, 
			int position, long id) {
		updateCourseSearch();
	}

	/* Required for OnItemSelectedListener interface. */
	@Override
	public void onNothingSelected(AdapterView<?> a) {}
	
	/* Handler class for school selection dialog. */
	public class SelectSchoolDialogFragment extends DialogFragment 
											implements OnItemSelectedListener {
		/* Create fields for the activity and dialog views. */
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
		    
			/* Build dialog with predefined layout and standard buttons. */
		    builder.setView(inflatedView)
		    		.setTitle("Select School")
		    		.setPositiveButton(R.string.okay, 
		    				new SelectSchoolListener((CreateCourseController) getActivity(), 
		    						schoolSpinner))
		        	.setNegativeButton(R.string.cancel, 
		        			new CancelDialogListener());	
			
			return builder.create();
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

		/* Required for OnItemSelectedListener interface. */
		@Override
		public void onNothingSelected(AdapterView<?> parentView) {}
		
		/* Listener for dialogs 'okay' button. */
		protected class SelectSchoolListener implements DialogInterface.OnClickListener {
			private CreateCourseController activity;
			private Spinner schoolSpinner;
			
			/* Must be created with the host activity and source spinner. */
			public SelectSchoolListener(CreateCourseController activity, Spinner schoolSpinner) {
				this.activity = activity;
				this.schoolSpinner = schoolSpinner;
			}
			
			/* Set the school on the activity. */
            public void onClick(DialogInterface dialog, int id) {
                String schoolName = schoolSpinner.getSelectedItem().toString();
                activity.setSchool(schoolName);
                updateCourseSearch();
            }
        }
		
		/* Cancel the dialog. */
		protected class CancelDialogListener implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int id) {
                SelectSchoolDialogFragment.this.getDialog().cancel();
            }
        }
		
	}
	
}
