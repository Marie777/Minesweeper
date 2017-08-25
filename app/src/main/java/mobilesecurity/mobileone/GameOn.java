package mobilesecurity.mobileone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GameOn extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "btn";

    private int n = 10;
    private int numberOfMines = 0;
    private int level;

    private TimerThread timerThread;
    private FieldAdapter fieldAdapter;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_on);

        //gridView = (GridView)findViewById(R.id.grid);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.ll);
        gridView = new GridView(this);
        gridView.setGravity(Gravity.CENTER);



        level = this.getIntent().getIntExtra("level", 0);

        switch (level) {
            case 0:
                n = 10;
                numberOfMines = 5;
                break;
            case 1:
                n = 10;
                numberOfMines = 10;
                break;
            case 2:
                n = 5;
                numberOfMines = 10;
                gridView.setLayoutParams(new GridView.LayoutParams(n*100,n*100));
                break;
        }

        mainLayout.addView(gridView,0);


        gridView.setNumColumns(n);

        boolean[][] mineMap = new boolean[n][n];

        for(int i = 0; i < numberOfMines; i++) {
            int x = (int)Math.floor((Math.random() * n));
            int y = (int)Math.floor((Math.random() * n));
            mineMap[x][y] = true;
        }

        fieldAdapter = new FieldAdapter(this, n, this, this, mineMap);

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

            if(!minesweeperButton.isFlagged()) {
                reveal(minesweeperButton);
            }
        }
    }

    private List<MinesweeperButton> getAdj(MinesweeperButton minesweeperButton){
        int x = minesweeperButton.getMyX();
        int y = minesweeperButton.getMyY();

        List<MinesweeperButton> adjButtons = new ArrayList<>();
        int minX = x > 0 ? x - 1 : x;
        int maxX = x < n - 1 ? x + 1 : x;
        int minY = y > 0 ? y - 1 : y;
        int maxY = y < n - 1 ? y + 1 : y;

        Log.d(TAG, "minX " + minX + " maxX " + maxX);
        Log.d(TAG, "minY " + minY + " maxY " + maxY);

        for(int i = minX; i <= maxX; i++) {
            for(int j = minY; j <= maxY; j++) {
                adjButtons.add((MinesweeperButton) fieldAdapter.getItem(i + j * n));
            }
        }

        Log.d(TAG, "List size " + adjButtons.size());

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
            endGameIntent(false, timerThread.getCurrentTime());
            return;
        }

        if(!minesweeperButton.isFlagged()) {
            bfsReveal(minesweeperButton);
            gridView.refreshDrawableState();

            if(isWin()) {
                Toast.makeText(this, "You won!", Toast.LENGTH_SHORT).show();
                timerThread.setFinished(true);
                endGameIntent(true, timerThread.getCurrentTime());
            }
        }
    }

    private void endGameIntent(boolean isWin, int time){
        Intent intent = new Intent(this,GameOver.class);
        intent.putExtra("isWin", isWin);
        intent.putExtra("level", level);
        intent.putExtra("time", time);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY| Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
    }



    private void bfsReveal(MinesweeperButton minesweeperButton){

        if(!minesweeperButton.isRevealed() && !minesweeperButton.isFlagged()){
            int countM = getMineCount(minesweeperButton);
            minesweeperButton.setText(String.valueOf(countM));
            minesweeperButton.setRevealed(true);
            if(countM==0){
                List<MinesweeperButton> adjButtons = getAdj(minesweeperButton);
                for (MinesweeperButton btn: adjButtons) {
                    bfsReveal(btn);
                }
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(v instanceof MinesweeperButton) {
            MinesweeperButton minesweeperButton = (MinesweeperButton) v;

            if(!minesweeperButton.isRevealed()) {
                minesweeperButton.setFlagged(!minesweeperButton.isFlagged());
            }

            return true;
        }
        return false;
    }

    private boolean isWin() {
        int revealedCount = 0;
        for(int i = 0; i < fieldAdapter.getCount(); i++) {
            MinesweeperButton tile = (MinesweeperButton) fieldAdapter.getItem(i);
            if(tile.isRevealed()) {
                revealedCount++;
            }
        }

        return n * n - numberOfMines == revealedCount;
    }
}
