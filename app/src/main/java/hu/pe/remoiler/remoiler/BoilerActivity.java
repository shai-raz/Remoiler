package hu.pe.remoiler.remoiler;


import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hu.pe.remoiler.remoiler.data.ScheduleContract.ScheduleEntry;
import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;

public class BoilerActivity extends AppCompatActivity {

    private final static String LOG_TAG = BoilerActivity.class.getSimpleName();

    //String boilerName = "";
    int boilerID;

    //Dialog wifiDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boiler);

        if (getIntent().getExtras() != null) {
            boilerID = getIntent().getIntExtra("id", 0);
        }



        Toast.makeText(this, "boiler id is" + boilerID, Toast.LENGTH_SHORT).show();

        /* TODO: Check if this boiler is already configured with Wi-Fi connection.
                 Check if connected to wifi network, and get its SSID. ( NetworkUtils.getWifiSSID() )
                 Send to new activity that will take SSID & Pass from user.
                 Send wifi details through SmartConfig / Bluetooth / ? */

        /*String wifiSSID = NetworkUtils.getWifiSSID(this);
        if (wifiSSID != null) {}

        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("First usage!")
                .setContentText("First time using this boiler, is your remoiler set up with Wi-Fi?")
                .setConfirmText("No, set it up now.")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        // Intent to wifi setup.
                    }
                })
                .setCancelText("Yes.")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();*/

        /*wifiDialog = new Dialog(this);
        wifiDialog.setContentView(R.layout.dialog_wifi);
        wifiDialog.setTitle("Wifi information");
        Button wifiSaveButton = (Button) wifiDialog.findViewById(R.id.wifi_save_button);
        wifiSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiDialog.dismiss();
            }
        });
        wifiDialog.show();*/

        // Create the ViewPager object
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create the TabAdapter (Custom FragmentPagerAdapter)
        TabAdapter tabAdapter = new TabAdapter(this, getSupportFragmentManager());

        // Set TabAdapter with the ViewPager
        viewPager.setAdapter(tabAdapter);

        // Create the TabLayout Object
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Set Tabs With Pages
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Temporary insert test
    private void insertDummyData() {
        RemoilerDbHelper mDbHelper = new RemoilerDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScheduleEntry.COLUMN_SCHEDULE_START_TIME, 420);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_END_TIME, 650);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_RETURNS, "[0,0,0,0,1,0,1]");
        values.put(ScheduleEntry.COLUMN_SCHEDULE_ACTIVE, 1);

        long newRowId = db.insert(ScheduleEntry.TABLE_NAME, null, values);

        if (newRowId != -1)
            Toast.makeText(this, "Added Dummy Data! ID: " + newRowId, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Problem adding dummy data", Toast.LENGTH_SHORT).show();

        db.close();
    }
}
