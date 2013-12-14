package com.nttuyen.android.umon.core.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

@Deprecated
public class UIContextHelper {
    private final Context context;
    private ProgressDialog progressDialog = null;

    public UIContextHelper(Context context) {
        this.context = context;
    }

    public void showLoading() {
        this.showLoading("Loading...", false);
    }

    public void showLoading(String message, boolean cancelable) {
        if(this.context == null) return;
        progressDialog = new ProgressDialog(this.context);
        if(this.context instanceof Activity) {
            progressDialog.setOwnerActivity((Activity)this.context);
        }
        progressDialog.setMessage(message);
        progressDialog.setCancelable(cancelable);
        progressDialog.show();
    }

    public void dismissLoading() {
        if(this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    public void showErrDialog(String title, String message) {
        if(this.context == null) return;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.context);
        dialog.setMessage(message)
				.setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }
}
