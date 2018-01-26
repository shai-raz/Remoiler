package hu.pe.remoiler.remoiler;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import hu.pe.remoiler.remoiler.barcode.BarcodeCaptureActivity;
import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;

public class BoilerEditor extends AppCompatActivity {

    private static final String LOG_TAG = BoilerEditor.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    int mBoilerID;
    String mBoilerName = "";
    String mBoilerKey = "";

    EditText mNameEdit;
    EditText mKeyEdit;
    Button mQrButton;

    boolean mEditMode = false;
            
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boiler_editor);

        mNameEdit = (EditText) findViewById(R.id.boiler_editor_name_edit);
        mKeyEdit = (EditText) findViewById(R.id.boiler_editor_key_edit);
        mQrButton = (Button) findViewById(R.id.boiler_editor_qr_button);

        mQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "mQrButton.onClick()");
                Intent barcodeScannerIntent = new Intent(BoilerEditor.this, BarcodeCaptureActivity.class);
                startActivityForResult(barcodeScannerIntent, BARCODE_READER_REQUEST_CODE);
            }
        });

        Intent intent = getIntent();

        if (getIntent().getExtras() != null) {
            mEditMode = true;
            mBoilerID = intent.getIntExtra("boilerID", 0);
            mBoilerName = intent.getStringExtra("boilerName");
            mBoilerKey = intent.getStringExtra("boilerKey");

            mNameEdit.setText(mBoilerName, TextView.BufferType.EDITABLE);
            mKeyEdit.setText(mBoilerKey, TextView.BufferType.EDITABLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_boiler_editor, menu);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBoiler();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    getting the qr result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG_TAG, "onActivityResult()");
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    Log.i(LOG_TAG, "BARCODE CAPTURED: " + barcode.displayValue);
                    mKeyEdit.setText(barcode.displayValue);
                } else {} // Handle no_barcode_captured
            } else Log.e(LOG_TAG, String.format(getString(R.string.qr_barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveBoiler() {
        String nameString = mNameEdit.getText().toString().trim();
        String keyString = mKeyEdit.getText().toString().trim();

        if (!TextUtils.isEmpty(nameString) && !TextUtils.isEmpty(keyString)) {
            // TODO: check if key exists in server's database
            Toast.makeText(this, keyString, Toast.LENGTH_SHORT).show();

            /*RemoilerDbHelper mDbHelper = new RemoilerDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();*/

            ContentValues values = new ContentValues();
            values.put(BoilerEntry.COLUMN_BOILER_NAME, nameString);
            values.put(BoilerEntry.COLUMN_BOILER_KEY, keyString);

            /*long newRowId;
            String whereClause = BoilerEntry._ID + "=?";
            String[] whereArgs = {String.valueOf(mBoilerID)};*/

            if (!mEditMode) {
                // Make a new boiler
                Uri newUri = getContentResolver().insert(BoilerEntry.CONTENT_URI, values);

                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.boiler_editor_insert_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.boiler_editor_insert_successful),
                            Toast.LENGTH_SHORT).show();
                }
                // newRowId = db.insert(BoilerEntry.TABLE_NAME, null, values);
            } else {
                // Update existing boiler
                int rowsAffected = getContentResolver().update(BoilerEntry.CONTENT_URI, values, null, null);
                //newRowId = db.update(BoilerEntry.TABLE_NAME, values, whereClause, whereArgs);
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.boiler_editor_insert_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.boiler_editor_insert_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }

            /*if (newRowId != -1)
                Toast.makeText(this, "Added new Boiler! ID: " + newRowId, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Problem adding new boiler.", Toast.LENGTH_SHORT).show();*/
            //db.close();
        } else {
            Toast.makeText(this, R.string.boiler_editor_error_fill_all_fields, Toast.LENGTH_SHORT).show();
        }
    }
}
