package hu.pe.remoiler.remoiler;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class SwitchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Integer> {

    // LOG TAG Constant
    private final String LOG_TAG = SwitchFragment.class.getSimpleName();

    // Constants that hold on/off src
    private final int SWITCH_ON_BUTTON = R.drawable.switch_on_button;
    private final int SWITCH_OFF_BUTTON = R.drawable.switch_off_button;

    // Constants that hold on/off status int
    private final int SWITCH_STATUS_OFF = 0;
    private final int SWITCH_STATUS_ON = 1;

    // Status within the app, initiated to 0 (OFF)
    private int mStatus = 0;

    // Switch Button ImageView
    private ImageButton switchButton;

    Vibrator vibe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_switch, container, false);

        switchButton = (ImageButton) rootView.findViewById(R.id.switch_button);
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        //status = new statusLoader().execute();

        //setButtonByStatus();

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
                vibe.vibrate(20);
                setButtonByStatus();
            }
        });

        return rootView;
    }
    
    private void setStatus(int status) {
        mStatus = status;
    }

    private void changeStatus() {
        mStatus = 1 - mStatus;
        // TODO: update server with new status.
    }

    private void setButtonByStatus() {
        if (mStatus == SWITCH_STATUS_ON)
            switchButton.setImageResource(SWITCH_ON_BUTTON);
        else
            switchButton.setImageResource(SWITCH_OFF_BUTTON);

        Toast.makeText(getActivity(), String.format(getResources().getString(R.string.status_change_msg), statusToText()), Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns status as text ( 0 = OFF , 1 = ON )
     */
    private String statusToText() {
        String statusText;

        if (mStatus == SWITCH_STATUS_OFF)
            statusText = getString(R.string.status_off);
        else
            statusText = getString(R.string.status_on);

        return statusText;

    }

    @Override
    public Loader<Integer> onCreateLoader(int id, Bundle args) {
        return new StatusLoader(getActivity(), createStatusURL());
    }

    @Override
    public void onLoadFinished(Loader<Integer> loader, Integer status) {
        setStatus(status);
        setButtonByStatus();
    }

    @Override
    public void onLoaderReset(Loader<Integer> loader) {

    }

    /* TRY OUT */
    private URL createStatusURL() {
        URL queryUrl = null;
        final String BASE_SERVER_URL = "https://ad,mgadf,g";
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

        if (queryUrl == null) return null;
        return queryUrl;
    }
}
