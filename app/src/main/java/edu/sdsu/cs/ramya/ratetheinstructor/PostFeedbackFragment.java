package edu.sdsu.cs.ramya.ratetheinstructor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by sarathbollepalli on 2/28/15.
 */
public class PostFeedbackFragment extends Fragment {
    private EditText userComments;
    private RatingBar userRating;
    boolean shouldPostComment;
    boolean shouldPostRating;
    boolean isCommentPosted;
    boolean isRatingPosted;
    private ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View v=inflater.inflate(R.layout.fragment_feedback,container,false);
        userComments=(EditText)v.findViewById(R.id.comments);
        userComments.setMovementMethod(new ScrollingMovementMethod());
        userComments.setMaxLines(5);
        userRating=(RatingBar)v.findViewById(R.id.ratingBar);
        userRating.setFocusable(false);
        userRating.setFocusableInTouchMode(false);
        final Button postFeedback=(Button)v.findViewById(R.id.postButton);
        progressDialog=new ProgressDialog(getActivity());
        userComments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                }
            }
        });
        postFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                sendFeedback();
            }
        });

        return v;
    }

    private void sendFeedback() {
        isCommentPosted = false;
        isRatingPosted  = false;
        shouldPostRating = false;
        shouldPostComment = false;
        if(!isOnline())
        {
            AlertDialogFragment alertDialog=AlertDialogFragment.newInstance(getString(R.string.alert_title),getString(R.string.offline_message));
            alertDialog.show(getFragmentManager(),null);
        }
        else
        {
            int rating=(int)userRating.getRating();
            final String commentToPost=userComments.getText().toString();
            Log.d("check",commentToPost);
            if(!(commentToPost.equals("")) || rating !=0)
            {
                postCommentFeedback(commentToPost);
                postRating(rating);
            }
            else
            {
                AlertDialogFragment alertDialog=AlertDialogFragment.newInstance("Input Error",getString(R.string.feedback_error));
                alertDialog.show(getFragmentManager(),null);
            }
        }
    }

    private void showToastMessage(int message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

    private void postRating(final int ratingToPost)
    {
        final int instructorId=getArguments().getInt("id");
        if(ratingToPost!=0)
        {
            shouldPostRating = true;
            String urlString="http://bismarck.sdsu.edu/rateme/rating/"+instructorId+"/"+ratingToPost;
            final RequestQueue requestQueue=VolleySingleton.getsInstance().getRequestQueue();

            progressDialog.setTitle(R.string.posting);
            progressDialog.setMessage(getString(R.string.progress_message));
            progressDialog.show();
            Response.Listener<String> success = new Response.Listener<String>()
            {
                public void onResponse(String response)
                {
                    /*clear the cache related to details */
                    String detailsUrlString = "http://bismarck.sdsu.edu/rateme/instructor" + "/" + instructorId;
                    requestQueue.getCache().remove(detailsUrlString);
                    isRatingPosted = true;
                    userRating.setRating(0);
                    checkPostStatus();

                }
            };
            Response.ErrorListener failure = new Response.ErrorListener()
            {
                public void onErrorResponse(VolleyError error)
                {
                    Log.d("rew", error.toString());
                }
            };
            StringRequest request= new StringRequest(urlString,success,failure);
            requestQueue.add(request);
        }
    }

    private void checkPostStatus()
    {
        if(shouldPostComment && shouldPostRating)
        {
            if(isCommentPosted && isRatingPosted)
            {
                showToastMessage(R.string.post_Status);
                progressDialog.dismiss();
            }
        }
        else
        {
            if(shouldPostComment && isCommentPosted)
            {
                showToastMessage(R.string.post_Status);
                progressDialog.dismiss();

            }
            if(shouldPostRating && isRatingPosted)
            {
                showToastMessage(R.string.post_Status);
                progressDialog.dismiss();
            }
        }
    }

    private void postCommentFeedback(final String commentToPost)
    {
        final int instructorId=getArguments().getInt("id");
        String commentURL="http://bismarck.sdsu.edu/rateme/comment/"+instructorId;
        if(!commentToPost.equals(""))
        {
            shouldPostComment = true;
            final RequestQueue requestQueue=VolleySingleton.getsInstance().getRequestQueue();
           progressDialog.setTitle(R.string.posting);
           progressDialog.setMessage(getString(R.string.progress_message));
           progressDialog.show();
            Response.Listener<String> success = new Response.Listener<String>()
            {
                public void onResponse(String response) {
                    //Log.d("postResponse", response);
                    String commentsUrlString = "http://bismarck.sdsu.edu/rateme/comments" + "/" + instructorId;
                    requestQueue.getCache().remove(commentsUrlString);
                    isCommentPosted = true;
                    userComments.setText("");
                    checkPostStatus();
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener()
            {
                public void onErrorResponse(VolleyError error)
                {
                    Log.d("rew", error.toString());
                }
            };
            StringRequest request= new StringRequest(Request.Method.POST,commentURL,success,failure)
            {
                public byte[] getBody()  {

                    return commentToPost.getBytes();
                }
            };
            requestQueue.add(request);
        }
    }


    public static PostFeedbackFragment newInstance(int instructorId)
    {
        Bundle args=new Bundle();
        args.putInt("id",instructorId);
        PostFeedbackFragment fragment=new PostFeedbackFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public boolean isOnline()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void hideKeyboard() {
        InputMethodManager keyboardManager=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (keyboardManager.isAcceptingText()) {
            keyboardManager.hideSoftInputFromWindow(userComments.getWindowToken(), 0);
        }
    }
}
