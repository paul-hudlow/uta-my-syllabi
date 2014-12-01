package edu.uta.mysyllabi.frontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import edu.uta.mysyllabi.R;
import edu.uta.mysyllabi.core.Listable;

public class MySyllabiActivity extends ActionBarActivity {
	
	public SimpleAdapter populateDetailedList(Context context, List<?> listData) {
		ArrayList<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		Listable.Detailed nextListable;
		for (Object nextItem : listData) {
			nextListable = (Listable.Detailed) nextItem;
			mapList.add(nextListable.getPreviewMap());
		}
		String[] eventElements = { 
				Listable.PREVIEW_TITLE,
				Listable.PREVIEW_SUBTITLE, 
				Listable.PREVIEW_SECOND_LINE,
				Listable.Detailed.PREVIEW_THIRD_LINE };
		int[] viewElements = { 
				R.id.preview_title, 
				R.id.preview_subtitle,
				R.id.preview_second_line,
				R.id.preview_third_line };
		
		SimpleAdapter listViewAdapter = new SimpleAdapter(context, mapList, R.layout.detailed_item, eventElements, viewElements);
		
		return listViewAdapter;
	}
	
	public void prepareText(TextView view, String start, String test) {
		if (start != null) {
			view.setText(start);
			view.setTextColor(getResources().getColor(R.color.black));
			if (!start.equals(test)) {
				view.setTextColor(getResources().getColor(R.color.modified));
				view.setOnLongClickListener(new RevertChangeListener(test));
			}
		} else if (test != null) {
			view.setTextColor(getResources().getColor(R.color.modified));
			view.setOnLongClickListener(new RevertChangeListener(test));
		}
	}
	
	protected class RevertChangeListener implements View.OnLongClickListener {
		
		private String backup;
		private boolean reverted = false;
			
		public RevertChangeListener(String backup) {
			this.backup = backup;
		}
			
		@Override
		public boolean onLongClick(View view) {
			String current = ((TextView)view).getText().toString();
			((TextView)view).setText(backup);
			backup = current;
			if (reverted) {
			((TextView)view).setTextColor(getResources().getColor(R.color.modified));
			reverted = false;
			} else {
				((TextView)view).setTextColor(getResources().getColor(R.color.black));
				reverted = true;
			}
			return true;
		}
			
	}
	
}
