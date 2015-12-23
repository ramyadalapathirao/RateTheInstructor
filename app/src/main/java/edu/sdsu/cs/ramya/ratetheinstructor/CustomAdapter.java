package edu.sdsu.cs.ramya.ratetheinstructor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sarathbollepalli on 2/27/15.
 */
public class CustomAdapter extends ArrayAdapter{
    private ArrayList<Comment> comments;
    public CustomAdapter(Context context,ArrayList<Comment> objects) {
        super(context,R.layout.comments_list_row,objects);
        this.comments=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.comments_list_row,parent,false);
            TextView date=(TextView)convertView.findViewById(R.id.dateTextView);
            TextView comment=(TextView)convertView.findViewById(R.id.commentTextView);
            comment.setText(comments.get(position).getComment());
            date.setText(comments.get(position).getDate());
        }
        else {
            TextView date=(TextView)convertView.findViewById(R.id.dateTextView);
            TextView comment=(TextView)convertView.findViewById(R.id.commentTextView);
            comment.setText(comments.get(position).getComment());
            date.setText(comments.get(position).getDate());
        }
        return convertView;
    }

}
