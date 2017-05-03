package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;
import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;

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
    private ImageView switchButton;

    LoaderManager loaderManager;
    ConnectivityManager cm;
    Vibrator vibe;

    // Boiler ID sent from the MainActivity;
    private int boilerID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_switch, container, false);

        boilerID = getActivity().getIntent().getIntExtra("boilerID", 0);

        switchButton = (ImageView) rootView.findViewById(R.id.switch_button);
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

        cm =  (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        loaderManager = getLoaderManager();
        if (cm.getActiveNetworkInfo() != null)
                loaderManager.initLoader(1, null, this);

        return rootView;
    }
    
    private void setStatus(int status) {
        mStatus = status;
    }

    private void changeStatus() {
        mStatus = 1 - mStatus;

        ChangeStatusTask changeStatusTask = new ChangeStatusTask();
        changeStatusTask.execute("status="+String.valueOf(mStatus));
        // TODO: Handle Bad response;
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
    public android.support.v4.content.Loader<Integer> onCreateLoader(int id, Bundle args) {
        //TODO: see placement of this database query

        RemoilerDbHelper dbHelper = new RemoilerDbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = { BoilerEntry.COLUMN_BOILER_KEY };
        String[] selectionArgs = { String.valueOf(boilerID) };

        Cursor cursor = db.query(
                BoilerEntry.TABLE_NAME,
                projection,
                BoilerEntry._ID + "=?",
                selectionArgs,
                null,
                null,
                null
        );
        Log.i(LOG_TAG, "CURSOR QUERY: " + DatabaseUtils.dumpCursorToString(cursor));

        cursor.moveToFirst();
        String authKey = cursor.getString(cursor.getColumnIndex(BoilerEntry.COLUMN_BOILER_KEY));

        db.close();
        return new StatusLoader(getActivity(), ServerQueries.createURL(ServerQueries.PATH_GET_STATUS));
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Integer> loader, Integer status) {
        Log.i(LOG_TAG, "onLoadFinished");
        Log.i(LOG_TAG, "status: " + status);
        setStatus(status);
        setButtonByStatus();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Integer> loader) {

    }

}
