package com.philliphsu.clock2.timers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.view.ViewGroup;

import com.philliphsu.clock2.AsyncTimersTableUpdateHandler;
import com.philliphsu.clock2.R;
import com.philliphsu.clock2.Timer;
import com.philliphsu.clock2.ringtone.RingtoneActivity;
import com.philliphsu.clock2.ringtone.RingtoneService;

public class TimesUpActivity extends RingtoneActivity<Timer> {
    private static final String TAG = "TimesUpActivity";

    private TimerController mController;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopService(new Intent(this, TimerNotificationService.class));
        // TODO: Consider calling this in the service's onDestroy()
        TimerNotificationService.cancelNotification(this, getRingingObject().getId());
        mController = new TimerController(getRingingObject(),
                new AsyncTimersTableUpdateHandler(this, null));
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void finish() {
        super.finish();
        mNotificationManager.cancel(TAG, getRingingObject().getIntId());
    }

    @Override
    protected Class<? extends RingtoneService> getRingtoneServiceClass() {
        return TimerRingtoneService.class;
    }

    @Override
    protected CharSequence getHeaderTitle() {
        return getRingingObject().label();
    }

    @Override
    protected void getHeaderContent(ViewGroup parent) {
        // Inflate the content and apply the parent's layout params, but don't
        // attach it to the parent yet. This is so the return value can be
        // the root of the inflated content, and not the parent. Alternatively,
        // we could set an id on the root of the content's layout and find it
        // from the returned parent.
        CountdownChronometer countdown = (CountdownChronometer) getLayoutInflater()
                .inflate(R.layout.content_header_timesup_activity, parent, false);
        countdown.setBase(SystemClock.elapsedRealtime());
        countdown.start();
        parent.addView(countdown);
    }

    @Override
    protected int getAutoSilencedDrawable() {
        // TODO: correct icon
        return R.drawable.ic_half_day_1_24dp;
    }

    @Override
    protected int getAutoSilencedText() {
        return R.string.timer_auto_silenced_text;
    }

    @Override
    protected int getLeftButtonText() {
        return R.string.add_one_minute;
    }

    @Override
    protected int getRightButtonText() {
        return R.string.stop;
    }

    @Override
    protected int getLeftButtonDrawable() {
        // TODO: correct icon
        return R.drawable.ic_half_day_1_24dp;
    }

    @Override
    protected int getRightButtonDrawable() {
        return R.drawable.ic_stop_24dp;
    }

    @Override
    protected void onLeftButtonClick() {
        mController.addOneMinute();
        stopAndFinish();
    }

    @Override
    protected void onRightButtonClick() {
        mController.stop();
        stopAndFinish();
    }

    // TODO: Consider changing the return type to Notification, and move the actual
    // task of notifying to the base class.
    @Override
    protected void showAutoSilenced() {
        super.showAutoSilenced();
        Notification note = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.timer_expired))
                .setContentText(getRingingObject().label())
                .setSmallIcon(R.mipmap.ic_launcher) // TODO: correct icon
                .build();
        mNotificationManager.notify(TAG, getRingingObject().getIntId(), note);
    }
}