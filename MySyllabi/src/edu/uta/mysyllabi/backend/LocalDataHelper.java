package edu.uta.mysyllabi.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.core.Event;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDataHelper extends SQLiteOpenHelper {
	// Version must be incremented upon schema change!
	public static final int DATABASE_VERSION = 47;
    public static final String DATABASE_NAME = "MyCourses.db";
	
    /* Query details */
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";
    public static final String DELIMITER = ",";
    
    public LocalDataHelper() {
		super(MySyllabi.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
	}
    
	@Override
	public void onCreate(SQLiteDatabase database) {
		createCourseTable(DataContract.Course.TABLE_MAIN, database);
		createCourseTable(DataContract.Course.TABLE_OBSOLETED_BY_LOCAL, database);
		createCourseTable(DataContract.Course.TABLE_OBSOLETED_BY_CLOUD, database);
		
		createEventTable(DataContract.Event.TABLE_MAIN, database);
		createEventTable(DataContract.Event.TABLE_OBSOLETED_BY_LOCAL, database);
		createEventTable(DataContract.Event.TABLE_OBSOLETED_BY_CLOUD, database);
		
		createSettingsTable(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		dropTable(DataContract.Course.TABLE_MAIN, database);
		dropTable(DataContract.Course.TABLE_OBSOLETED_BY_LOCAL, database);
		dropTable(DataContract.Course.TABLE_OBSOLETED_BY_CLOUD, database);
		
		dropTable(DataContract.Event.TABLE_MAIN, database);
		dropTable(DataContract.Event.TABLE_OBSOLETED_BY_LOCAL, database);
		dropTable(DataContract.Event.TABLE_OBSOLETED_BY_CLOUD, database);
		
		dropTable(DataContract.Settings.TABLE_NAME, database);
		onCreate(database);
	}
    
    /* Uses predefined data in the DataContract class as well as the core Course class to
     * initialize a database table for holding Course object data. */
    public void createCourseTable(String tableName, SQLiteDatabase database) {
    	String firstSegment = "CREATE TABLE " + tableName + "(" +
        		DataContract.Course.COLUMN_ID + INT_TYPE + " PRIMARY KEY" + DELIMITER +
        		DataContract.Course.COLUMN_CLOUD_ID + TEXT_TYPE + DELIMITER;
    	
    	StringBuilder middleSegment = new StringBuilder();
    	String[] middleColumns = new Course(null, null).getContentKeys().toArray(new String[]{});
    	
    	for (int i = 0; i < middleColumns.length; i++) {
    		middleSegment.append(middleColumns[i] + TEXT_TYPE + DELIMITER);
    	}
    	
    	String lastSegment = DataContract.Course.COLUMN_LOCKED + TEXT_TYPE + DELIMITER +
    						 DataContract.Course.COLUMN_TIME_UPDATED + INT_TYPE + ")";
    	
    	database.execSQL(firstSegment + middleSegment.toString() + lastSegment);
    }
    
    private void createEventTable(String tableName, SQLiteDatabase database) {
    	String firstSegment = "CREATE TABLE " + tableName + "(" +
        		DataContract.Event.COLUMN_ID + INT_TYPE + " PRIMARY KEY" + DELIMITER +
        		DataContract.Event.COLUMN_CLOUD_ID + TEXT_TYPE + DELIMITER + 
        		DataContract.Event.COLUMN_COURSE_ID + INT_TYPE + DELIMITER;
    	
    	StringBuilder middleSegment = new StringBuilder();
    	String[] middleColumns = new Event(null, null).getContentKeys().toArray(new String[]{});
    	
    	for (int i = 0; i < middleColumns.length; i++) {
    		middleSegment.append(middleColumns[i] + TEXT_TYPE + DELIMITER);
    	}
    	
    	String lastSegment = DataContract.Event.COLUMN_LOCKED + TEXT_TYPE + DELIMITER +
    						 DataContract.Event.COLUMN_TIME_UPDATED + INT_TYPE + ")";
    	
    	database.execSQL(firstSegment + middleSegment.toString() + lastSegment);
    }
    
    /* Uses predefined data from the DataContract class to create the local settings table. */
    public void createSettingsTable(SQLiteDatabase database) {
    	database.execSQL("CREATE TABLE " + DataContract.Settings.TABLE_NAME + "(" +
        		DataContract.Settings.COLUMN_SETTING + TEXT_TYPE + " PRIMARY KEY" + DELIMITER +
        		DataContract.Settings.COLUMN_VALUE + TEXT_TYPE + ")");
    }
    
    /* Drops the specified table from the specified database. */
    private void dropTable(String tableName, SQLiteDatabase database) {
    	database.execSQL("DROP TABLE IF EXISTS " + tableName);
    }
    
    public List<Event> getEvents(String courseId) {
    	List<Event> eventList = new ArrayList<Event>();
    	
    	SQLiteDatabase database = this.getReadableDatabase();
		
		Cursor tableCursor = database.query(DataContract.Event.TABLE_MAIN, null, 
				DataContract.Event.COLUMN_COURSE_ID + " = " + courseId, null, null, null, null);
		
		if (!tableCursor.moveToFirst()) {
			database.close();
			return null; // Return null if cursor is empty.
		}
		
		do {
			eventList.add(getEvent(tableCursor));
		} while (tableCursor.moveToNext());
		
		database.close();
		
    	return eventList;
    }
	
    private Event getEvent(Cursor tableCursor) {
		Event event = new Event(null, null);
		fillContainer(event, tableCursor);
		return event;
	}
    
    private Course getCourse(Cursor tableCursor) {
		Course course = new Course(null, null);
		fillContainer(course, tableCursor);
		return course;
	}

	public String createCourse(Course course) {
		return createCourse(course, DataContract.Course.TABLE_MAIN);
	}
    
	public String createCourse(Course course, String tableName) {
		
		/* If a course with the same cloudId already exists, simply return its localId */
		String courseId;
		if (course.getCloudId() != null) {
			courseId = getLocalId(course.getCloudId(), tableName);
			if (courseId != null) {
				return courseId;
			}
		}
		
		SQLiteDatabase database = this.getWritableDatabase();
		
		ContentValues values = containerToValues(course);
		long id = database.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_IGNORE);
	   	courseId = Long.toString(id);   
	   	
	   	values = new ContentValues();
		values.put(DataContract.Settings.COLUMN_SETTING, DataContract.Settings.KEY_SCHOOL);
		values.put(DataContract.Settings.COLUMN_VALUE, course.getSchool());
		database.replace(DataContract.Settings.TABLE_NAME, null, values);
		
	    database.close();
		
		return courseId;
	}
	
	private ContentValues containerToValues(DataContainer container) {
		ContentValues values = new ContentValues();
		
		if (container.getLocalId() != null) {
			values.put(DataContract.Course.COLUMN_ID, container.getLocalId());
		}
		
		if (container.getCloudId() != null) {
			values.put(DataContract.Course.COLUMN_CLOUD_ID, container.getCloudId());
		}
		
	    if(container.isLocked()) {
	    	values.put(DataContract.Course.COLUMN_LOCKED, DataContract.Course.LOCKED_TRUE);
	    } else {
	    	values.put(DataContract.Course.COLUMN_LOCKED, DataContract.Course.LOCKED_FALSE);
	    }
	    
	    values.put(DataContract.Course.COLUMN_TIME_UPDATED, container.getUpdateTime());
	    
		Map<String, String> courseMap = container.getContentMap();
		List<String> keys = container.getContentKeys();
		String nextValue;
		for (String nextKey : keys) {
			nextValue = courseMap.get(nextKey);
			if (nextValue != null) {
				values.put(nextKey, nextValue);
			}
		}
		
		return values;
	}
	
	private void fillContainer(DataContainer container, Cursor tableCursor) {
		Map<String, String> containerMap = getMap(tableCursor);
		
		container.setLocalId(containerMap.get(DataContract.Course.COLUMN_ID));
		container.setCloudId(containerMap.get(DataContract.Course.COLUMN_CLOUD_ID));
		
		container.addContent(containerMap);
		
		String isLocked = containerMap.get(DataContract.Course.COLUMN_LOCKED);
		if (isLocked.equals(DataContract.Course.LOCKED_TRUE)) {
			container.setLocked(true);
		}
		
		Long updateTime = Long.parseLong(containerMap.get(DataContract.Course.COLUMN_TIME_UPDATED));
		if (updateTime != null) {
			container.setUpdateTime(updateTime);
		}
	}
	
	public String createEvent(Event event, String courseId) {
		return createEvent(event, courseId, DataContract.Event.TABLE_MAIN);
	}
	
	private String createEvent(Event event, String courseId, String tableName) {
		
		SQLiteDatabase database = this.getWritableDatabase();
		
		ContentValues values = containerToValues(event);
		values.put(DataContract.Event.COLUMN_COURSE_ID, courseId);
		long id = database.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_IGNORE);
	   	String eventId = Long.toString(id);
		
	    database.close();
		
		return eventId;
	}
	
	private String getMainTable(DataContainer container) {
		if (container instanceof Course) {
			return DataContract.Course.TABLE_MAIN;
		} else {
			return DataContract.Event.TABLE_MAIN;
		}
	}
	
	private String getLocalTable(DataContainer container) {
		if (container instanceof Course) {
			return DataContract.Course.TABLE_OBSOLETED_BY_LOCAL;
		} else {
			return DataContract.Event.TABLE_OBSOLETED_BY_LOCAL;
		}
	}
	
    /* Updates course data (based on the localId of the provided Course object) in the local
     * database to include the cloudId and timeUpdated stored in the provided Course object. */
	public void linkToCloud(DataContainer container) {
		ContentValues values = new ContentValues();
	    values.put(DataContract.Course.COLUMN_CLOUD_ID, container.getCloudId());
	    values.put(DataContract.Course.COLUMN_TIME_UPDATED, container.getUpdateTime());
	    SQLiteDatabase database = this.getWritableDatabase();
	    database.update(getMainTable(container), values, 
	    		DataContract.Course.COLUMN_ID + " = " + container.getLocalId(), null);
	    database.close();
	    deleteCourse(container.getLocalId(), getLocalTable(container));
	}
	
	/* Saves changes that come from an update to the cloud rather than the local user interface. */
	public void saveFromCloud(Course course) {
		
		/* Store the obsoleted course for the purpose of synchronization, but only if an obsoleted
		 * version of the course does not already exist. */
		Course currentCourse = getCourse(course.getLocalId());
		createCourse(currentCourse, DataContract.Course.TABLE_OBSOLETED_BY_CLOUD);
		
		/* If changes have been made by the user since the last synchronization, merge the changes
		 * rather than overwriting them. */		
		Course oldCourse = getCourse(course.getLocalId(), DataContract.Course.TABLE_OBSOLETED_BY_LOCAL);
		if (oldCourse != null) {
			Map<String, String> changes = oldCourse.getDifferenceMap(course);
			currentCourse.addContent(changes);
			currentCourse.setUpdateTime(course.getUpdateTime());
		} else {
			currentCourse = course;
		}
		
		updateRow(currentCourse, DataContract.Course.TABLE_MAIN);
	}
	
	/* Saves changes that come from an update to the cloud rather than the local user interface. */
	public void saveFromCloud(Event event, String courseId) {
		
		/* Store the obsoleted course for the purpose of synchronization, but only if an obsoleted
		 * version of the event does not already exist. */
		Event currentEvent = getEvent(event.getLocalId());
		createEvent(currentEvent, courseId, DataContract.Event.TABLE_OBSOLETED_BY_CLOUD);
		
		/* If changes have been made by the user since the last synchronization, merge the changes
		 * rather than overwriting them. */
		Event oldEvent = getEvent(event.getLocalId(), DataContract.Event.TABLE_OBSOLETED_BY_LOCAL);
		if (oldEvent != null) {
			Map<String, String> changes = oldEvent.getDifferenceMap(event);
			currentEvent.addContent(changes);
			currentEvent.setUpdateTime(event.getUpdateTime());
		} else {
			currentEvent = event;
		}
		
		updateRow(currentEvent, DataContract.Event.TABLE_MAIN);
	}
	
	public Event getEvent(String localId) {
		return getEvent(localId, DataContract.Event.TABLE_MAIN);
	}
	
	/* Saves changes that come from the local user interface. */
	public void saveFromLocal(Event event, String courseId) {
		/* If there were any outstanding changes from updates to the cloud, they should have
		 * been presented to the user before the change. */
		deleteCourse(event.getLocalId(), DataContract.Event.TABLE_OBSOLETED_BY_CLOUD);
		
		/* Only take action if the user has actually changed something. */
		Event oldEvent = getEvent(event.getLocalId());
		if (oldEvent.sharesContents(event)) {
			return;
		} else {
			updateRow(event, DataContract.Event.TABLE_MAIN);
			
			/* Store the obsoleted course data for the purpose of synchronization, but only if 
			 * an obsoleted version does not already exist. */
			createEvent(oldEvent, courseId, DataContract.Event.TABLE_OBSOLETED_BY_LOCAL);
		}
	}
	
	/* Saves changes that come from the local user interface. */
	public void saveFromLocal(Course course) {
		/* If there were any outstanding changes from updates to the cloud, they should have
		 * been presented to the user before the change. */
		deleteCourse(course.getLocalId(), DataContract.Course.TABLE_OBSOLETED_BY_CLOUD);
		
		/* Only take action if the user has actually changed something. */
		Course oldCourse = getCourse(course.getLocalId());
		if (oldCourse.sharesContents(course)) {
			return;
		} else {
			updateRow(course, DataContract.Course.TABLE_MAIN);
			
			/* Store the obsoleted course data for the purpose of synchronization, but only if 
			 * an obsoleted version does not already exist. */
			createCourse(oldCourse, DataContract.Course.TABLE_OBSOLETED_BY_LOCAL);
		}
	}

	public void updateRow(DataContainer container, String tableName) {
		
		ContentValues values = containerToValues(container);
		
	    String courseId = container.getLocalId();
	    
	    SQLiteDatabase database = this.getWritableDatabase();

	    int rowsUpdated = database.update(tableName, values, 
	    		DataContract.Course.COLUMN_ID + " = " + courseId, null);
	    
	    database.close();
	    
	    if (rowsUpdated < 1) {
	    	throw new SQLiteException("Object with id " + courseId + " does not exist!");
	    }
	}
	
	public Course getCourse(String localId) {
		return getCourse(localId, DataContract.Course.TABLE_MAIN);
	}
	
	public Course getObsoleteCourse(String localId) {
		return getCourse(localId, DataContract.Course.TABLE_OBSOLETED_BY_CLOUD);
	}
	
	public Event getObsoleteEvent(String localId) {
		return getEvent(localId, DataContract.Event.TABLE_OBSOLETED_BY_CLOUD);
	}
	
	private Course getCourse(String localId, String tableName) {
		SQLiteDatabase database = this.getReadableDatabase();
		
		Cursor tableCursor = database.query(tableName, null, 
				DataContract.Course.COLUMN_ID + " = " + localId, null, null, null, null);
		
		if (!tableCursor.moveToFirst()) {
			database.close();
			return null; // Return null if cursor is empty.
		}
		
		Course course = getCourse(tableCursor);
		course.setEvents(getEvents(localId));
		database.close();
		return course;
	}
	
	private Event getEvent(String localId, String tableName) {
		SQLiteDatabase database = this.getReadableDatabase();
		
		Cursor tableCursor = database.query(tableName, null, 
				DataContract.Course.COLUMN_ID + " = " + localId, null, null, null, null);
		
		if (!tableCursor.moveToFirst()) {
			database.close();
			return null; // Return null if cursor is empty.
		}
		
		Event event = getEvent(tableCursor);
		database.close();
		return event;
	}
	
	public List<Course> getAllUpdatedCourses() {
		List<Course> courseList = getAllCourses();
		LinkedList<Course> updateList = new LinkedList<Course>();
		for (Course nextCourse : courseList) {
			if (getObsoleteCourse(nextCourse.getLocalId()) != null) {
				updateList.add(nextCourse);
			}
		}
		return updateList;
	}
	
	public List<Course> getAllCourses() {
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor = database.query(DataContract.Course.TABLE_MAIN, null, null, null, null, null, null);
		
		LinkedList<Course> courseList = new LinkedList<Course>();
		if (!tableCursor.moveToFirst()) {
			database.close();
			return courseList;
		}
		
		do {
			Course course = getCourse(tableCursor);
			course.setEvents(getEvents(course.getLocalId()));
			courseList.add(course);
		} while (tableCursor.moveToNext());
		
		database.close();
		
		return courseList;
	}
	
	public String getLocalId(String cloudId, String tableName) {
		SQLiteDatabase database = this.getReadableDatabase();
		
		String[] column = {DataContract.Course.COLUMN_ID};
		
		Cursor tableCursor = database.query(tableName, column, 
				DataContract.Course.COLUMN_CLOUD_ID + " = ?", new String[]{cloudId}, null, null, null, null);
		
		if (!tableCursor.moveToFirst()) {
			database.close();
			return null;
		}
		
		int localIdIndex = tableCursor.getColumnIndex(DataContract.Course.COLUMN_ID);
		String localId = tableCursor.getString(localIdIndex);
		database.close();
		return localId;
	}
	
	public void deleteCourse(String localId) {
		deleteCourse(localId, DataContract.Course.TABLE_MAIN);
		deleteCourse(localId, DataContract.Course.TABLE_OBSOLETED_BY_CLOUD);
		deleteCourse(localId, DataContract.Course.TABLE_OBSOLETED_BY_LOCAL);
	}
	
	public void deleteCourse(String localId, String tableName) {
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		database.delete(tableName, DataContract.Course.COLUMN_ID + " = " + localId, null);
		database.close();
	}
	
	/* Checks to see if the any local changes have been made since the last successful synchronization. */
	public boolean hasLocalChanges(String localId) {
		Course testCourse = getCourse(localId, DataContract.Course.TABLE_OBSOLETED_BY_LOCAL);
		if (testCourse == null) {
			return false;
		} else {
			return true;
		}
	}
	
	private Map<String, String> getMap(Cursor tableCursor) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] keys = tableCursor.getColumnNames();
		for (int i = 0; i < keys.length; i++) {
			if (tableCursor.getType(i) == Cursor.FIELD_TYPE_STRING) {
				map.put(keys[i], tableCursor.getString(i));
			} else if (tableCursor.getType(i) == Cursor.FIELD_TYPE_INTEGER) {
				map.put(keys[i], Long.toString(tableCursor.getLong(i)));
			}
		}
		return map;
	}
	
	public List<String> getCourseKeys() {
		SQLiteDatabase database = this.getReadableDatabase();
		
		String[] column = {DataContract.Course.COLUMN_ID};
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor = database.query(DataContract.Course.TABLE_MAIN, column, null, null, null, null, null);
		
		ArrayList<String> courseKeys = new ArrayList<String>();
		
		if (!tableCursor.moveToFirst()) {
			database.close();
			return courseKeys; // Return empty array if cursor is empty.
		}
		
		do {
			int nextKey = tableCursor.getInt(0);
			courseKeys.add(Integer.toString(nextKey));
		} while (tableCursor.moveToNext());
		
		database.close();
		return courseKeys;
	}
	
	public String getLatestSchool() {
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor;
		tableCursor = database.query(DataContract.Settings.TABLE_NAME, null, 
				DataContract.Settings.COLUMN_SETTING + " = ?", new String[]{DataContract.Settings.KEY_SCHOOL}, null, null, null);
		
		if (!tableCursor.moveToFirst()) {
			database.close();
			return null; // Return null if cursor is empty.
		}
		
		int schoolIndex = tableCursor.getColumnIndex(DataContract.Settings.COLUMN_VALUE);
		if (tableCursor.isNull(schoolIndex)) {
			database.close();
			return null;
		}
		
		database.close();
		return tableCursor.getString(schoolIndex);
	}

	public boolean hasLocalChanges(Event event) {
		Event testEvent = getEvent(event.getLocalId(), DataContract.Event.TABLE_OBSOLETED_BY_LOCAL);
		if (testEvent == null) {
			return false;
		} else {
			return true;
		}
	}
}
