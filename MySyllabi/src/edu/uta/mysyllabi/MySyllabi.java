package edu.uta.mysyllabi;

import edu.uta.mysyllabi.core.Controller;
import android.app.Application;
import android.content.Context;

public class MySyllabi extends Application {
	
    private static Context context;
    private static Controller controller;

    public void onCreate(){
        super.onCreate();
        MySyllabi.context = getApplicationContext();
        MySyllabi.controller = new Controller();
    }

    public static Context getAppContext() {
        return MySyllabi.context;
    }
    
    public static Controller getAppController() {
    	return controller;
    }

}
