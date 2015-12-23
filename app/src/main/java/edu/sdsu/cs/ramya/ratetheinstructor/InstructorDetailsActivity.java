package edu.sdsu.cs.ramya.ratetheinstructor;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class InstructorDetailsActivity extends ActionBarActivity {

    private int instructorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_container);
        Bundle extras=getIntent().getExtras();
        instructorId=extras.getInt("id");
        Log.d("id","id is"+instructorId);
        if(savedInstanceState ==null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            InstructorDetailsFragment fragment = InstructorDetailsFragment.newInstance(instructorId);
            transaction.add(R.id.layout_container, fragment);
            transaction.commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instructor_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id == R.id.feedback)
        {
            Intent intent=new Intent(this,PostFeedbackActivity.class);
            intent.putExtra("id",instructorId);
            startActivity(intent);
            return true;
        }
        if(id == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
