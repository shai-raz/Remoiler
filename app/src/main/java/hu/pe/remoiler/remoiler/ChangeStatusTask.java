package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import cn.pedant.SweetAlert.SweetAlertDialog;

class ChangeStatusTask extends AsyncTask<String, Void, Boolean> {

    final static String LOG_TAG = ChangeStatusTask.class.getSimpleName();

    private Context mContext;
    public ChangeStatusTaskCallback delegate = null;

    ChangeStatusTask(Context context) {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        URL queryUrl = ServerQueries.createURL(ServerQueries.PATH_CHANGE_STATUS);

        try {
            boolean response = NetworkUtils.executeURL(queryUrl, params);
            Log.i(LOG_TAG, "response: " + response);

            return response;

            /*if (!response) {
                return false;
                Log.e(LOG_TAG, "Bad response.");
            }*/
        } catch (IOException e) {
            Log.e(LOG_TAG, "ERROR CONNECTING TO SERVER");
            e.printStackTrace();
            return false;
        }
        //return null;
    }

    @Override
    protected void onPostExecute(Boolean response) {
        if (!response) {
            new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Something went wrong..")
                    .setContentText("Couldn't reach the remoiler, try again later.")
                    .show();
        }
        delegate.postResult(response);
    }
}
