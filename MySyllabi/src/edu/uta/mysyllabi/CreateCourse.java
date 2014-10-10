package edu.uta.mysyllabi;

import android.support.v7.app.ActionBarActivity;
import android.widget.AdapterView.OnItemSelectedListener;
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
import android.widget.Button;
import android.widget.Spinner;

public class CreateCourse extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_course);
		
		Spinner semesterSpinner = (Spinner) findViewById(R.id.create_course_semester);
		
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
	
	public void addCourse(View view) {
		/* Move on to the main activity. */
		Intent intent = new Intent(this, SelectCourse.class);
		startActivity(intent);
	}
	
	public class SelectSchoolDialogFragment extends DialogFragment 
											implements OnItemSelectedListener {
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
			spinnerAdapter = ArrayAdapter.createFromResource(CreateCourse.this, 
							R.array.us_states, R.layout.spinner_item);
			spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
			
			/* 
			 * Apply the adapter to the Spinner and add this instance of
			 * SelectSchoolDialogFragment as the selection listener.
			 * */
			stateSpinner.setAdapter(spinnerAdapter);
			stateSpinner.setOnItemSelectedListener(this);
		    
		    builder.setView(inflatedView)
		    // Add action buttons
		           .setPositiveButton(R.string.okay, new SelectSchoolListener())
		           .setNegativeButton(R.string.cancel, new CancelDialogListener());	
			
			return builder.create();
		}

		@Override
		public void onItemSelected(AdapterView<?> parentView, View view, int position,
				long id) {
			/* Get school list for selected state. */
			String state = parentView.getItemAtPosition(position).toString();
			String[] schoolList = Course.getSchoolList(state);
			
			/* Create and populate ArrayAdapter for school Spinner. */
			ArrayAdapter<String> spinnerAdapter;
			spinnerAdapter = new ArrayAdapter<String>(CreateCourse.this, 
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
            public void onClick(DialogInterface dialog, int id) {
                Button schoolButton = (Button) findViewById(R.id.create_course_school);
                Spinner schoolSpinner = (Spinner) findViewById(R.id.dialog_select_school);
                String schoolName = schoolSpinner.getSelectedItem().toString();
                schoolButton.setText(schoolName);
            }
        }
		
		protected class CancelDialogListener implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int id) {
                SelectSchoolDialogFragment.this.getDialog().cancel();
            }
        }
		
	}
	
}
