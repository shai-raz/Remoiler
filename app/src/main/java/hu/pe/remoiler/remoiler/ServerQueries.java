package hu.pe.remoiler.remoiler;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Responsible for getting information from the server.
 */
final class ServerQueries {

    private final static String LOG_TAG = ServerQueries.class.getSimpleName();

    private static final String BASE_SERVER_URL = "http://10.0.0.2/remoiler/public";
    private static URL queryUrl = null;

    // Uncallable constructor
    private ServerQueries() {}

    /**
     * Used to get current status from the server.
     * @return true/false -> on/off.
     */
    /*public static int getStatus() throws IOException {
        URL queryUrl = null;
        final String BASE_SERVER_URL = "http://10.0.0.2/remoiler/public";
        Uri baseUri = Uri.parse(BASE_SERVER_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        final String STATUS_PATH = "status";
        // TODO: Get authkey from SharedPreferences.
        String authKey = "shai"; // md5(sha1(b7:30:cf:f1:7d:b4))

        // Append path /status/{key}
        uriBuilder.appendPath(STATUS_PATH);
        uriBuilder.appendPath(authKey);

        Log.i(LOG_TAG, "URL: " + String.valueOf(uriBuilder));

        try {
            queryUrl = new URL(String.valueOf(uriBuilder));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Malformed URL.");
        }

        //Log.i(LOG_TAG, String.valueOf(queryUrl));

        if (queryUrl == null) return null;
        return queryUrl;
    }*/

    static URL createURL(String path, String... params) {
        Uri baseUri = Uri.parse(BASE_SERVER_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //Temporary Auth Key
        String authKey = "raz";

        uriBuilder.appendPath(path);
        uriBuilder.appendPath(authKey);

        switch (path) {
            case "status":
                break;
            case "change_status":
                uriBuilder.appendPath(params[0]);
                break;
        }

        try {
            queryUrl = new URL(String.valueOf(uriBuilder));
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Malformed URL.");
            e.printStackTrace();
        }

        if (queryUrl == null) return null;
        return queryUrl;
    }

    static String getAuthKeyByUserPass(String user, String pass) {
        // TODO: get key from server

        // Temporary return
        return "raz";
    }
}
