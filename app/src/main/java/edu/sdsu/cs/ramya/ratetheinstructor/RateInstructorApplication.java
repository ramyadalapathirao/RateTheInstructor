package edu.sdsu.cs.ramya.ratetheinstructor;

import android.app.Application;
import android.content.Context;

/**
 * Created by sarathbollepalli on 3/6/15.
 */
public class RateInstructorApplication  extends Application {

    private static RateInstructorApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static RateInstructorApplication getInstance()
    {
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }

}
