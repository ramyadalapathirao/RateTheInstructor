package edu.sdsu.cs.ramya.ratetheinstructor;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
/**
 * Created by sarathbollepalli on 3/6/15.
 */
public class VolleySingleton {
    private static VolleySingleton sInstance=null;
    private RequestQueue mRequestQueue;
    private VolleySingleton()
    {
        mRequestQueue = Volley.newRequestQueue
                (RateInstructorApplication.getAppContext());
    }
    public static VolleySingleton getsInstance()
    {
        if(sInstance == null)
        {
            sInstance = new VolleySingleton();
        }
        return sInstance;
    }
    public RequestQueue getRequestQueue()
    {

        return mRequestQueue;
    }
}

