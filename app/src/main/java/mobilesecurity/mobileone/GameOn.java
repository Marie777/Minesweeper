package mobilesecurity.mobileone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GameOn extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final int N = 10;
    private static final String TAG = "btn";

    private TimerThread timerThread;

    private FieldAdapter fieldAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_on);

        GridView gridView = (GridView)findViewById(R.id.grid);
        gridView.setBackgroundColor(1);

        gridView.setNumColumns(N);

        boolean[][] mineMap = new boolean[N][N];

        for(int i = 0; i < 0; i++) {
            int x = (int)Math.floor((Math.random() * N));
            int y = (int)Math.floor((Math.random() * N));
            mineMap[x][y] = true;
        }

        fieldAdapter = new FieldAdapter(this, N, this, this, mineMap);

        gridView.setAdapter(fieldAdapter);
        TextView timer = (TextView) findViewById(R.id.timer);

        timerThread = new TimerThread(this, timer, 0);
        timerThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timerThread.setFinished(true);
    }

    @Override
    public void onClick(View v) {
        if(v instanceof MinesweeperButton) {
            MinesweeperButton minesweeperButton = (MinesweeperButton) v;

            reveal(minesweeperButton);

            Toast.makeText(
                    this,
                    String.format(
                            Locale.ENGLISH,
                            "(%d, %d) - %s",
                            minesweeperButton.getMyX(),
                            minesweeperButton.getMyY(),
                            minesweeperButton.isMine() ? "Mine" : "Not mine"),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private List<MinesweeperButton> getAdj(MinesweeperButton minesweeperButton){
        int x = minesweeperButton.getMyX();
        int y = minesweeperButton.getMyY();

        List<MinesweeperButton> adjButtons = new ArrayList<>();
        int minX = x > 0 ? x - 1 : x;
        int maxX = x < N - 1 ? x + 1 : x;
        int minY = y > 0 ? y - 1 : y;
        int maxY = y < N - 1 ? y + 1 : y;

        Log.d(TAG, "minX " + minX + " maxX " + maxX);
        Log.d(TAG, "minY " + minY + " maxY " + maxY);

        for(int i = minX; i <= maxX; i++) {
            for(int j = minY; j <= maxY; j++) {
                adjButtons.add((MinesweeperButton) fieldAdapter.getItem(i + j * N));
            }
        }

        Log.d(TAG, "List size " + adjButtons.size());

        MinesweeperButton t = (MinesweeperButton)fieldAdapter.getItem(0);

        t.setText("B");

        Log.d(TAG, t.getText().toString());

        return adjButtons;
    }

    private int getMineCount(MinesweeperButton minesweeperButton) {
        int number = 0;

        List<MinesweeperButton> adjButtons = getAdj(minesweeperButton);

        for(MinesweeperButton adjBtn : adjButtons) {
            if(adjBtn != null) {
                if(adjBtn.isMine()) {
                    number++;
                }
            }
        }

        return number;
    }

    private void reveal(MinesweeperButton minesweeperButton) {

        if(minesweeperButton.isMine()) {
            Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show();
            return;
        }
        bfsReveal(minesweeperButton);
    }

    private void bfsReveal(MinesweeperButton minesweeperButton){

        if(!minesweeperButton.isRevealed()){
            int countM = getMineCount(minesweeperButton);
            minesweeperButton.setText(String.valueOf(countM));
            minesweeperButton.setRevealed(true);
            if(countM==0){
                List<MinesweeperButton> adjButtons = getAdj(minesweeperButton);
                for(int i = 0; i < adjButtons.size(); i++) {
                    adjButtons.get(i).setText("A");
                }
//                for (MinesweeperButton btn: adjButtons) {
//
//                    //bfsReveal(btn);
//                }
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(v instanceof MinesweeperButton) {
            MinesweeperButton minesweeperButton = (MinesweeperButton) v;
            Toast.makeText(
                    this,
                    String.format(
                            Locale.ENGLISH,
                            "Long: (%d, %d) - %s",
                            minesweeperButton.getMyX(),
                            minesweeperButton.getMyY(),
                            minesweeperButton.isMine() ? "Mine" : "Not mine"),
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}

      /*

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

                        //if(btns2[ii][jj].getText().equals("*")) {
                         //   Log.i(TAG, "Game over");
                        //}
                    }
                });
                myLayout.addView(btns[i][j]);
            }
        }


        setContentView(myLayout);
*/
