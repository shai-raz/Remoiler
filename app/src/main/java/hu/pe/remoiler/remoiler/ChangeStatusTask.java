package hu.pe.remoiler.remoiler;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

public class ChangeStatusTask extends AsyncTask<String, Void, Void> {

    final static String LOG_TAG = ChangeStatusTask.class.getSimpleName();

    @Override
    protected Void doInBackground(String... params) {
        URL queryUrl = ServerQueries.createURL("change_status", params[0]);

        try {
            boolean response = NetworkUtils.executeURL(queryUrl);
            Log.i(LOG_TAG, "response: " + response);

            if (!response) {
                Log.e(LOG_TAG, "Bad response.");
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "ERROR CONNECTING TO SERVER");
            e.printStackTrace();
        }
        return null;
    }
}
