package edu.sdsu.cs.ramya.ratetheinstructor;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class InstructorsListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_container);
        setTitle(R.string.title_activity_instructor_list);
        if(savedInstanceState ==null) {
            FragmentManager listManager = getSupportFragmentManager();
            FragmentTransaction transaction = listManager.beginTransaction();
            InstructorsListFragment listFragment = new InstructorsListFragment();
            transaction.add(R.id.layout_container, listFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instructors_list, menu);
        return true;
    }

}
