package edu.uta.mysyllabi.frontend;

import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.TimeOfDay;

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
	private TimeOfDay time;
	private TextView hostView;
	private TimeHolder meeting;
	private boolean isEnd;
	private boolean is24HourFormat;
	
	public SetTimeDialogFragment(Activity activity, TimeHolder meeting, boolean isEnd) {
		super();
		if (activity == null) {
			throw new NullPointerException();
		}
		this.activity = activity;
		this.meeting = meeting;
		this.isEnd = isEnd;
	}
	
	@Override
	public void onClick(View view) {
		this.hostView = (TextView) view;
		this.show(activity.getFragmentManager(), TAG_STRING);
		
		if (isEnd) {
			this.time = meeting.getEndTime();
		} else {
			this.time = meeting.getStartTime();
		}
		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the start time as the default values for the picker
		int hour = 12;
		int minute = 0;
		if (time != null) {
			hour = time.getHour();
			minute = time.getMinute();
		}
		
		is24HourFormat = DateFormat.is24HourFormat(getActivity());
		
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, is24HourFormat);
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		TimeOfDay time = new TimeOfDay(hourOfDay, minute);
		
		hostView.setText(time.toString(is24HourFormat));
		hostView.setTextColor(getResources().getColor(R.color.black));
		
		if (meeting != null) {
			if (isEnd) {
				meeting.setEndTime(time);
			} else {
				meeting.setStartTime(time);
			}
		}
	}
	

	
}