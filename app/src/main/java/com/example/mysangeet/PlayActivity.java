package com.example.mysangeet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chibde.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends AppCompatActivity {
Button btnPlay,btnNext,btnPrevious,btnFastForward,btnFastBackward;
TextView txtSongName,txtSongStart,txtSongEnd;
SeekBar seekMusicBar;
BarVisualizer barVisualizer;


ImageView imageView;
static MediaPlayer mediaPlayer;
String songName;
int position;
ArrayList<File> mySongs;
Thread updateSeekBar;
public static final  String EXTRA_NAME= "song_name";

//    protected void onDestroy() {
//        super.onDestroy();
//        mediaPlayer.pause();
//        mediaPlayer.release();
////        updateSeekBar.interrupt();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        btnPlay=findViewById(R.id.btnPlay);
        btnNext=findViewById(R.id.btnNext);
        btnPrevious=findViewById(R.id.btnPrev);
        btnFastBackward=findViewById(R.id.btnFastBackward);
        btnFastForward=findViewById(R.id.btnFastForward);

        txtSongName=findViewById(R.id.txtSong);
        txtSongEnd=findViewById(R.id.txtSongEnd);
        txtSongStart=findViewById(R.id.txtSongStart);

        seekMusicBar=findViewById(R.id.seekBar);
        barVisualizer=findViewById(R.id.wave);

        imageView=findViewById(R.id.imgView);


        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
//        if(barVisualizer!=null){
//            barVisualizer.release();
//        }
        Intent intent = getIntent();
        Bundle bundle= intent.getExtras();

        mySongs=(ArrayList)bundle.getParcelableArrayList("songs");
        String sName = intent.getStringExtra("songname");
       position=bundle.getInt("pos",0);
       txtSongName.setSelected(true);
       Uri uri = Uri.parse(mySongs.get(position).toString());
//       songName=mySongs.get(position).getName();
//       txtSongName.setText(songName);
        txtSongName.setText(sName);

       mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
       mediaPlayer.start();

       updateSeekBar=new Thread(){
           @Override
           public void run() {
               int totalDuration = 100000000;
               int currentPosition=0;
               while (currentPosition < totalDuration) {
                   try {
                       sleep(500);
                       currentPosition = mediaPlayer.getCurrentPosition();
                       seekMusicBar.setProgress(currentPosition);

                   } catch (InterruptedException | IllegalStateException e) {
                       e.printStackTrace();
                   }
               }
//               while(currentPosition==totalDuration){
//                   try{
//                       sleep(200);
//                       currentPosition=mediaPlayer.getCurrentPosition();
//                       seekMusicBar.setProgress(0);
//                   }
//                   catch(InterruptedException|IllegalStateException e){
//                       e.printStackTrace();
//                   }
//               }

           }
       };
       seekMusicBar.setMax(mediaPlayer.getDuration());
       updateSeekBar.start();
       seekMusicBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.purple_700), PorterDuff.Mode.MULTIPLY);
       seekMusicBar.getThumb().setColorFilter(getResources().getColor(R.color.purple_700),PorterDuff.Mode.SRC_IN);

       seekMusicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(seekBar.getProgress());
           }
       });

       String endTime=createTime(mediaPlayer.getDuration());
       txtSongEnd.setText(endTime);

       final Handler handler=new Handler();
       final int delay=1000;
       handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               String currentTime = createTime(mediaPlayer.getCurrentPosition());
               txtSongStart.setText(currentTime);
               handler.postDelayed(this,delay);
           }
       },delay);

       btnPlay.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(mediaPlayer.isPlaying()){
                   btnPlay.setBackgroundResource(R.drawable.ic_play);
                   mediaPlayer.pause();
               }
               else{
                   btnPlay.setBackgroundResource(R.drawable.ic_pause);
                   mediaPlayer.start();

                   TranslateAnimation animation=new TranslateAnimation(-25,25,-25,25);
                    animation.setInterpolator(new AccelerateInterpolator());
                    animation.setDuration(600);
                    animation.setFillEnabled(true);
                    animation.setFillAfter(true);
                    animation.setRepeatMode(Animation.REVERSE);
                    animation.setRepeatCount(1);
                    imageView.startAnimation(animation);

               }
           }
       });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnNext.performClick();
                seekMusicBar.setProgress(0);
            }
        });
        // set custom color to the line.
        barVisualizer.setColor(ContextCompat.getColor(this,R.color.purple_700));
// define custom number of bars you want in the visualizer between (10 - 256).
        barVisualizer.setDensity(70);
// Set your media player to the visualizer.
        barVisualizer.setPlayer(mediaPlayer.getAudioSessionId());

       btnNext.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mediaPlayer.stop();
//               mediaPlayer.release();
               btnPlay.setBackgroundResource(R.drawable.ic_pause);
               position=((position+1)%mySongs.size());
               Uri uri = Uri.parse(mySongs.get(position).toString());
               mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
               songName=mySongs.get(position).getName();
               txtSongName.setText(songName);
               mediaPlayer.start();
               seekMusicBar.setMax(mediaPlayer.getDuration());//
               String endTime=createTime(mediaPlayer.getDuration());//
               txtSongEnd.setText(endTime);//
               startAnimation(imageView,360f);
               barVisualizer.release();
               // set custom color to the line.
               barVisualizer.setColor(ContextCompat.getColor(getApplicationContext(),R.color.purple_700));
// define custom number of bars you want in the visualizer between (10 - 256).
               barVisualizer.setDensity(70);
// Set your media player to the visualizer.
               barVisualizer.setPlayer(mediaPlayer.getAudioSessionId());
               mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                   @Override
                   public void onCompletion(MediaPlayer mp) {
                       btnNext.performClick();
                       seekMusicBar.setProgress(0);
                   }
               });
           }
       });


        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
//                mediaPlayer.release();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                position=((position-1)<0)?(mySongs.size()-1):position-1;
                Uri uri = Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                songName=mySongs.get(position).getName();
                txtSongName.setText(songName);
                mediaPlayer.start();
                String endTime=createTime(mediaPlayer.getDuration());//
                txtSongEnd.setText(endTime);//
                seekMusicBar.setMax(mediaPlayer.getDuration());//
                startAnimation(imageView,-360f);
                barVisualizer.release();
                // set custom color to the line.
                barVisualizer.setColor(ContextCompat.getColor(getApplicationContext(),R.color.purple_700));
// define custom number of bars you want in the visualizer between (10 - 256).
                barVisualizer.setDensity(70);
// Set your media player to the visualizer.
                barVisualizer.setPlayer(mediaPlayer.getAudioSessionId());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btnNext.performClick();
                        seekMusicBar.setProgress(0);
                    }
                });

            }
        });

        btnFastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        btnFastBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });

    }

    public void startAnimation(View view, Float degree)
    {
        ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(imageView,"rotation",8f,degree);
        objectAnimator.setDuration(1000);
        AnimatorSet animatorSet= new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();

    }
    public String createTime(int duration)
    {
        String time ="";
        int min= duration/1000/60;
        int sec=duration/1000%60;

        time=time+min+":";
        if(sec<10)
        {
            time+="0";
        }
        time+=sec;
        return time;
    }

//    @Override
//    public void onBackPressed() {
//        barVisualizer.release();
//        super.onBackPressed();
//    }
}