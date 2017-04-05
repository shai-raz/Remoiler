package hu.pe.remoiler.remoiler;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.URL;


public class StatusLoader extends android.support.v4.content.AsyncTaskLoader<Integer> {

    final static String LOG_TAG = StatusLoader.class.getSimpleName();
    URL mUrl;

    public StatusLoader(Context context, URL url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Integer loadInBackground() {
        try {
            String response = NetworkUtils.getStringFromURL(mUrl);
            Log.i(LOG_TAG, "response: " + response);

            if (response != null && !response.equals("") && response.equals("1")) return 1;
            return 0;
        } catch (IOException e) {
            Log.e(LOG_TAG, "ERROR CONNECTING TO SERVER");
            e.printStackTrace();
        }

        return 0;
    }
}
