package hu.pe.remoiler.remoiler;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;
import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;

public class BoilerEditor extends AppCompatActivity {

    int mBoilerID;
    String mBoilerName = "";
    String mBoilerKey = "";
    EditText mNameEdit;
    EditText mKeyEdit;
    boolean mEditMode = false;
            
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boiler_editor);

        mNameEdit = (EditText) findViewById(R.id.boiler_editor_name_edit);
        mKeyEdit = (EditText) findViewById(R.id.boiler_editor_key_edit);

        if (getIntent().getExtras() != null) {
            mEditMode = true;
            mBoilerID = getIntent().getIntExtra("boilerID", 0);
            mBoilerName = getIntent().getStringExtra("boilerName");
            mBoilerKey = getIntent().getStringExtra("boilerKey");

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
                saveKey(mNameEdit.getText().toString(), mKeyEdit.getText().toString());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveKey(String name, String key) {
        if (name != null && !name.equals("") && key != null && !key.equals("")) {
            // TODO: check if key exists in server's database
            Toast.makeText(this, key, Toast.LENGTH_SHORT).show();

            RemoilerDbHelper mDbHelper = new RemoilerDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(BoilerEntry.COLUMN_BOILER_NAME, name);
            values.put(BoilerEntry.COLUMN_BOILER_KEY, key);

            long newRowId;
            String whereClause = BoilerEntry._ID + "=?";
            String[] whereArgs = {String.valueOf(mBoilerID)};

            if (!mEditMode)
                // Make a new boiler
                newRowId = db.insert(BoilerEntry.TABLE_NAME, null, values);
            else
                // Update existing boiler
                newRowId = db.update(BoilerEntry.TABLE_NAME, values, whereClause, whereArgs);

            if (newRowId != -1)
                Toast.makeText(this, "Added new Boiler! ID: " + newRowId, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Problem adding new boiler.", Toast.LENGTH_SHORT).show();

            this.finish();

            db.close();
        } else {
            Toast.makeText(this, R.string.boiler_editor_error_fill_all_fields, Toast.LENGTH_SHORT).show();
        }
    }
}
