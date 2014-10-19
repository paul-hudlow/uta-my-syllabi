package edu.uta.mysyllabi.frontend;

import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.datatypes.TimeOfDay;
import edu.uta.mysyllabi.datatypes.WeeklyMeeting;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class SetTimeDialogFragment extends DialogFragment
		implements TimePickerDialog.OnTimeSetListener, View.OnClickListener {
	public static final String TAG_STRING = "set_time";
	private Activity activity;
	private TimeOfDay startTime;
	private TextView hostView;
	private WeeklyMeeting meeting;
	private boolean isDuration;
	private boolean is24HourFormat;
	
	public SetTimeDialogFragment(Activity activity, WeeklyMeeting meeting, boolean isDuration) {
		super();
		if (activity == null) {
			throw new NullPointerException();
		}
		this.activity = activity;
		this.meeting = meeting;
		
		if (isDuration) {
			this.startTime = new TimeOfDay(meeting.getDuration());
		} else {
			this.startTime = meeting.getStartTime();
		}
		
		this.isDuration = isDuration;
	}
	
	@Override
	public void onClick(View view) {
		this.hostView = (TextView) view;
		this.show(activity.getFragmentManager(), TAG_STRING);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the start time as the default values for the picker
		int hour = 12;
		int minute = 0;
		if (startTime != null) {
			hour = startTime.getHour();
			minute = startTime.getMinute();
		} else if (isDuration) {
			hour = 0;
		}
		
		if (!isDuration) {
			is24HourFormat = DateFormat.is24HourFormat(getActivity());
		} else {
			is24HourFormat = true;
		}
		
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, is24HourFormat);
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		TimeOfDay time = new TimeOfDay(hourOfDay, minute);
		
		boolean is24HourFormat = true;
		if (!isDuration) {
			is24HourFormat = DateFormat.is24HourFormat(getActivity());
		}
		
		hostView.setText(time.toString(is24HourFormat));
		hostView.setTextColor(getResources().getColor(R.color.black));
		
		if (meeting != null) {
			if (isDuration) {
				meeting.setDuration(time.getTotalMinutes());
			} else {
				meeting.setStartTime(time);
			}
		}
	}
	

	
}