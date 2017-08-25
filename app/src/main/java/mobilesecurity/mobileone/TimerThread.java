package mobilesecurity.mobileone;

import android.app.Activity;
import android.widget.TextView;

class TimerThread extends Thread {
    private TextView view;
    private int currentTime;
    private boolean isFinished;
    private Activity activity;

    TimerThread(Activity activity, TextView view, int startTime) {
        currentTime = startTime;
        this.view = view;
        this.activity = activity;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    @Override
    public void run() {
        while(!isFinished) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setText(currentTime + "");
                }
            });
            currentTime++;
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getCurrentTime(){
        return currentTime;
    }

}
