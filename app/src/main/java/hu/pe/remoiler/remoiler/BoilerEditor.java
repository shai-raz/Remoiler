package hu.pe.remoiler.remoiler;

import android.app.Activity;
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

    String boilerName = "";
    EditText nameEdit;
    EditText userEdit;
    EditText passEdit;
            
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boiler_editor);

        nameEdit = (EditText) findViewById(R.id.boiler_name_edit);
        userEdit = (EditText) findViewById(R.id.boiler_username_edit);
        passEdit = (EditText) findViewById(R.id.boiler_password_edit);

        if (getIntent().getExtras() != null) {
            boilerName = getIntent().getStringExtra("name");
            
            nameEdit.setText(boilerName, TextView.BufferType.EDITABLE);
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
                saveKey(String.valueOf(userEdit), String.valueOf(passEdit));
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveKey(String user, String pass) {
        if (user != null && !user.equals("") && pass != null && !pass.equals("")) {
            String key = ServerQueries.getAuthKeyByUserPass(user, pass);
            Toast.makeText(this, key, Toast.LENGTH_SHORT).show();

            RemoilerDbHelper mDbHelper = new RemoilerDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(BoilerEntry.COLUMN_BOILER_NAME, nameEdit.getText().toString());
            values.put(BoilerEntry.COLUMN_BOILER_KEY, key);

            long newRowId = db.insert(BoilerEntry.TABLE_NAME, null, values);

            if (newRowId != -1)
                Toast.makeText(this, "Added new Boiler! ID: " + newRowId, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Problem adding new boiler.", Toast.LENGTH_SHORT).show();

            db.close();
        } else {
            Toast.makeText(this, R.string.boiler_editor_error_fill_all_fields, Toast.LENGTH_SHORT).show();
        }
    }
}
