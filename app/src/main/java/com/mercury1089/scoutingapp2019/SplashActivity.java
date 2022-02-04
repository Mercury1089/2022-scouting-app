package com.mercury1089.scoutingapp2019;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private float volume = .25f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ConstraintLayout constraintLayout = findViewById(R.id.SplashActivity);
                ImageView lightningBolt = findViewById(R.id.LightningBolt);
                TextView developersText = findViewById(R.id.CreditWhereCreditsDue);

                mediaPlayer = MediaPlayer.create(SplashActivity.this, R.raw.thunder2);
                mediaPlayer.setVolume(volume, volume);

                int lightningBoltSpeed = 200;

                ObjectAnimator animatorX = ObjectAnimator.ofFloat(lightningBolt, View.X, 100, 200).setDuration(lightningBoltSpeed);
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(lightningBolt, View.Y, -200, 350).setDuration(lightningBoltSpeed);
                ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(lightningBolt, View.ALPHA, 0, 1).setDuration(lightningBoltSpeed);
                ObjectAnimator animatorScreenAlphaOff = ObjectAnimator.ofFloat(constraintLayout, View.ALPHA, 1, 0).setDuration(100);
                ObjectAnimator animatorTextAlpha = ObjectAnimator.ofFloat(developersText, View.ALPHA, 0, .5f).setDuration(0);
                ObjectAnimator animatorScreenAlphaOn = ObjectAnimator.ofFloat(constraintLayout, View.ALPHA, 0, 1).setDuration(100);
                ObjectAnimator anim = ObjectAnimator.ofFloat(developersText, View.ALPHA, .5f, 1.0f).setDuration(500);

                AnimatorSet lightningAnimation = new AnimatorSet();
                lightningAnimation.playTogether(animatorX, animatorY, animatorAlpha);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(lightningAnimation, animatorScreenAlphaOff, animatorTextAlpha, animatorScreenAlphaOn, anim);

                animatorSet.start();
                mediaPlayer.start();
                startFadeOut();
            }
        }, 750);

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                // After t seconds redirect to another intent
                Intent intent = new Intent(SplashActivity.this, PregameActivity.class);
                startActivity(intent);

                //Remove activity
                finish();
            }
        }, 2250);
    }

    private void startFadeOut(){
        final int FADE_DURATION = 3750; //The duration of the fade
        //The amount of time between volume changes. The smaller this is, the smoother the fade
        final int FADE_INTERVAL = 100;
        int numberOfSteps = FADE_DURATION/FADE_INTERVAL; //Calculate the number of fade steps
        //Calculate by how much the volume changes each step
        final float deltaVolume = volume / (float)numberOfSteps;

        //Create a new Timer and Timer task to run the fading outside the main UI thread
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeOutStep(deltaVolume); //Do a fade step
                //Cancel and Purge the Timer if the desired volume has been reached
                if(volume<=0f){
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.schedule(timerTask,FADE_INTERVAL,FADE_INTERVAL);
    }

    private void fadeOutStep(float deltaVolume){
        mediaPlayer.setVolume(volume, volume);
        volume -= deltaVolume;

    }
}
