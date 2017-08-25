package mobilesecurity.mobileone;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class GameOver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        TextView tvResult = (TextView) findViewById(R.id.tvResult);
        TextView tvTime = (TextView) findViewById(R.id.tvTime);
        TextView tvLevel = (TextView) findViewById(R.id.tvLevel);

        Resources res = getResources();

        int level = getIntent().getIntExtra(res.getString(R.string.extra_key_level), 0);
        boolean isWon = getIntent().getBooleanExtra(res.getString(R.string.extra_key_is_won), false);
        int time = getIntent().getIntExtra(res.getString(R.string.extra_key_time), 0);

        String[] difficulties = res.getStringArray(R.array.difficulties);

        String levelName = difficulties[level % difficulties.length];

        String resultText = isWon ? res.getString(R.string.you_win_text) : res.getString(R.string.you_loose_text);
        String levelText = String.format(Locale.ENGLISH, "Level: %s", levelName);
        String timeText = String.format(Locale.ENGLISH, "Time: %d sec", time);

        tvResult.setText(resultText);
        tvTime.setText(timeText);
        tvLevel.setText(levelText);
    }
}
