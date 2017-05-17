package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;
import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;

public class SwitchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Integer>, ChangeStatusTaskCallback {

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
    private ImageView mSwitchButton;
    private ProgressBar mProgressBar;

    LoaderManager loaderManager;
    Vibrator vibe;

    // Boiler ID sent from the MainActivity;
    private int boilerID;

    private String authKey;

    SweetAlertDialog loadingDialog;
    boolean mResponse = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_switch, container, false);

        boilerID = getActivity().getIntent().getIntExtra("boilerID", 0);

        // TODO: see placement of this db query

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
        authKey = cursor.getString(cursor.getColumnIndex(BoilerEntry.COLUMN_BOILER_KEY));

        db.close();

        mSwitchButton = (ImageView) rootView.findViewById(R.id.switch_button);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.switch_progressbar);
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        //status = new statusLoader().execute();

        //setButtonByStatus();

        mSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
                vibe.vibrate(20);
                setButtonByStatus();
            }
        });

        loaderManager = getLoaderManager();

        // Check if device can access internet. If not - display an error and go back to MainActivity.
        if (NetworkUtils.isOnline()) {
            loaderManager.initLoader(1, null, this);
        } else {
            SweetAlertDialog noInternetDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.boiler_error_no_internet_connection))
                    .setContentText(getString(R.string.boiler_error_check_your_connection))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            //getActivity().finish();
                        }
                    });
            noInternetDialog.show();
            noInternetDialog.setOnDismissListener(new SweetAlertDialog.OnDismissListener(){
                @Override
                public void onDismiss(DialogInterface dialog) {
                    getActivity().finish();
                }
            });
        }

        return rootView;
    }
    
    private void setStatus(int status) {
        mStatus = status;
    }

    private void changeStatus() {
        loadingDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Just a sec..");
        loadingDialog.show();

        ChangeStatusTask changeStatusTask = new ChangeStatusTask(getActivity());
        String[] params = {"status="+String.valueOf(1- mStatus),"key=" + authKey};

        changeStatusTask.delegate = this;
        changeStatusTask.execute(params);


        /*if (changeStatusTask.getStatus() == AsyncTask.Status.FINISHED) {
            loadingDialog.dismiss();
            if (mResponse)
                mStatus = 1 - mStatus;
        }*/

        // TODO: check if GOOD doesn't fall into bad response.
        // TODO: Check if Bad response well;
    }

    private void setButtonByStatus() {
        if (mStatus == SWITCH_STATUS_ON)
            mSwitchButton.setImageResource(SWITCH_ON_BUTTON);
        else
            mSwitchButton.setImageResource(SWITCH_OFF_BUTTON);

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
        return new StatusLoader(getActivity(), ServerQueries.createURL(ServerQueries.PATH_GET_STATUS), authKey);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Integer> loader, Integer status) {
        Log.i(LOG_TAG, "onLoadFinished");
        Log.i(LOG_TAG, "status: " + status);
        mProgressBar.setVisibility(ProgressBar.GONE);
        if (status == null) {
            SweetAlertDialog badResponseDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.boiler_error_cant_get_data_from_server))
                    .setContentText(getString(R.string.boiler_error_please_try_again_later));
            badResponseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    getActivity().finish();
                }
            });
            badResponseDialog.show();
        } else if (status == -1) {
            SweetAlertDialog remoilerOfflineDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.boiler_error_remoiler_is_offline))
                    .setContentText(getString(R.string.boiler_error_make_sure_remoiler_is_connected));
            remoilerOfflineDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    getActivity().finish();
                }
            });
            remoilerOfflineDialog.show();
        } else {
            setStatus(status);
            setButtonByStatus();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Integer> loader) {

    }

    @Override
    public void postResult(Boolean response) {
        mResponse = response;
        loadingDialog.dismiss();
        if (mResponse)
            mStatus = 1 - mStatus;
    }
}
