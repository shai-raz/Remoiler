package hu.pe.remoiler.remoiler;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Responsible for Network Connections to the server
 */
public final class NetworkUtils {

    final private static String LOG_TAG = NetworkUtils.class.getSimpleName();

    // Uncallable constructor
    private NetworkUtils() {}

    static boolean executeURL(URL queryUrl) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            // Making the connection with the URL, using "GET" request method
            urlConnection = (HttpURLConnection) queryUrl.openConnection();
            urlConnection.setReadTimeout(3000);
            urlConnection.setConnectTimeout(3000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Checking if the response code is OK
            if (urlConnection.getResponseCode() != 200) {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                return false;
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the data from server.", e);
            return false;
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return true;
    }

    // Returns json String from given URL
    public static String getStringFromURL(URL queryUrl) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String stringResponse = "";

        try {
            // Making the connection with the URL, using "GET" request method
            urlConnection = (HttpURLConnection) queryUrl.openConnection();
            urlConnection.setReadTimeout(3000);
            urlConnection.setConnectTimeout(3000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Checking if the response code is OK
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                stringResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the data from server.", e);
            return null;
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return stringResponse;
    }

    // Reading the String from the "Stream"
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        return output.toString();
    }

}
