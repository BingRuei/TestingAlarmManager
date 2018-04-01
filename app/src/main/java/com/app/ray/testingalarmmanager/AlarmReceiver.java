package com.app.ray.testingalarmmanager;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    private int requestCode;
    private String s;

    @Override
    public void onReceive(Context context, Intent intent) {

        requestCode = intent.getStringExtra("test") == null ?
                0 : Integer.parseInt(intent.getStringExtra("test"));
        s = intent.getStringExtra("test") == null ?
                "Reboot" : intent.getStringExtra("test");

        int TIME_INTERVAL = 6000;
        // 重复定时任务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("1111", "AlarmReceiver 1");
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("test", requestCode);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, 0);
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TIME_INTERVAL, pendingIntent);
//            manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.i("1111", "AlarmReceiver 2");
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("test", requestCode);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, 0);
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TIME_INTERVAL, pendingIntent);
        }

        // to do something
        doSomething(context);
    }

    private void doSomething(Context context) {
        /** 這裡是喚醒螢幕 **/
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //獲取電源管理器對象
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "1111");
        //獲取PowerManager.WakeLock對象,後面的參數|表示同時傳入兩個值,最後的是LogCat裡用的Tag

//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK  | PowerManager.ON_AFTER_RELEASE, "wakeup");

        wl.acquire();
        //點亮屏幕
        wl.release();
        //釋放

        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        //得到鍵盤鎖管理器對象
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //參數是LogCat裡用的Tag
        kl.disableKeyguard();
        //解鎖
        /** 這裡是喚醒螢幕 **/

        Log.i("1111", "Running"+requestCode);
        // For our recurring task, we'll just display a message
        Toast.makeText(context, "I'm running "+s, Toast.LENGTH_SHORT).show();
    }
}