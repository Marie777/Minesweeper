package mobilesecurity.mobileone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

    public static final int MY_GPS_PERMISSION = 0;

    private int n = 10;
    private int numberOfMines = 0;
    private int level;

    private TimerThread timerThread;
    private FieldAdapter fieldAdapter;
    private GridView gridView;

    public MyLocationService myLocationService;

    private boolean isFinished = false;

    public float[] initialTilt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_on);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.ll);
        gridView = new GridView(this);
        gridView.setGravity(Gravity.CENTER);
        gridView.setBackgroundColor(Color.WHITE);
        gridView.setPadding(0,0,0,0);

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
            int x;
            int y;

            do {
                x = (int)Math.floor((Math.random() * n));
                y = (int)Math.floor((Math.random() * n));
            } while(mineMap[x][y]);

            mineMap[x][y] = true;
        }

        fieldAdapter = new FieldAdapter(this, n, this, this, mineMap);

        gridView.setAdapter(fieldAdapter);
        TextView timer = (TextView) findViewById(R.id.timer);

        timerThread = new TimerThread(this, timer, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, TiltService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onCreate: after bind service");

        Intent locationIntent = new Intent(this, MyLocationService.class);
        bindService(locationIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
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

        for(int i = minX; i <= maxX; i++) {
            for(int j = minY; j <= maxY; j++) {
                adjButtons.add((MinesweeperButton) fieldAdapter.getItem(i + j * n));
            }
        }

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

        if(!timerThread.isAlive()) {
            timerThread.start();
        }

        if(isFinished) {
            return;
        }

        if(minesweeperButton.isMine()) {
            Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show();
            minesweeperButton.setText("*");
            minesweeperButton.setTextColor(Color.RED);
            waitAndEndGame(false);

            return;
        }

        if(!minesweeperButton.isFlagged()) {
            bfsReveal(minesweeperButton);
            gridView.refreshDrawableState();

            if(isWin()) {
                Toast.makeText(this, "You won!", Toast.LENGTH_SHORT).show();
                waitAndEndGame(true);
            }
        }
    }

    public void unReveal(){

    }

    private void waitAndEndGame(final boolean isWin) {
        timerThread.setFinished(true);
        isFinished = true;

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
            runOnUiThread(() -> endGameIntent(isWin, timerThread.getCurrentTime()));
        }).start();
    }

    private void endGameIntent(boolean isWin, int time){
        Resources res = getResources();
        Intent intent = new Intent(this, GameOver.class);
        intent.putExtra(res.getString(R.string.extra_key_is_won), isWin);
        intent.putExtra(res.getString(R.string.extra_key_level), level);
        intent.putExtra(res.getString(R.string.extra_key_time), time);

        Location location = null;

        if(myLocationService != null) {
            location = myLocationService.getCurrentLocation();
        }

        if (location != null) {
            intent.putExtra(res.getString(R.string.extra_key_lng), location.getLongitude());
            intent.putExtra(res.getString(R.string.extra_key_lat), location.getLatitude());
        } else {
            intent.putExtra(res.getString(R.string.extra_key_lng), 0.0);
            intent.putExtra(res.getString(R.string.extra_key_lat), 0.0);
        }


        startActivity(intent);
    }

    private void bfsReveal(MinesweeperButton minesweeperButton){

        if(!minesweeperButton.isRevealed() && !minesweeperButton.isFlagged()){
            int countM = getMineCount(minesweeperButton);
            minesweeperButton.setRevealed(countM);
            if(countM==0){
                List<MinesweeperButton> adjButtons = getAdj(minesweeperButton);

                for (MinesweeperButton btn : adjButtons) {
                    this.bfsReveal(btn);
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

        Log.d("ISWIN", "Revealed count: " + revealedCount);

        return n * n - numberOfMines == revealedCount;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(service instanceof TiltService.TiltBinder) {
                TiltService.TiltBinder binder = (TiltService.TiltBinder) service;
                binder.getService().setResetListener(GameOn.this);
            } else if (service instanceof MyLocationService.MyLocationBinder){
                MyLocationService.MyLocationBinder binder = (MyLocationService.MyLocationBinder) service;
                myLocationService = binder.getService();

                Log.d(TAG, "onServiceConnected: MyLocationService");

                requestGPSPermission();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void requestGPSPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION },
                MY_GPS_PERMISSION);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_GPS_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(myLocationService != null) {
                        myLocationService.startUpdating();
                    }
                }
        }
    }
}
