package mobilesecurity.mobileone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner) findViewById(R.id.diffSpinner);

        Button playButton = (Button) findViewById(R.id.playBtn);
        playButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        int lastLevel = prefs.getInt("level", 0);

        spinner.setSelection(lastLevel);

        RecordDbHelper mDbHelper = new RecordDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                RecordsContract.RecordEntry.COLUMN_NAME_NAME,
                RecordsContract.RecordEntry.COLUMN_NAME_DATE,
                RecordsContract.RecordEntry.COLUMN_NAME_TIME,
        };

        String sortOrder = RecordsContract.RecordEntry.COLUMN_NAME_TIME + " DESC";

        Cursor cur = db.query(RecordsContract.RecordEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);

        //List<String> items = new ArrayList<>();

        for(int i = 0; i < 10 && cur.moveToNext(); i++) {
            //items.add(cur.getString(0) + " " + cur.getString(1) + " " + cur.getInt(2));
            Log.d("TEST",
                    cur.getString(cur.getColumnIndexOrThrow(RecordsContract.RecordEntry.COLUMN_NAME_NAME)) + " " +
                    cur.getString(cur.getColumnIndexOrThrow(RecordsContract.RecordEntry.COLUMN_NAME_DATE)) + " " +
                    cur.getInt(cur.getColumnIndexOrThrow(RecordsContract.RecordEntry.COLUMN_NAME_TIME)));
        }
        cur.close();
        mDbHelper.close();
    }

    public void startGame(int level) {
        Intent intent=new Intent(this,GameOn.class);
        intent.putExtra("level",level);

        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_FORWARD_RESULT);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("level", level);
        editor.apply();

        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int level = spinner.getSelectedItemPosition();
        startGame(level);
    }
}

