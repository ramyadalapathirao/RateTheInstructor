package edu.sdsu.cs.ramya.ratetheinstructor;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by sarathbollepalli on 2/28/15.
 */
public class InstructorDetailsFragment extends Fragment
{
    private ArrayList<Comment>  commentsArray;
    private TextView            nameTextView;
    private ListView            commentsListView;
    private TextView            officeTextView;
    private TextView            phoneTextView;
    private TextView            emailTextView;
    private TextView            ratingValue;
    private TextView            instructorTotalRatings;
    private int                 instructorId;
    private String              firstName;
    private String              lastName;
    private String              fullName;
    private String              office;
    private String              phone;
    private String              email;
    private double              avgRating;
    private int                 totalRatings;
    private TextView            emptyMessage;
    private ProgressDialog      progressDialog;
    private SQLiteDatabase      db;

    public static InstructorDetailsFragment newInstance(int id)
    {
        Bundle args=new Bundle();
        args.putInt("instructorId",id);
        InstructorDetailsFragment fragment=new InstructorDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_list,container,false);
        emptyMessage=(TextView)view.findViewById(R.id.empty);
        commentsListView=(ListView)view.findViewById(android.R.id.list);
        View headerView=inflater.inflate(R.layout.list_header,null);
        initializeHeaderViews(headerView);
        return view;
    }

    private void initializeHeaderViews(View headerView)
    {
        nameTextView=(TextView)headerView.findViewById(R.id.nameTextView);
        officeTextView=(TextView)headerView.findViewById(R.id.instructorOffice);
        phoneTextView=(TextView)headerView.findViewById(R.id.instructorPhone);
        emailTextView=(TextView)headerView.findViewById(R.id.instructorEmail);
        instructorTotalRatings=(TextView)headerView.findViewById(R.id.instructorTotalRatings);
        ratingValue=(TextView)headerView.findViewById(R.id.ratingValue);
        commentsListView.addHeaderView(headerView,null,false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        commentsArray = new ArrayList<>();
        instructorId=getArguments().getInt("instructorId");
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getString(R.string.progress_message));
        progressDialog.show();
        if(isOnline())
        {
            getInstructorDetails();
            getInstructorComments();
        }
        else
        {
            InstructorDetailsDbTask commentsDbTask = new InstructorDetailsDbTask("commentsQuery");
            commentsDbTask.execute();
            InstructorDetailsDbTask detailsDbTask=new InstructorDetailsDbTask("detailsQuery");
            detailsDbTask.execute();
        }
    }

    private void getInstructorComments()
    {
        String commentsUrlString = "http://bismarck.sdsu.edu/rateme/comments" + "/" + instructorId;
        RequestQueue requestQueue=VolleySingleton.getsInstance().getRequestQueue();
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                parseJSONInstructorComments(response);
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        };
        JsonArrayRequest request=new JsonArrayRequest(commentsUrlString,success,failure);
        Cache.Entry entry=requestQueue.getCache().get(commentsUrlString);
        if(entry != null)
        {
            try
            {   //Data coming from cache
                JSONArray array= new JSONArray(new String(entry.data,"UTF8"));
                parseJSONInstructorComments(array);

            }
            catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else {
            requestQueue.add(request);
        }
    }

    private void parseJSONInstructorComments(JSONArray response)
    {
        for (int i = 0; i < response.length(); i++)
        {
            try
            {
                JSONObject jsonobject = response.getJSONObject(i);
                String comment = jsonobject.getString("text");
                String date = jsonobject.getString("date");
                int commentId = jsonobject.getInt("id");
                Comment comm = new Comment();
                comm.setComment(comment);
                comm.setDate(date);
                comm.setCommentId(commentId);
                commentsArray.add(comm);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        showComments();
        InstructorDetailsDbTask commentDBTask = new InstructorDetailsDbTask("commentsInsert");
        commentDBTask.execute();
    }

    private void showComments()
    {
        CustomAdapter adapter = new CustomAdapter(getActivity(), commentsArray);
        if(adapter.getCount()>0) {
            commentsListView.setAdapter(adapter);
        }
        else
        {
            emptyMessage.setVisibility(View.VISIBLE);
        }
        progressDialog.dismiss();
    }

    private void getInstructorDetails()
    {
        String urlString = "http://bismarck.sdsu.edu/rateme/instructor" + "/" + instructorId;
        RequestQueue requestQueue=VolleySingleton.getsInstance().getRequestQueue();
        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                parseJSONInstructorDetails(response);
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        };
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET,urlString,null,success,failure);
        Cache.Entry entry=requestQueue.getCache().get(urlString);
        if(entry != null )
        {
            try
            {                //Data coming from cache
                JSONObject detailsObject = new JSONObject(new String(entry.data,"UTF8"));
                parseJSONInstructorDetails(detailsObject);
            }
            catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else {
            requestQueue.add(request);
        }
    }

    private void parseJSONInstructorDetails(JSONObject jsonObject)
    {
        try
        {
        firstName = jsonObject.getString("firstName");
        lastName =jsonObject.getString("lastName");
        fullName = firstName +" "+ lastName;
        office = jsonObject.getString("office");
        phone = jsonObject.getString("phone");
        email =jsonObject.getString("email");
        JSONObject ratingObject=jsonObject.getJSONObject("rating");
        avgRating =ratingObject.getDouble("average");
        totalRatings =ratingObject.getInt("totalRatings");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        updateInstructorDetails();
        InstructorDetailsDbTask detailsDbTask = new InstructorDetailsDbTask("detailsInsert");
        detailsDbTask.execute();
    }

    private void updateInstructorDetails()
    {
        getActivity().setTitle(fullName);
        nameTextView.setText(fullName);
        officeTextView.setText(office);
        phoneTextView.setText(phone);
        emailTextView.setText(email);
        instructorTotalRatings.setText(""+ totalRatings);
        ratingValue.setText(""+(float)Math.round(avgRating * 10) / 10);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    class InstructorDetailsDbTask extends AsyncTask<Void, Void, Void>
    {
        String requestType=null;
        public InstructorDetailsDbTask(String dbRequest)
        {
           this.requestType=dbRequest;
        }
        @Override
        protected Void doInBackground(Void... params)
        {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());
            db = dbHelper.getWritableDatabase();
            if(requestType.equals("detailsQuery"))
            {
                Cursor result = db.rawQuery("select firstName,lastName,office,phone,email,avgRating," +
                        "totalRatings from InstructorsList,InstructorDetails "
                        +"where InstructorsList.id=InstructorDetails.id and " +
                        "InstructorsList.id="+instructorId,null);
                try
                {
                    result.moveToFirst();
                    while (!result.isAfterLast())
                    {
                        firstName = result.getString(0);
                        lastName = result.getString(1);
                        fullName = firstName + " "+ lastName;
                        office = result.getString(2);
                        phone = result.getString(3);
                        email = result.getString(4);
                        avgRating = result.getFloat(5);
                        totalRatings = result.getInt(6);
                        result.moveToNext();
                    }
                }
                finally
                {
                    result.close();
                }
            }
            if(requestType.equals("detailsInsert"))
            {

                ContentValues newRecord = new ContentValues();
                newRecord.put("id",instructorId);
                newRecord.put("office", office);
                newRecord.put("phone", phone);
                newRecord.put("email", email);
                newRecord.put("avgRating", avgRating);
                newRecord.put("totalRatings", totalRatings);
                db.replace("InstructorDetails", null, newRecord);

            }
            if(requestType.equals("commentsQuery"))
            {
                commentsArray.clear();
                Cursor result = db.rawQuery("select commentId,comment,date from " +
                        "InstructorComments where instructorId="+instructorId+" order by commentId DESC", null);
                try
                {
                    result.moveToFirst();
                    while (!result.isAfterLast())
                    {
                        Comment comment = new Comment();
                        comment.setCommentId(result.getInt(0));
                        comment.setComment(result.getString(1));
                        comment.setDate(result.getString(2));
                        commentsArray.add(comment);
                        result.moveToNext();
                    }
                }
                finally
                {
                    result.close();
                }
            }
            if(requestType.equals("commentsInsert"))
            {
                for(int i=0;i< commentsArray.size();i++)
                {
                    ContentValues newRecord = new ContentValues();
                    newRecord.put("commentId", commentsArray.get(i).getCommentId() );
                    newRecord.put("comment", commentsArray.get(i).getComment());
                    newRecord.put("date", commentsArray.get(i).getDate());
                    newRecord.put("instructorId",instructorId);
                    db.replace("InstructorComments", null, newRecord);
                }

            }
            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(requestType.equals("detailsQuery"))
            {
              updateInstructorDetails();
            }
            if(requestType.equals("commentsQuery"))
            {
                showComments();
            }

        }
    }
}
