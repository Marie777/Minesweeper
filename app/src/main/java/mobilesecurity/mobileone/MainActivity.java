package mobilesecurity.mobileone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button[] levelBtns;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        levelBtns = new Button[3];

        Button btnEasy = (Button) findViewById(R.id.btnEasy);
        Button btnMedium = (Button) findViewById(R.id.btnMedium);
        Button btnHard = (Button) findViewById(R.id.btnHard);

        levelBtns[0] = btnEasy;
        levelBtns[1] = btnMedium;
        levelBtns[2] = btnHard;

        btnEasy.setOnClickListener(this);
        btnMedium.setOnClickListener(this);
        btnHard.setOnClickListener(this);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        int lastLevel = prefs.getInt("level", 0);

        Button lastLevelBtn = levelBtns[lastLevel % levelBtns.length];

        lastLevelBtn.setText("> " + lastLevelBtn.getText());
        lastLevelBtn.requestFocus();
    }


    public void startGame(int level) {
        Intent intent=new Intent(this,GameOn.class);
        intent.putExtra("level",level);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|
        Intent.FLAG_ACTIVITY_FORWARD_RESULT);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("level", level);
        editor.apply();

        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(levelBtns[0] == v) {
            startGame(0);
        } else if(levelBtns[1] == v) {
            startGame(1);
        } else if(levelBtns[2] == v) {
            startGame(2);
        }
    }
}

