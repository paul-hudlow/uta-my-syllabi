package edu.uta.mysyllabi;

import android.app.Application;
import android.content.Context;

public class MySyllabi extends Application {
	
    private static Context context;

    public void onCreate(){
        super.onCreate();
        MySyllabi.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MySyllabi.context;
    }

}
