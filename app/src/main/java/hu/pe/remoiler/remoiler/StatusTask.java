package hu.pe.remoiler.remoiler;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

import static java.security.AccessController.getContext;

/**
 * Get status with a background thread.
 */
public class StatusTask extends AsyncTask<URL, Void, Integer> {

    @Override
    protected Integer doInBackground(URL... params) {
        try {
            String response = NetworkUtils.getStringFromURL(params[0]);

            if (response != null && response.equals("1")) return 1;
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(getContext(), "Error loading data from server.", Toast.LENGTH_SHORT).show();
        }

        return 0;
    }
}
