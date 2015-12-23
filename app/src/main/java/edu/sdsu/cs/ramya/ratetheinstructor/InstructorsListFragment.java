package edu.sdsu.cs.ramya.ratetheinstructor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * Created by sarathbollepalli on 2/28/15.
 */
public class InstructorsListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<String>  firstNameArray;
    private ArrayList<String>  lastNameArray;
    private ArrayList<String>  fullNameArray;
    private ArrayList<Integer> instructorIdArray;
    private ListView           instructorsList;
    private SQLiteDatabase     db;
    private ProgressDialog     progressDialog;
    private TextView           emptyMessage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.fragment_list,container,false);
        emptyMessage=(TextView)view.findViewById(R.id.empty);
        instructorsList=(ListView)view.findViewById(android.R.id.list);
        instructorsList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        instructorsList.setOnItemClickListener(this);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        firstNameArray =new ArrayList<String>();
        lastNameArray = new ArrayList<String>();
        fullNameArray = new ArrayList<String>();
        instructorIdArray =new ArrayList<>();
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getString(R.string.progress_message));
        progressDialog.show();
        if(isOnline())
        {
            getInstructorData();
        }
        else
        {
            Toast.makeText(getActivity(),R.string.offline,Toast.LENGTH_SHORT).show();
            InstructorListDbTask  dbTask= new InstructorListDbTask("QUERY");
            dbTask.execute();
        }


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getInstructorData()
    {
        RequestQueue requestQueue=VolleySingleton.getsInstance().getRequestQueue();
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                parseJSONInstructorData(response);
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        };
        String urlString = "http://bismarck.sdsu.edu/rateme/list";
        JsonArrayRequest request=new JsonArrayRequest(urlString,success,failure);
        Cache.Entry entry=requestQueue.getCache().get(urlString);
        if(entry != null)
        {
            //Data coming from cache
            try
            {
                JSONArray array= new JSONArray(new String(entry.data,"UTF8"));
                parseJSONInstructorData(array);
            }
            catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else
        {
            //Get data from network
            requestQueue.add(request);
        }
    }

    private void parseJSONInstructorData(JSONArray response)
    {
        for (int i = 0; i < response.length(); i++)
        {
            try
            {
                JSONObject instructorObject = response.getJSONObject(i);
                String firstName = instructorObject.getString("firstName");
                String lastName  = instructorObject.getString("lastName");
                int    id        = instructorObject.getInt("id");
                instructorIdArray.add(id);
                firstNameArray.add(firstName);
                lastNameArray.add(lastName);
                fullNameArray.add(firstName + " " + lastName);

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        showInstructors();
        InstructorListDbTask dbTask = new InstructorListDbTask("INSERT");
        dbTask.execute();
    }

    private void showInstructors()
    {
        progressDialog.dismiss();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, fullNameArray);
        if(adapter.getCount() > 0)
        {
            instructorsList.setAdapter(adapter);
        }
        else
        {
            emptyMessage.setVisibility(View.VISIBLE);
        }
    }

    public boolean isOnline()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int instructorId= instructorIdArray.get(position);
        Intent intent=new Intent(getActivity(),InstructorDetailsActivity.class);
        intent.putExtra("id",instructorId);
        startActivity(intent);
    }

    class InstructorListDbTask extends AsyncTask<Void, Void, Void>
    {
        private String requestType;
        public InstructorListDbTask(String dbRequestType)
        {
           this.requestType = dbRequestType;
        }

        @Override
        protected Void doInBackground(Void... strings)
        {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());
            db =dbHelper.getWritableDatabase();
            if(requestType.equals("QUERY"))
            {
                instructorIdArray.clear();
                Cursor result = db.rawQuery("select id, firstName,lastName from InstructorsList", null);
                try
                {
                    result.moveToFirst();
                    while (!result.isAfterLast())
                    {
                        instructorIdArray.add(result.getInt(0));
                        firstNameArray.add(result.getString(1));
                        lastNameArray.add(result.getString(2));
                        fullNameArray.add(result.getString(1) + " " + result.getString(2));
                        result.moveToNext();
                    }
                }
                finally
                {
                    result.close();
                }
            }
            if(requestType.equals("INSERT"))
            {
                for(int i=0;i< instructorIdArray.size();i++)
                {
                    ContentValues newRecord = new ContentValues();
                    newRecord.put("id", instructorIdArray.get(i) );
                    newRecord.put("firstName", firstNameArray.get(i));
                    newRecord.put("lastName", lastNameArray.get(i));
                    db.replace("InstructorsList", null, newRecord);
                }

            }
          return null;
        }

        protected void onPostExecute(Void result)
        {
          if(requestType.equals("QUERY"))
          {
              showInstructors();
          }

        }
    }
}
