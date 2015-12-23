package edu.sdsu.cs.ramya.ratetheinstructor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by sarathbollepalli on 3/14/15.
 */
public class AlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener{


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String dialogTitle=getArguments().getString("title");
        String dialogMessage=getArguments().getString("message");
        return new AlertDialog.Builder(getActivity())
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton(R.string.alert_positive_title, this)
                .setIcon(R.drawable.ic_alert_warning)
                .create();
    }

    public static AlertDialogFragment newInstance(String title,String message) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message",message);
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    }
}
