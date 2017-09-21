package mobilesecurity.mobileone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class TiltService extends Service implements SensorEventListener {

    private static final int SENSOR_DELAY = 50000;
    private static final float TILT_THRESHOLD = 0.1f;
    private static final long TIMESTAMP_THRESHOLD = 5000000000L;

    private final IBinder mBinder = new TiltBinder();
    private GameOn mGame;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private long tiltedTime = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("SENSOR", "Tilt service bound");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mSensor, SENSOR_DELAY);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("SENSOR", "Tilt service unbound");
        mSensorManager.unregisterListener(this);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(mGame != null) {
            if(mGame.initialTilt == null) {
                mGame.initialTilt = event.values.clone();
            }

            if(Math.abs(event.values[3] - mGame.initialTilt[3]) >= TILT_THRESHOLD){
                if( event.timestamp - tiltedTime > TIMESTAMP_THRESHOLD) {
                    mGame.resetGame();
                    tiltedTime = event.timestamp;
                }
            } else {
                tiltedTime = event.timestamp;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class TiltBinder extends Binder {
        TiltService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TiltService.this;
        }
    }

    public void setResetListener(GameOn game) {
        mGame = game;
    }
}
