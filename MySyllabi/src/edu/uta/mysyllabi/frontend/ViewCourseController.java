package edu.uta.mysyllabi.frontend;

import java.util.List;

import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.Controller;
import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.core.Instructor;
import edu.uta.mysyllabi.core.WeeklyMeeting;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ViewCourseController extends ActionBarActivity {
	private Controller controller;
	
	public static final String KEY_COURSE_ID = "course_id";
	public static final String KEY_PAGER_INDEX = "pager_index";
	public static final String KEY_MODIFY_COURSE = "modify_course";
	public static int REQUEST_FINISH = 1;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    CoursePagerAdapter pagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager pager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);
        this.controller = MySyllabi.getAppController();

        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        pagerAdapter = new CoursePagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        
        Bundle extras = getIntent().getExtras();
        
        if (extras != null) {
        	String courseId = extras.getString(KEY_COURSE_ID);
        	if (courseId != null) {
        		pager.setCurrentItem(pagerAdapter.getCourseIndex(courseId));
        	}
        	Boolean modifyCourse = extras.getBoolean(KEY_MODIFY_COURSE);
        	if (modifyCourse) {
            	modifyCourse(courseId);
            }
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent newIntent;
        
        switch (id) {
        case R.id.action_settings:
        	return true;
        case R.id.action_create_course:
        	 newIntent = new Intent(this, CreateCourseController.class);
    		startActivity(newIntent);
    		break;
        case R.id.action_delete_course:
        	if (pager.getChildCount() > 0) {
        		int pagerPosition = pager.getCurrentItem();
            	controller.deleteCourse(pagerAdapter.courseList.get(pagerPosition));
            	pagerAdapter = new CoursePagerAdapter(getSupportFragmentManager());
                pager.setAdapter(pagerAdapter);
                if (pagerPosition > 1) {
                	pager.setCurrentItem(pagerPosition - 1);
                }
        	}
        	break;
        case R.id.action_modify_course:
        	if (pager.getChildCount() > 0) {
        		String courseId = pagerAdapter.courseList.get(pager.getCurrentItem());
            	modifyCourse(courseId);
        	}
        	break;
        case R.id.action_view_updates:
        	newIntent = new Intent(this, ViewUpdatesController.class);
    		startActivity(newIntent);
    		break;
        case R.id.action_refresh:
        	refreshData();
        	break;
        case R.id.action_list_events:
        	newIntent = new Intent(this, ListEventsController.class);
    		startActivity(newIntent);
    		break;
        case R.id.action_create_event:
	       	newIntent = new Intent(this, ModifyEventController.class);
	   		startActivity(newIntent);
	   		break;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	refreshView();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FINISH && resultCode == RESULT_OK) {
        	this.finish();
        }
    }
    
    public void refreshView() {
    	int currentIndex = pager.getCurrentItem();
    	pagerAdapter = new CoursePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(currentIndex);
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
    
    public void modifyCourse(String courseId) {
    	Intent intent = new Intent(this, ModifyCourseController.class);
		intent.putExtra(ModifyCourseController.KEY_COURSE_ID, courseId);
		startActivityForResult(intent, REQUEST_FINISH);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the courses.
     */
    public class CoursePagerAdapter extends FragmentStatePagerAdapter {
    	protected List<String> courseList;
    	
    	public CoursePagerAdapter(FragmentManager fm) {
    		super(fm);
    		this.courseList = controller.getCourseList();
    	}

    	@Override
    	public Fragment getItem(int index) {
    		Fragment fragment = new CourseFragment();
    		Bundle fragmentBundle = new Bundle();
    		fragmentBundle.putString(ViewCourseController.KEY_COURSE_ID, courseList.get(index).toString());
    		fragment.setArguments(fragmentBundle);
    		return fragment;
    	}

    	@Override
    	public int getCount() {
    		return courseList.size();
    	}
    	
    	public int getCourseIndex(String courseId) {
    		for (int i = 0; i < courseList.size(); i++) {
    			if (courseId.equals(courseList.get(i))) {
    				return i;
    			}
    		}
    		return 0;
    	}

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CourseFragment extends Fragment {
    	private Course course;
    	private Controller controller;
    	private View root;
    	
    	@Override
        public View onCreateView(LayoutInflater inflater,
                ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_select_course, container, false);
            String courseId = getArguments().getString(KEY_COURSE_ID);
            
            this.root = rootView;
            this.controller = new Controller();
            course = controller.getCourse(courseId);
            
            setText(R.id.view_course_name, course.getName());
            setText(R.id.view_course_title, course.getTitle());
            
            Instructor instructor = course.getInstructor();
            if (instructor.isValid()) {
            	
            	setText(R.id.view_instructor_name, instructor.getName());
            	setText(R.id.view_instructor_email, instructor.getEmailAddress());
            	setText(R.id.view_instructor_office, instructor.getOfficeId());
            	setText(R.id.view_instructor_phone, instructor.getPhoneNumber());
            	
            } else {
            	
            	hideView(R.id.heading_instructor_contact);
            	hideView(R.id.view_instructor_name);
            	hideView(R.id.view_instructor_email);
            	hideView(R.id.view_instructor_office);
            	hideView(R.id.view_instructor_phone);
            	
            }
            
            WeeklyMeeting meeting = course.getMeeting();
            if (meeting != null && meeting.getStartTime() != null) {
            	
            	setText(R.id.view_classroom, meeting.getLocation());
            	setText(R.id.view_meeting_time, meeting.getOccurrence());
            	
            } else {
            	
            	hideView(R.id.heading_course_meeting);
            	hideView(R.id.view_classroom);
            	hideView(R.id.view_meeting_time);
            	
            }
            
            return rootView;
        }
    	
    	private void setText(int viewId, String text) {
    		if (text != null && text.length() > 0) {
    			((TextView) root.findViewById(viewId)).setText(text);
    		} else {
    			hideView(viewId);
    		}
    	}
    	
    	private void hideView(int viewId) {
    		((ViewGroup) root.findViewById(viewId).getParent()).setVisibility(View.GONE);
    	}
    	
    }

}
