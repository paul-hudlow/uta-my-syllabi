package edu.uta.mysyllabi.core;

import java.util.Map;

public interface Listable {
	
	public static final String PREVIEW_TITLE = "title";
	
	public static final String PREVIEW_SUBTITLE = "subtitle";
	
	public static final String PREVIEW_SECOND_LINE = "second";
	
	public Map<String, String> getPreviewMap();
	
	public interface Detailed extends Listable {
		
		public static final String PREVIEW_THIRD_LINE = "third";
		
		public static final String PREVIEW_FOURTH_LINE = "fourth";
		
	}
	
}
