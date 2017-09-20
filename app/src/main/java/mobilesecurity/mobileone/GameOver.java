package mobilesecurity.mobileone;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameOver extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OVER", "Game over start");
        setContentView(R.layout.activity_game_over);

        TextView tvResult = (TextView) findViewById(R.id.tvResult);
        TextView tvTime = (TextView) findViewById(R.id.tvTime);
        TextView tvLevel = (TextView) findViewById(R.id.tvLevel);

        Resources res = getResources();

        int level = getIntent().getIntExtra(res.getString(R.string.extra_key_level), 0);
        boolean isWon = getIntent().getBooleanExtra(res.getString(R.string.extra_key_is_won), false);
        int time = getIntent().getIntExtra(res.getString(R.string.extra_key_time), 0);
        double lng = getIntent().getDoubleExtra(res.getString(R.string.extra_key_lng), 0.0);
        double lat = getIntent().getDoubleExtra(res.getString(R.string.extra_key_lat), 0.0);

        String[] difficulties = res.getStringArray(R.array.difficulties);

        String levelName = difficulties[level % difficulties.length];

        String resultText = isWon ? res.getString(R.string.you_win_text) : res.getString(R.string.you_loose_text);
        String levelText = String.format(Locale.ENGLISH, "Level: %s", levelName);
        String timeText = String.format(Locale.ENGLISH, "Time: %d sec", time);

        tvResult.setText(resultText);
        tvTime.setText(timeText);
        tvLevel.setText(levelText);

        if(isWon) {
            RecordDbHelper mDbHelper = new RecordDbHelper(getApplicationContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(RecordsContract.RecordEntry.COLUMN_NAME_NAME, "Test");
            values.put(RecordsContract.RecordEntry.COLUMN_NAME_DATE, new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            values.put(RecordsContract.RecordEntry.COLUMN_NAME_TIME, time);
            values.put(RecordsContract.RecordEntry.COLUMN_NAME_GPS_ALT, 0.0);
            values.put(RecordsContract.RecordEntry.COLUMN_NAME_GPS_LONG, lng);
            values.put(RecordsContract.RecordEntry.COLUMN_NAME_GPS_LAT, lat);

            db.insert(RecordsContract.RecordEntry.TABLE_NAME, null, values);

            mDbHelper.close();
        }
    }
}
