package mobilesecurity.mobileone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Random;

public class GameOn extends AppCompatActivity {

    private static final int n = 10;
    private static final int sizeP = 110;
    private static final int MaxMines = 10;
    private static final String TAG = "btn";
    public int i,j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_on);

        RelativeLayout myLayout = new RelativeLayout(this);
        LinearLayout.LayoutParams param =
                new LinearLayout.LayoutParams(sizeP, sizeP);

        final Button[][] btns = new Button[n][n];
        final Button[][] btns2 = new Button[n][n];
        Random rand = new Random();
        int countMines = 0;

        for(j = 0; j < n ; j++) {
            for (i = 0; i < n; i++) {
                btns2[i][j] = new Button(this);
                btns2[i][j].setId(i*10+j);
                btns2[i][j].setLayoutParams(param);
                btns2[i][j].setX(i + j*sizeP);
                btns2[i][j].setY(sizeP * i);

                if(rand.nextInt(n/5 + 2)==0 && countMines < MaxMines) {
                    btns2[i][j].setText("*");
                    countMines++;
                }else{
                    btns2[i][j].setText("F");
                }

                myLayout.addView(btns2[i][j]);
            }
        }


        for(j = 0; j < n ; j++) {
            for (i = 0; i < n; i++) {
                btns[i][j] = new Button(this);
                btns[i][j].setId(i*10+j);
                btns[i][j].setLayoutParams(param);
                btns[i][j].setX(i + j*sizeP);
                btns[i][j].setY(sizeP * i);
                btns[i][j].setText(i + "" + j);
                btns[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.INVISIBLE);


                        int ii = v.getId()/10;
                        int jj = v.getId()%10;

                        if(btns2[ii][jj].getText().equals("*")) {
                            Log.i(TAG, "Game over");
                        }
                    }
                });
                myLayout.addView(btns[i][j]);
            }
        }


        setContentView(myLayout);

    }
}
