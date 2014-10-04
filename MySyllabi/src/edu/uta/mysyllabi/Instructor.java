package edu.uta.mysyllabi;

public class Instructor {
	private String prefix;
	private String firstName;
	private String lastName;
	protected WeeklyMeeting officeHours;
	
	public Instructor(String instructorName) {
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
	}
	
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

}
