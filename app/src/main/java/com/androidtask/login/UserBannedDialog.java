package com.androidtask.login;



import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.androidtask.R;

/**
 * Created by vova on 27.06.17.
 */

public class UserBannedDialog extends DialogFragment {
    private static final String BAN_TIME = "ban_time";
    private static final String BAN_REASON = "ban_reason";

    public static UserBannedDialog newInstance(String banTime, String banReason) {
        UserBannedDialog frag = new UserBannedDialog();
        Bundle args = new Bundle();
        args.putString(BAN_TIME, banTime);
        args.putString(BAN_REASON, banReason);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.user_ban_fragment, null);
        TextView banT = (TextView) view.findViewById(R.id.user_banned_time);
        TextView banR = (TextView) view.findViewById(R.id.user_banned_reason);

        banT.setText(getArguments().getString(BAN_TIME));
        banR.setText(getArguments().getString(BAN_REASON));

        builder.setView(view)
                .setTitle(R.string.user_ban_dialog_title)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

}
