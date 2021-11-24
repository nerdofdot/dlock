package com.example.d_lockv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class mainpage extends AppCompatActivity {

    Vibrator vibrator;

    LottieAnimationView temperatureanimation;
    TextView temperaturereading;
    TextView firestatus;

    LottieAnimationView dooranimation;
    TextView lockstatus;
    TextView lockinfo;

    LottieAnimationView motiondetectedanimation;
    TextView motiontime;
    TextView motionrealtime;
    TextView motionstatus;

    LottieAnimationView lightanimation;
    TextView lightstatus;

    TextView safetytxt;

    LottieAnimationView connectionanimation;
    TextView connectionstatus;

    LottieAnimationView voicefeedanimation;

    int doordata = 0;
    String doordatastr;

    int tempdata;
    String tempdatastr;

    int flamedata;
    String flamedatastr;

    int motiondata;
    String motiondatastr;
    long startTime = 0;
    long endTime = 0;
    long difference = 0;
    int minutes = 0;
    int hour = 0;
    String date="";

    int lightdata;
    String lightdatastr;

    int prevformotion =0;
    int voicefeedback=1;

    DatabaseReference lockdataindb;
    DatabaseReference tempdataindb;
    DatabaseReference flamedataindb;
    DatabaseReference motiondataindb;
    DatabaseReference lightdataindb;

    MediaPlayer alarmsound;
    MediaPlayer motionendedsound;
    MediaPlayer motiondetectedsound;
    MediaPlayer lightsound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        dooranimation = findViewById(R.id.dooranimation);
        lockstatus = findViewById(R.id.lockstatus);
        lockinfo = findViewById(R.id.lockinfo);

        temperatureanimation = findViewById(R.id.temperatureanimation);
        temperaturereading = findViewById(R.id.temperaturereading);
        firestatus = findViewById(R.id.firestatus);

        motiondetectedanimation = findViewById(R.id.motiondetectedanimation);
        motiontime = findViewById(R.id.motiontime);
        motionrealtime = findViewById(R.id.motionrealtime);
        motionstatus = findViewById(R.id.motiondescription);

        motiondetectedanimation.setAnimation(R.raw.motiondetectman);
        motiondetectedanimation.setFrame(4);

        lightanimation = findViewById(R.id.lightanimation);
        lightstatus = findViewById(R.id.lightstatus);

        safetytxt = findViewById(R.id.safetytxt);

        connectionanimation = findViewById(R.id.connectionstatus);
        connectionstatus= findViewById(R.id.connectionstatustxt);

        FirebaseDatabase.getInstance().setPersistenceEnabled(false);

        lockdataindb = FirebaseDatabase.getInstance().getReference("lockdata");
        lockdataindb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doordatastr = snapshot.getValue().toString();
                doordata = Integer.parseInt(doordatastr);

                connectionstatus.setText("Connected");
                connectionanimation.setAnimation(R.raw.connectedanimation2);
                connectionanimation.playAnimation();

                if (doordata == 1) {
                    //door is locked
                    dooranimation.setAnimation(R.raw.doorclosinganimation);
                    dooranimation.playAnimation();
                    lockstatus.setText("Locked");
                    lockinfo.setText("Tap to Unlock");

                    YoYo.with(Techniques.Shake)
                            .duration(1500)
                            .playOn(lockstatus);
                    YoYo.with(Techniques.FadeIn)
                            .duration(3000)
                            .playOn(lockinfo);
                    vibrator.vibrate(100);

                }
                else if (doordata == 2)
                {
                    dooranimation.setAnimation(R.raw.dooropeninganimationfinal);
                    dooranimation.playAnimation();
                    lockstatus.setText("Unlocked");
                    lockinfo.setText("Tap to Lock");

                    YoYo.with(Techniques.Shake)
                            .duration(1500)
                            .playOn(lockstatus);
                    YoYo.with(Techniques.FadeIn)
                            .duration(3000)
                            .playOn(lockinfo);
                    vibrator.vibrate(100);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // play disconnected animation
            }
        });

        tempdataindb = FirebaseDatabase.getInstance().getReference("temperature");
        tempdataindb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tempdatastr = snapshot.getValue().toString();
                tempdata = Integer.parseInt(tempdatastr);
                temperaturereading.setText(tempdata + " °c");
                YoYo.with(Techniques.Landing)
                        .duration(3000)
                        .playOn(temperaturereading);
                if(flamedata!=1)
                {
                    settemperatureanimation();
                }

                if (tempdata >= 55) {
                    temperaturereading.setTextColor(Color.parseColor("#FF6161"));
                } else if (tempdata < 55) {
                    temperaturereading.setTextColor(Color.parseColor("#4D4D4D"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        alarmsound = MediaPlayer.create(mainpage.this,R.raw.alarm);
        motionendedsound = MediaPlayer.create(mainpage.this,R.raw.motionendedsound);
        motiondetectedsound = MediaPlayer.create(mainpage.this,R.raw.motionstartedsound);
        lightsound = MediaPlayer.create(mainpage.this,R.raw.lightsound2);

        flamedataindb = FirebaseDatabase.getInstance().getReference("flame");
        flamedataindb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                flamedatastr = snapshot.getValue().toString();
                flamedata = Integer.parseInt(flamedatastr);
                if (flamedata == 1) {
                    temperatureanimation.setAnimation(R.raw.flameanimation);
                    temperatureanimation.playAnimation();
                    firestatus.setText("FIRE FIRE FIRE!");
                    firestatus.setTextColor(Color.parseColor("#FF6161"));

                    YoYo.with(Techniques.Tada)
                            .duration(1000)
                            .repeat(25)
                            .playOn(firestatus);
                    setalarm();
                    safetycheck();
                }
                else if (flamedata == 0) {
                    settemperatureanimation();

                    firestatus.setText("House not on fire");
                    firestatus.setTextColor(Color.WHITE);
                    firestatus.setAnimation(null);
                    setalarm();
                    safetycheck();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        motiondataindb = FirebaseDatabase.getInstance().getReference("motion");
        motiondataindb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                motiondatastr = snapshot.getValue().toString();
                motiondata = Integer.parseInt(motiondatastr);

                if(motiondata==0)
                {
                    motiondetectedanimation.setAnimation(R.raw.motiondetectman);
                    motiondetectedanimation.pauseAnimation();
                    motiondetectedanimation.setFrame(4);
                    motionendedsound.start();
                    if(prevformotion==1)
                    {
                        endTime = System.currentTimeMillis();
                        difference = endTime - startTime;
                        int diff = (int) difference / 1000;
                        minutes = diff / 60;
                        hour = diff / 3600;
                        String diffstr = Integer.toString(diff);
                        String minutesstr = Integer.toString(minutes);
                        String hours = Integer.toString(hour);

                        if (diff < 60 && hour == 0 && minutes == 0)
                        {
                            motiontime.setText(diffstr + " sec");
                        }
                        else if (minutes < 60 && minutes > 0 && hour == 0)
                        {
                            int sec = minutes * 60;
                            int secremain = diff - sec;
                            String secremaining = Integer.toString(secremain);
                            motiontime.setText(minutesstr + " min " + secremaining + " sec");
                        }
                        else if (hour != 0)
                        {
                            int min = hour * 60;
                            int minremain = minutes - min;
                            int sec = minutes * 60;
                            int secremain = diff - sec;
                            String minremaining = Integer.toString(minremain);
                            String secremaining = Integer.toString(secremain);
                            motiontime.setText(hours + " h  " + minremaining + " m  " + secremaining + " s");
                        }

                        YoYo.with(Techniques.Shake)
                                .duration(1000)
                                .playOn(motiontime);
                    }
                    prevformotion=1;
                    vibrator.vibrate(200);

                    motionstatus.setText("No Motion");
                    motionstatus.setTextColor(Color.parseColor("#4D4D4D"));

                    YoYo.with(Techniques.Wave)
                            .duration(1000)
                            .repeat(0)
                            .playOn(motionstatus);
                    motionrealtime.setTextColor(Color.parseColor("#34EBD8"));
                    motionrealtime.setText("Seen- "+date);

                    setalarm();
                    safetycheck();
                }

                else if(motiondata==1)
                {
                    motiondetectedanimation.setAnimation(R.raw.motiondetectman);
                    motiondetectedanimation.playAnimation();
                    motiondetectedsound.start();
                    prevformotion=1;
                    motiontime.setText("Calculating...");
                    DateFormat df = new SimpleDateFormat("h:mm a");
                    date = df.format(Calendar.getInstance().getTime());

                    startTime = System.currentTimeMillis();
                    vibrator.vibrate(200);

                    motionstatus.setText("Motion Detected");
                    motionstatus.setTextColor(Color.parseColor("#34EBD8"));
                    YoYo.with(Techniques.Wobble)
                            .duration(1500)
                            .playOn(motionstatus);
                    motionrealtime.setText("Seen- "+date);
                    motionrealtime.setTextColor(Color.parseColor("#4D4D4D"));
                    setalarm();
                    safetycheck();

                }

                else if(motiondata==2)
                {
                    motiondetectedanimation.setAnimation(R.raw.warningbreaking);
                    motiondetectedanimation.playAnimation();
                    prevformotion=1;
                    vibrator.vibrate(200);
                    motionrealtime.setText("Breaking in!");
                    motionrealtime.setTextColor(Color.parseColor("#FF6161"));

                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .repeat(25)
                            .playOn(motionrealtime);
                    motionstatus.setText("Messaging Police.");
                    motionstatus.setTextColor(Color.parseColor("#4D4D4D"));
                    YoYo.with(Techniques.FadeIn)
                            .duration(1000)
                            .repeat(10)
                            .playOn(motionstatus);
                    setalarm();
                    safetycheck();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        lightdataindb = FirebaseDatabase.getInstance().getReference("ldrdata");
        lightdataindb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                lightdatastr = snapshot.getValue().toString();
                lightdata = Integer.parseInt(lightdatastr);

                if(lightdata==0)
                {
                    //lights off
                    lightanimation.setAnimation(R.raw.lightsoffanimationfinal);
                    lightanimation.playAnimation();
                    lightsound.start();
                    lightstatus.setText("OFF");
                    YoYo.with(Techniques.Swing)
                            .duration(3000)
                            .playOn(lightstatus);
                }
                else if(lightdata==1)
                {
                    lightanimation.setAnimation(R.raw.lightonanimation);
                    lightanimation.playAnimation();
                    lightsound.start();
                    lightstatus.setText("ON");
                    YoYo.with(Techniques.Swing)
                            .duration(3000)
                            .playOn(lightstatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void doorlockingmechanism(View v)
    {
        if (InternetConnection.checkConnection(this))
        {
            // Its Available...
            if(doordata==1)
            {
                //setvaluetodb
                lockdataindb.setValue(2);

            }

            else if(doordata==2)
            {
                lockdataindb.setValue(1);
            }
        }
        else
        {
            // Not Available...
            connectionstatus.setText("Disconnected");
            connectionanimation.setAnimation(R.raw.disconnectedanimation);
            connectionanimation.playAnimation();
            vibrator.vibrate(200);
        }

    }

    public void safetycheck()
    {
        if(flamedata==1||motiondata==2)
        {
            safetytxt.setText("House is unsafe");
            safetytxt.setTextColor(Color.parseColor("#FF6161"));
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(safetytxt);
        }
        else
        {
            safetytxt.setText("House is safe");
            safetytxt.setTextColor(Color.parseColor("#FFFFFF"));
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .playOn(safetytxt);
        }
    }

    public void settemperatureanimation()
    {
        if(tempdata<15)
        {
            temperatureanimation.setAnimation(R.raw.winteranimation3);
            temperatureanimation.playAnimation();
        }
        else if(tempdata>=15&&tempdata<=25)
        {
            temperatureanimation.setAnimation(R.raw.sunnyanimation6);
            temperatureanimation.playAnimation();
        }
        else
        {
            temperatureanimation.setAnimation(R.raw.extremesunnyanimation2);
            temperatureanimation.playAnimation();
        }
    }

    public void setalarm()
    {
        if(flamedata==1||motiondata==2)
        {
            alarmsound.start();
            alarmsound.setLooping(true);
        }
        else
        {
            alarmsound.setLooping(false);
        }
    }
}