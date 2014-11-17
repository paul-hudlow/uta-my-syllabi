package edu.uta.mysyllabi.frontend;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.WeeklyMeeting;

public class SelectDaysDialogFragment extends DialogFragment implements View.OnClickListener {
	public static final String TAG_STRING = "select_days";
	
	private Activity activity;
	private TextView hostView;
	private WeeklyMeeting meeting;
	private View dialogView;

	
	public SelectDaysDialogFragment(Activity activity, WeeklyMeeting meeting) {
		super();
		if (activity == null || meeting == null) {
			throw new NullPointerException();
		}
		this.activity = activity;
		this.meeting = meeting;
	}
	
	@Override
	public void onClick(View view) {
		hostView = (TextView) view;
		this.show(activity.getFragmentManager(), TAG_STRING);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* Get a builder to prepare the dialog. */
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		/* Inflate the predefined layout. */
		LayoutInflater inflater = getActivity().getLayoutInflater();
		dialogView = inflater.inflate(R.layout.dialog_select_days, null);
		
		builder.setView(dialogView)
			.setTitle("Select Days")
			/* Add action buttons. */
			.setPositiveButton(R.string.okay, 
			new SelectDaysListener())
			.setNegativeButton(R.string.cancel, new CancelDialogListener());	
		
		fillInDays();
		
		return builder.create();
	}
	
	protected class SelectDaysListener implements DialogInterface.OnClickListener {
		
		public void onClick(DialogInterface dialog, int id) {
			ArrayList<Integer> dayList = new ArrayList<Integer>();
			
			if (((CheckBox)dialogView.findViewById(R.id.checkbox_mon)).isChecked()) {
				dayList.add(WeeklyMeeting.MONDAY);
			}
			if (((CheckBox)dialogView.findViewById(R.id.checkbox_tue)).isChecked()) {
				dayList.add(WeeklyMeeting.TUESDAY);
			}
			if (((CheckBox)dialogView.findViewById(R.id.checkbox_wed)).isChecked()) {
				dayList.add(WeeklyMeeting.WEDNESDAY);
			}
			if (((CheckBox)dialogView.findViewById(R.id.checkbox_thu)).isChecked()) {
				dayList.add(WeeklyMeeting.THURSDAY);
			}
			if (((CheckBox)dialogView.findViewById(R.id.checkbox_fri)).isChecked()) {
				dayList.add(WeeklyMeeting.FRIDAY);
			}
			if (((CheckBox)dialogView.findViewById(R.id.checkbox_sat)).isChecked()) {
				dayList.add(WeeklyMeeting.SATURDAY);
			}
			if (((CheckBox)dialogView.findViewById(R.id.checkbox_sun)).isChecked()) {
				dayList.add(WeeklyMeeting.SUNDAY);
			}
			
			meeting.setDaysOfWeek(dayList);
			if (dayList.size() > 0) {
				hostView.setText(meeting.getDaysOfWeek());
				hostView.setTextColor(getResources().getColor(R.color.black));
			} else {
				hostView.setText(R.string.hint_meeting_days);
				hostView.setTextColor(getResources().getColor(R.color.gray));
			}
		}
		
	}
	
	protected class CancelDialogListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int id) {
			SelectDaysDialogFragment.this.getDialog().cancel();
		}
	}
	
	private void fillInDays() {
		if (meeting.isMeetingDay(WeeklyMeeting.MONDAY)) {
			((CheckBox)dialogView.findViewById(R.id.checkbox_mon)).setChecked(true);
		}
		if (meeting.isMeetingDay(WeeklyMeeting.TUESDAY)) {
			((CheckBox)dialogView.findViewById(R.id.checkbox_tue)).setChecked(true);
		}
		if (meeting.isMeetingDay(WeeklyMeeting.WEDNESDAY)) {
			((CheckBox)dialogView.findViewById(R.id.checkbox_wed)).setChecked(true);
		}
		if (meeting.isMeetingDay(WeeklyMeeting.THURSDAY)) {
			((CheckBox)dialogView.findViewById(R.id.checkbox_thu)).setChecked(true);
		}
		if (meeting.isMeetingDay(WeeklyMeeting.FRIDAY)) {
			((CheckBox)dialogView.findViewById(R.id.checkbox_fri)).setChecked(true);
		}
		if (meeting.isMeetingDay(WeeklyMeeting.SATURDAY)) {
			((CheckBox)dialogView.findViewById(R.id.checkbox_sat)).setChecked(true);
		}
		if (meeting.isMeetingDay(WeeklyMeeting.SUNDAY)) {
			((CheckBox)dialogView.findViewById(R.id.checkbox_sun)).setChecked(true);
		}
	}
	
}