package edu.uta.mysyllabi.frontend;

import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.Controller;
import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.core.Event;

public class ListEventsController extends Activity implements OnItemClickListener, OnItemSelectedListener {

	private ListView eventListView;
	private List<Event> eventList;
	private Spinner courseSpinner;
	private ArrayAdapter<Course> spinnerAdapter;
	private SimpleAdapter listViewAdapter;
	private Controller controller;
	private MySyllabiActivity dynamicHelper = new MySyllabiActivity();
	private int currentCoursePosition = -1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_events_controller);
		
		this.eventListView = (ListView) findViewById(R.id.list_events);
		this.eventListView.setOnItemClickListener(this); 
		this.courseSpinner = (Spinner)findViewById(R.id.course_filter_spinner);
		
		this.controller = MySyllabi.getAppController();
		
		/* Create an new ArrayAdapter for course spinner. */
		List<Course> courses = controller.getAllCourses();
		Course dummyCourse = new Course(null, null);
		dummyCourse.forceName(getString(R.string.course_filter));
		courses.add(0, dummyCourse);
		spinnerAdapter = new ArrayAdapter<Course>(getBaseContext(), R.layout.spinner_item, courses);
		spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
		
		courseSpinner.setAdapter(spinnerAdapter);
		courseSpinner.setOnItemSelectedListener(this);
	}
	
    @Override
    protected void onResume() {
    	super.onResume();
    	updateListView();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_events, menu);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		String eventId = eventList.get(position).getLocalId();
		Intent intent = new Intent(this, ModifyEventController.class);
		intent.putExtra(ModifyEventController.KEY_EVENT_ID, eventId);
		startActivity(intent);
		
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		this.currentCoursePosition = position;
		updateListView();		
	}
	
	private void updateListView() {
		if (this.currentCoursePosition < 0) {
			return;
		}
		String courseId = this.spinnerAdapter.getItem(currentCoursePosition).getLocalId();
		if (courseId != null) {
			this.eventList = controller.getEvents(courseId);
			this.listViewAdapter = dynamicHelper.populateDetailedList(this, eventList);
			this.eventListView.setAdapter(listViewAdapter);
		} else {
			this.eventList = controller.getAllEvents();
			this.listViewAdapter = dynamicHelper.populateDetailedList(this, eventList);
			this.eventListView.setAdapter(listViewAdapter);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing.
	}
}
