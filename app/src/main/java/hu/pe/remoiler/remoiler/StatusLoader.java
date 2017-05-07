package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.URL;


class StatusLoader extends android.support.v4.content.AsyncTaskLoader<Integer> {

    final static String LOG_TAG = StatusLoader.class.getSimpleName();
    URL mUrl;
    Context mContext;
    String mKey;

    StatusLoader(Context context, URL url, String key) {
        super(context);
        mUrl = url;
        mContext = context;
        mKey = key;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Integer loadInBackground() {
        try {
            String response = NetworkUtils.getStringFromURL(mUrl, mKey);
            Log.i(LOG_TAG, "response: " + response);

            if (response == null) return null;
            else if (response.equals("1")) return 1;
            return 0;
        } catch (IOException e) {
            Log.e(LOG_TAG, "ERROR CONNECTING TO SERVER");
            e.printStackTrace();
        }

        return null;
    }
}
