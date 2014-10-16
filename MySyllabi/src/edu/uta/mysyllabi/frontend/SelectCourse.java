package edu.uta.mysyllabi.frontend;

import java.util.ArrayList;

import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.Course;

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


public class SelectCourse extends ActionBarActivity {

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

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pagerAdapter = new CoursePagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_course, menu);
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
        if (id == R.id.action_create_course) {
    		Intent intent = new Intent(this, CreateCourseController.class);
    		startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class CoursePagerAdapter extends FragmentStatePagerAdapter {
    	// List of bundles; one for each course.
    	private ArrayList<Bundle> syllabusBundles;
    	
    	public CoursePagerAdapter(FragmentManager fm) {
    		super(fm);
    		// LocalDataHelper helper = new LocalDataHelper();
    		this.syllabusBundles = new ArrayList<Bundle>();
    	}

    	@Override
    	public Fragment getItem(int index) {
    		Fragment fragment = new CourseFragment();
    		fragment.setArguments(syllabusBundles.get(index));
    		return fragment;
    	}

    	@Override
    	public int getCount() {
    		return syllabusBundles.size();
    	}

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CourseFragment extends Fragment {
    	
    	@Override
        public View onCreateView(LayoutInflater inflater,
                ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_select_course, container, false);
            // Bundle syllabusBundle = getArguments();
            
            Course syllabus = new Course(null, null);

            TextView nextView;

            nextView = (TextView) rootView.findViewById(R.id.view_course_name);
            //nextView.setText(syllabus.name);

            nextView = (TextView) rootView.findViewById(R.id.view_course_title);
            //nextView.setText(syllabus.title);
            
            /*if (syllabus.meeting != null) {
	            nextView = (TextView) rootView.findViewById(R.id.view_meeting_time);
	            nextView.setText(syllabus.meeting.getOccurence());
            }

            nextView = (TextView) rootView.findViewById(R.id.view_instructor_name);
            nextView.setText(syllabus.instructor.getName()); */
            
            return rootView;
        }
    	
    }

}
