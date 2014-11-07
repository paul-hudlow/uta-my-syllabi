package edu.uta.mysyllabi.datatypes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Instructor {
	private String firstName;
	private String lastName;
	private String office;
	private String phoneNumber;
	private String emailAddress;
	protected WeeklyMeeting officeHours = new WeeklyMeeting();
	
	private final String FIRST_NAME = "first_name";
	private final String LAST_NAME = "last_name";
	private final String EMAIL = "email";
	private final String PHONE = "phone";
	private final String OFFICE = "office";
	private final String OFFICE_HOURS_PREFIX = "office_hours_";
	
	private final String[] contentKeys = {
		FIRST_NAME,
		LAST_NAME,
		EMAIL,
		PHONE,
		OFFICE
	};
	
	public Instructor() {
		super();
	}
	
	public boolean isValid() {
		if (lastName != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public Instructor(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public Instructor(HashMap<String, String> map, String keyPrefix) {
		this.firstName = map.get(keyPrefix + FIRST_NAME);
		this.lastName = map.get(keyPrefix + LAST_NAME);
	}
	
	public Map<String, String> getContentMap(String keyPrefix) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(keyPrefix + FIRST_NAME, this.firstName);
		map.put(keyPrefix + LAST_NAME, this.lastName);
		map.put(keyPrefix + EMAIL, this.emailAddress);
		map.put(keyPrefix + PHONE, this.phoneNumber);
		map.put(keyPrefix + OFFICE, this.office);
		
		map.putAll(officeHours.getContentMap(keyPrefix + OFFICE_HOURS_PREFIX));
		
		return map;
	}
	
	public List<String> getContentKeys(String keyPrefix) {
		LinkedList<String> keyList = new LinkedList<String>();
		for (int i = 0; i < contentKeys.length; i++) {
			keyList.add(keyPrefix + contentKeys[i]);
		}
		keyList.addAll(this.officeHours.getContentKeys(keyPrefix + OFFICE_HOURS_PREFIX));
		return keyList;
	}

	public void addContentFromMap(Map<String, String> map, String keyPrefix) {
		this.firstName = map.get(keyPrefix + FIRST_NAME);
		this.lastName = map.get(keyPrefix + LAST_NAME);
		this.emailAddress = map.get(keyPrefix + EMAIL);
		this.phoneNumber = map.get(keyPrefix + PHONE);
		this.office = map.get(keyPrefix + OFFICE);
		
		this.officeHours.addContentFromMap(map, keyPrefix + OFFICE_HOURS_PREFIX);
	}
	
	public String getName() {
		String fullName = "";
		if (firstName != null) {
			fullName += firstName + " "; // Add first name only if it exists.
		}
		fullName += lastName; // Always add last name; it must exist.
		return fullName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName.replace(" ", "");
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName.replace(" ", "");
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getOfficeId() {
		return office;
	}
	
	public void setOfficeId(String officeId) {
		this.office = officeId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public String getOfficeHoursStart() {
		if (officeHours == null) {
			return null;
		}
		return officeHours.getStartTime().toString();
	}
	
	public WeeklyMeeting getOfficeHours() {
		return this.officeHours;
	}
	
	public void setOfficeHours(WeeklyMeeting officeHours) {
		this.officeHours = officeHours;
	}
	
}
