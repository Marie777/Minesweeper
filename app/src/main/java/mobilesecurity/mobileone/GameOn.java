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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class GameOn extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, Observer {
    private static final String TAG = "GameOn";

    public static final int MY_GPS_PERMISSION = 0;
    private int level;

    private TimerThread timerThread;
    private MyLocationService myLocationService;
    public float[] initialTilt;

    private GameModel mModel;
    private GameController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_on);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.ll);
        TextView timer = (TextView) findViewById(R.id.timer);

        int n;
        int numberOfMines;

        GridView gridView = new GridView(this);
        gridView.setGravity(Gravity.CENTER);
        gridView.setBackgroundColor(Color.WHITE);
        gridView.setPadding(0, 0, 0, 0);
        mainLayout.addView(gridView, 0);

        level = this.getIntent().getIntExtra("level", 0);

        switch (level) {
            default:
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
                gridView.setLayoutParams(new GridView.LayoutParams(n * 100, n * 100));
                break;
        }
        gridView.setNumColumns(n);

        mModel = new GameModel(n);
        mModel.addObserver(this);
        mController = new GameController(mModel, numberOfMines);

        FieldAdapter fieldAdapter = new FieldAdapter(this, mModel);

        gridView.setAdapter(fieldAdapter);
        timerThread = new TimerThread(this, timer, 0);

        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, TiltService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        Intent locationIntent = new Intent(this, MyLocationService.class);
        bindService(locationIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
        timerThread.setFinished(true);
    }

    private void waitAndEndGame(final boolean isWin) {
        timerThread.setFinished(true);

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int x = position % mModel.getSize();
        int y = position / mModel.getSize();

        if(!timerThread.isAlive()) {
            timerThread.start();
        }

        mController.reveal(x, y);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int x = position % mModel.getSize();
        int y = position / mModel.getSize();

        mController.toggleFlag(x, y);
        return false;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(mModel.isFinished()) {
            waitAndEndGame(mModel.isWin());
        }
    }

    public void resetGame() {
        mController.reset();
    }
}
