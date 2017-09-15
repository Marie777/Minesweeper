package mobilesecurity.mobileone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner spinner;

    Button playButton;
    Button recordsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner) findViewById(R.id.diffSpinner);

        playButton = (Button) findViewById(R.id.playBtn);
        recordsButton = (Button) findViewById(R.id.recordsBtn);

        playButton.setOnClickListener(this);
        recordsButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        int lastLevel = prefs.getInt("level", 0);

        spinner.setSelection(lastLevel);
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
        if(v == playButton) {
            int level = spinner.getSelectedItemPosition();
            startGame(level);
        } else if(v == recordsButton) {
            Intent intent = new Intent(this, Records.class);
            startActivity(intent);
        }
    }
}

