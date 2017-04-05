package hu.pe.remoiler.remoiler;

import java.net.URL;

/**
 * Responsible for getting information from the server.
 */
final class ServerQueries {

    private final static String LOG_TAG = ServerQueries.class.getSimpleName();

    private static final String BASE_SERVER_URL = "https://remoiler.pe.hu";
    private static URL queryUrl = null;

    // Uncallable constructor
    private ServerQueries() {}

    /**
     * Used to get current status from the server.
     * @return true/false -> on/off.
     */
    /*public static int getStatus() throws IOException {
        Uri baseUri = Uri.parse(BASE_SERVER_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        final String STATUS_PATH = "status";
        // TODO: Get authkey from SharedPreferences.
        String authKey = "7174113503d87e061a0be1e9989e45f2"; // md5(sha1(b7:30:cf:f1:7d:b4))

        // Append path /status/{key}
        uriBuilder.appendPath(STATUS_PATH);
        uriBuilder.appendPath(authKey);

        try {
            queryUrl = new URL(String.valueOf(uriBuilder));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Malformed URL.");
        }

        Log.v(LOG_TAG, "Query URL: " + String.valueOf(queryUrl));

        //int status = new StatusTask().execute(queryUrl);

        /*String response = NetworkUtils.getStringFromURL(queryUrl);

        if (response != null && response.equals("1")) return 1;
        return 0;
    }*/
}
