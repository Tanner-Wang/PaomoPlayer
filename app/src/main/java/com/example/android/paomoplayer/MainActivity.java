package com.example.android.paomoplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final static String SONG_URL = "http://ws.stream.qqmusic.qq.com/1530858.m4a?fromtag=46";

    MediaPlayer m;

    AudioManager manager;

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

            releaseMediaPlayer();
        }
    };


    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {

                m.pause();
                m.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {

                m.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {

                releaseMediaPlayer();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m = new MediaPlayer();

        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        TextView textView = (TextView) findViewById(R.id.button_play);
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (m != null) {
                    releaseMediaPlayer();
                }
                    m = new MediaPlayer();
                    manager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                    PaomoAsyncTask task = new PaomoAsyncTask();
                    task.execute(SONG_URL);
            }
        });
    }

    public class PaomoAsyncTask extends AsyncTask<String, Void, MediaPlayer>{

        public PaomoAsyncTask(){}

        @Override
        protected void onPostExecute(MediaPlayer mediaPlayer) {
            m.start();
            if (m.isPlaying()){
                mediaPlayer.setOnCompletionListener(mCompletionListener);
            }

        }

        @Override
        protected MediaPlayer doInBackground(String... url) {
            try {
                m.setAudioStreamType(AudioManager.STREAM_MUSIC);
                m.setDataSource(url[0]);
                m.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return m;
        }
    }

    public void releaseMediaPlayer()
    {
        m.release();
        m = null;
        manager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }
}
