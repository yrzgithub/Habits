package com.example.habits;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    Button yes,no;
    TextView passed,missed;
    int days_passed,times_missed;
    String today,yesterday,passed_txt,times_txt;
    SimpleDateFormat formatter;
    SharedPreferences data;
    SharedPreferences.Editor editor;
    MediaPlayer player;
    NotificationChannel channel;
    NotificationManager manager;


    @SuppressLint({"SimpleDateFormat", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        passed = findViewById(R.id.passed);
        missed = findViewById(R.id.missed);

        player = MediaPlayer.create(this,R.raw.hey);

        formatter = new SimpleDateFormat("dd MM yyyy");

        data = getSharedPreferences("Habits", Context.MODE_PRIVATE);
        editor = data.edit();

        today = formatter.format(new Date());
        yesterday = data.getString("Lastly clicked date","");

        days_passed = data.getInt("days_passed",0);
        times_missed = data.getInt("times_missed",0);

        yes.setOnClickListener(view -> {
            ++days_passed;
            player.start();
            Toast.makeText(MainActivity.this,"Button disabled",Toast.LENGTH_SHORT).show();
            update(false);
        });

        no.setOnClickListener(view -> {
            days_passed = 0;
            ++times_missed;
            Toast.makeText(MainActivity.this,"Button disabled",Toast.LENGTH_SHORT).show();
            update(false);
        });

        update(!today.equals(yesterday));

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            channel = new NotificationChannel("Habits","channel", NotificationManager.IMPORTANCE_MAX);
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if(!data.getBoolean("first time",false))
        {
            alarm();
            editor.putBoolean("first time",true);
            editor.apply();
        }

    }


    @SuppressLint("DefaultLocale")
    public void update(boolean enable)
    {
        passed_txt = days_passed>1? String.format("%d days passed",days_passed):String.format("%d day passed",days_passed);
        times_txt = times_missed>1? String.format("%d days missed",times_missed):String.format("%d day missed",times_missed);

        passed.setText(passed_txt);
        missed.setText(times_txt);

        if(!enable) {
            yes.setEnabled(false);
            no.setEnabled(false);
            editor.putString("Lastly clicked date",today);
            editor.commit();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        player.stop();
        editor.putInt("days_passed",days_passed);
        editor.putInt("times_missed",times_missed);
        editor.commit();

    }
    @SuppressLint("UnspecifiedImmutableFlag")
    private void alarm() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,7);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);

        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarm.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        Toast.makeText(MainActivity.this,"Daily notification has been set",Toast.LENGTH_SHORT).show();

    }
}
