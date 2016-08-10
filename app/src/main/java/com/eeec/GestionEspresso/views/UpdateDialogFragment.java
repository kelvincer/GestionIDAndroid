package com.eeec.GestionEspresso.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.eeec.GestionEspresso.R;

/**
 * Created by rrodriguez on 11/07/16.
 */
public class UpdateDialogFragment extends DialogFragment {

    public static final String TAG = "UpdateDialogFragment";

    private boolean forceUpdate;
    private String messageVersion;

    private UpdateDialogListener listener;

    public UpdateDialogFragment(){
    }

    public void setListener(UpdateDialogListener listener) {
        this.listener = listener;
    }

    public void setMessageVersion(String messageVersion, double newVersion ) {
        String message = messageVersion;
        if( message == null ) message = getResources().getString(R.string.dialog_update_message);
        message = message.replaceAll("\\{\\{version\\}\\}", String.valueOf(newVersion));
        this.messageVersion = message;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_update_title);
        builder.setMessage(messageVersion)
                .setPositiveButton(R.string.dialog_update_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if( listener != null ) listener.onUpdateButtonClicked();
                    }
                });

        if( !forceUpdate ) {
            builder.setNegativeButton(R.string.dialog_update_skip, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if( listener != null ) listener.onSkipButtonClicked();
                }
            });
        }
        return builder.create();
    }


}

