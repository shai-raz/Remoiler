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

    private static final String BASE_SERVER_URL = "https://remolier.000webhostapp.com/public/";
    private static URL queryUrl = null;

    public final static int PATH_GET_STATUS = 601;
    public final static int PATH_CHANGE_STATUS = 602;

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

    static URL createURL(int path) {
        Uri baseUri = Uri.parse(BASE_SERVER_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //Temporary Auth Key
        //String authKey = "raz";

        switch (path) {
            case PATH_GET_STATUS:
                uriBuilder.appendPath("status");
                break;
            case PATH_CHANGE_STATUS:
                uriBuilder.appendPath("change_status");
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
}
