package com.yelelen.sfish.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.yelelen.sfish.R;
import com.yelelen.sfish.activity.MainActivity;
import com.yelelen.sfish.contract.MediaPlayerListener;

public class SoundService extends Service {
    private MediaPlayer mPlayer;
    private SoundBinder mBinder;
    private MediaPlayerListener mListener;

    public SoundService() {
        Log.e("xxxx", "SoundService");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mBinder = new SoundBinder();
        initPlayerListener();
        stayForeground();
        Log.e("xxxx", "SoundService onCreate");
    }

    private void stayForeground() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, "SoundSerivce")
                .setAutoCancel(true)
                .setContentTitle("I'm a fish")
                .setContentText("I'm a fish.")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo))
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }

    private void initPlayerListener() {
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mBinder.play();
            }
        });

        mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mBinder.play();
                if (mListener != null)
                    mListener.onSeekDone();
            }
        });

        mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (mListener != null)
                    mListener.onBufferingUpdate(mp, percent);
            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mListener != null)
                    mListener.onCompletion();
            }
        });
    }



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class SoundBinder extends Binder {
        public void init(String pathOrUrl) {
            if (mPlayer != null) {
                if (mPlayer.isPlaying())
                    mPlayer.stop();
                mPlayer.reset();
            }

            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                if (pathOrUrl.startsWith("http://"))
                    mPlayer.setDataSource(SoundService.this, Uri.parse(pathOrUrl));
                else
                    mPlayer.setDataSource(pathOrUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();
        }

        public void play() {
            if (mPlayer != null && !mPlayer.isPlaying()) {
                mPlayer.start();
                if (mListener != null)
                    mListener.onPlayerPlay();
            }
        }

        public void pause() {
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.pause();
                if (mListener != null)
                    mListener.onPlayerPause();
            }
        }

        public void replay() {
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                }
                mPlayer.seekTo(0);
                mPlayer.start();
            }
        }

        public int getPlayedDuration() {
            if (mPlayer != null) {
                return Math.round(mPlayer.getCurrentPosition() / 1000);
            }
            return 0;
        }

        public void playNext(String pathOrUrl) {
            init(pathOrUrl);
        }

        public void playPrevious(String pathOrUrl) {
            init(pathOrUrl);
        }

        public int getDuration() {
           return  mPlayer.getDuration() / 1000;
        }

        public void seekTo(int secs) {
            mPlayer.seekTo(secs * 1000);
        }

        public boolean isPlaying() {
            return mPlayer.isPlaying();
        }


        public void stopSoundService() {
            stopSelf();
            if (mPlayer.isPlaying())
                mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        public void setListener(MediaPlayerListener listener) {
            mListener = listener;
        }

    }
}
