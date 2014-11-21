package edu.uta.mysyllabi.core;

import java.util.Map;

public interface Listable {
	
	public static final String PREVIEW_TITLE = "title";
	
	public static final String PREVIEW_SUBTITLE = "subtitle";
	
	public static final String PREVIEW_HEAD_STRING = "head";
	
	public static final String PREVIEW_TAIL_STRING = "tail";
	
	public Map<String, String> getPreviewMap();
	
}
