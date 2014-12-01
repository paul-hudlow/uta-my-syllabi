package edu.uta.mysyllabi.frontend;

import edu.uta.mysyllabi.core.TimeOfDay;

public interface TimeHolder {
	public void setStartTime(TimeOfDay time);
	
	public void setEndTime(TimeOfDay time);
	
	public TimeOfDay getStartTime();
	
	public TimeOfDay getEndTime();
}
