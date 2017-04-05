package hu.pe.remoiler.remoiler;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.net.URL;


public class StatusLoader extends AsyncTaskLoader<Integer> {

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

            if (response != null && !response.equals("") && response.equals("1")) return 1;
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(getContext(), "Error loading data from server.", Toast.LENGTH_SHORT).show();
        }

        return 0;
    }
}
