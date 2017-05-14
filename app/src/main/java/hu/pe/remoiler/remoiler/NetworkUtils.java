package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Responsible for Network Connections to the server
 */
final class NetworkUtils {

    final private static String LOG_TAG = NetworkUtils.class.getSimpleName();

    // Uncallable constructor
    private NetworkUtils() {}

    /**
     * Checks if connected to an online Wi-Fi, and returns its SSID.
     * @param context Activity context.
     * @return null not online, SSID (String) if online.
     */
    static String getWifiSSID(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if( wifiInfo.getNetworkId() == -1 ){
                Log.i(LOG_TAG, "Wifi state: No Access point.");
                return null; // Not connected to an access point
            }
            Log.i(LOG_TAG, "Wifi state: Online.");
            return wifiInfo.getSSID(); // Connected to an access point
        }
        else {
            Log.i(LOG_TAG, "Wifi state: Adapter is OFF.");
            return null; // Wi-Fi adapter is OFF
        }
    }

    /**
     * Checks wither internet is reachable.
     * @return Boolean for wither internet is reachable or not.
     */
    static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    /**
     * Used to execute a given URL address with given params using POST method.
     * @param queryUrl Full URL address that should be executed.
     * @param params The parameters to be sent through POST.
     * @return Returns boolean for wither the URL was executed successfully.
     * @throws IOException inputStream.close() IOException
     */
    static boolean executeURL(URL queryUrl, String... params) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        Log.i(LOG_TAG, "queryUrl=" + queryUrl);
        Log.i(LOG_TAG, "params=" + params[0] + " , " + Arrays.toString(params));

        try {
            // Making the connection with the URL, using "POST" request method
            urlConnection = (HttpURLConnection) queryUrl.openConnection();
            urlConnection.setReadTimeout(3000);
            urlConnection.setConnectTimeout(3000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            DataOutputStream dStream = new DataOutputStream(urlConnection.getOutputStream());
            String urlParameters = "";
            for (String param : params) {
                urlParameters += param + "&";
            }
            dStream.writeBytes(urlParameters);
            dStream.flush();
            dStream.close();

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

    // Returns String from given URL
    public static String getStringFromURL(URL queryUrl, String key) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String stringResponse = "";

        try {
            // Making the connection with the URL, using "GET" request method
            urlConnection = (HttpURLConnection) queryUrl.openConnection();
            urlConnection.setReadTimeout(3000);
            urlConnection.setConnectTimeout(3000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            DataOutputStream dStream = new DataOutputStream(urlConnection.getOutputStream());
            String urlParameters = "key=" + key;
            dStream.writeBytes(urlParameters);
            dStream.flush();

            dStream.close();

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
