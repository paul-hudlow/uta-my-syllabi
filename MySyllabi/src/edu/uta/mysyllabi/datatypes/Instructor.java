package edu.uta.mysyllabi.datatypes;


public class Instructor {
	private String prefix;
	private String firstName;
	private String lastName;
	private String office;
	private String phoneNumber;
	private String emailAddress;
	protected WeeklyMeeting officeHours;
	
	public Instructor(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	/*public Instructor(String instructorName) {
		String[] nameParts = instructorName.split(" ");
		switch (nameParts.length) {
		case 1:
			this.lastName = nameParts[0];
			break;
		case 2:
			this.firstName = nameParts[0];
			this.lastName = nameParts[1];
			break;
		case 3:
			this.prefix = nameParts[0];
			this.firstName = nameParts[1];
			this.lastName = nameParts[2];
		}
	}*/
	
	public String getName() {
		String fullName = "";
		if (prefix != null) {
			fullName += prefix + " "; // Add prefix only if it exists.
		}
		if (firstName != null) {
			fullName += firstName + " "; // Add first name only if it exists.
		}
		fullName += lastName; // Always add last name; it must exist.
		return fullName;
	}
	
	public String getFirstName() {
		return firstName;
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
	
	public int getOfficeHoursDuration() {
		if (officeHours == null) {
			return 0;
		}
		return officeHours.getDuration();
	}
	
	public WeeklyMeeting getOfficeHours() {
		return this.officeHours;
	}
	
	public void setOfficeHours(WeeklyMeeting officeHours) {
		this.officeHours = officeHours;
	}
}
