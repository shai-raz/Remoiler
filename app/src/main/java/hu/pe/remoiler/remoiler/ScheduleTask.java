package hu.pe.remoiler.remoiler;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * Used to update server with new/updated schedule.
 */

/*class ScheduleTask extends AsyncTask<String, Void, Boolean> {

    private final static String LOG_TAG = ScheduleTask.class.getSimpleName();

    @Override
    protected Boolean doInBackground(String... params) {
        URL queryUrl = ServerQueries.createURL(ServerQueries.PATH_SCHEDULE);

        try {
            boolean response = NetworkUtils.executeURL(queryUrl, params);
            Log.i(LOG_TAG, "response: " + response);

            return response;
        } catch (IOException e) {
            Log.e(LOG_TAG, "ERROR CONNECTING TO SERVER");
            e.printStackTrace();
            return false;
        }
    }
}*/
