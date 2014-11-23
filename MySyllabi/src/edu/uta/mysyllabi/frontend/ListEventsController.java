package edu.uta.mysyllabi.frontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.Controller;
import edu.uta.mysyllabi.core.Event;
import edu.uta.mysyllabi.core.Listable;

public class ListEventsController extends Activity implements OnItemClickListener {

	private List<Event> events;
	private ListView eventList;
	private SimpleAdapter listViewAdapter; 
	private Controller controller;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_events_controller);
		
		
		this.eventList = (ListView) findViewById(R.id.list_events);
		this.eventList.setOnItemClickListener(this); 
		
		this.controller = MySyllabi.getAppController();
		
		
		events = controller.getAllEvents();
		
    	ArrayList<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for (Event nextEvent : events) {
			mapList.add(nextEvent.getPreviewMap());
		}
		String[] eventElements = { 
				Listable.PREVIEW_TITLE,
				Listable.PREVIEW_SUBTITLE, 
				Listable.PREVIEW_SECOND_LINE,
				Listable.Detailed.PREVIEW_THIRD_LINE,
				Listable.Detailed.PREVIEW_FOURTH_LINE };
		int[] viewElements = { 
				R.id.preview_title, 
				R.id.preview_subtitle,
				R.id.preview_second_line,
				R.id.preview_third_line,
				R.id.preview_fourth_line };
		
		listViewAdapter = new SimpleAdapter(this, mapList,
				R.layout.detailed_item, eventElements, viewElements);
		eventList.setAdapter(listViewAdapter);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_events_controller, menu);
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
		
		Event selectedEvent = this.events.get(position);
		//modifyEventController(selectedEvent.getLocalId());
		
		Intent intent = new Intent(this, ModifyEventController.class);
		//intent.putExtra(ModifyCourseController.KEY_COURSE_ID, courseId);
		startActivity(intent);
		    
		
		
		
	}
}
