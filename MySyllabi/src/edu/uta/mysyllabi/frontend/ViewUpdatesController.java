package edu.uta.mysyllabi.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.Controller;
import edu.uta.mysyllabi.core.Course;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ViewUpdatesController extends Activity implements OnItemClickListener {

	private ListView updateList;
	private Controller controller;
	
	private List<Course> cloudList;
	SimpleAdapter listViewAdapter;
	
	public static int REQUEST_FINISH = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_updates);

		this.updateList = (ListView) findViewById(R.id.update_list);
		this.updateList.setOnItemClickListener(this);
		
		this.controller = MySyllabi.getAppController();
		controller.synchronize(null);
		
		refreshView();
	}
	
    public void refreshView() {
    	cloudList = controller.getUpdatedCourses();
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
		listViewAdapter = new SimpleAdapter(this, mapList,
				R.layout.course_item, courseElements, viewElements);
		updateList.setAdapter(listViewAdapter);
    }
    
    public void refreshData() {
    	Controller.CallBack callBack = new Controller.CallBack() {
			@Override
			public void onPostExecute() {
				refreshView();
			}
    	};
    	controller.synchronize(callBack);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	refreshView();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_updates, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        switch (id) {
        case R.id.action_refresh:
        	refreshData();
        	break;
        }
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Course clickedCourse = this.cloudList.get(position);
		modifyCourse(clickedCourse.getLocalId());
	}
	
	public void modifyCourse(String courseId) {
    	Intent intent = new Intent(this, ModifyCourseController.class);
		intent.putExtra(ModifyCourseController.KEY_COURSE_ID, courseId);
		startActivityForResult(intent, REQUEST_FINISH);
    }
}
