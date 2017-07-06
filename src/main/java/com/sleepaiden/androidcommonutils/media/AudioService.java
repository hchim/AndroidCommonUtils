package com.sleepaiden.androidcommonutils.media;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

/**
 * Background service for playing audio.
 */
public class AudioService extends Service {
    public static final String TAG = "AudioService";
    public static final String ACTION_PLAY_STATUS = "sleepaiden.audioservice.play_status";
    public static final String EXTRA_PLAY_STATUS = "status";

    public enum PlayMode {
        Once, Loop
    }

    public enum PlayStatus {
        Playing, Stopped, Paused
    }

    private MediaPlayer[] players = new MediaPlayer[2];
    private MediaPlayer mMediaPlayer;
    private PlayMode mPlayMode = PlayMode.Once;
    private CountDownTimer mTimer;
    private Uri mAudioUri;
    private int currentPosition = -1;
    private int loops = 0;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "AudioService created.");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "AudioService destroyed.");
        stopAudio();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void pauseAudio() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            currentPosition = mMediaPlayer.getCurrentPosition();
            broadcastPlayStatus(PlayStatus.Paused);
        }
    }

    public void resumeAudio() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying() && currentPosition > 0) {
            mMediaPlayer.start();
            mMediaPlayer.seekTo(currentPosition);
            currentPosition = -1;
            broadcastPlayStatus(PlayStatus.Playing);
        }
    }

    public void playAudio(String audioUri, final PlayMode playMode, int playTime) {
        Log.d(TAG, "Play audio uri: " + audioUri);
        if (mMediaPlayer != null) {
            stopAudio();
        }

        if (audioUri == null) {
            Log.e(TAG, "Invalid intent message, audio uri not found.");
            return;
        }

        Uri uri = Uri.parse(audioUri);
        if (uri == null) {
            Log.e(TAG, "Failed to parse audio uri: " + audioUri);
            return;
        }
        mAudioUri = uri;
        mPlayMode = playMode;
        mMediaPlayer = createMediaPlayer();
        if (mMediaPlayer == null) {
            return;
        }

        if (mPlayMode == PlayMode.Loop) {
            players[0] = mMediaPlayer;
            players[1] = createMediaPlayer();
            players[0].setNextMediaPlayer(players[1]);
            createTimer(playTime);
        } else {
            players[0] = players[1] = null;
        }

        try {
            mMediaPlayer.start();
            loops = 0;
            broadcastPlayStatus(PlayStatus.Playing);
        } catch (Exception e) {
            Log.e(TAG, "Failed to play audio.", e);
            stopAudio();
        }
    }

    private MediaPlayer createMediaPlayer() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getApplicationContext(), mAudioUri);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (Exception e) {
            Log.d(TAG, "Failed to set audio source with uri: " + mAudioUri, e);
            return null;
        }
    }

    public void stopAudio() {
        if (mMediaPlayer != null) {
            if (players[0] != null) {
                players[0].release();
                players[1].release();
            } else {
                mMediaPlayer.release();
            }
            mMediaPlayer= null;
            broadcastPlayStatus(PlayStatus.Stopped);
        }
    }

    public PlayStatus getStatus() {
        if (mMediaPlayer == null) {
            return PlayStatus.Stopped;
        }

        if (mMediaPlayer.isPlaying()) {
            return PlayStatus.Playing;
        }

        return PlayStatus.Paused;
    }

    public void resetTimer(int playTimeInMin) {
        if (mMediaPlayer == null || mPlayMode != PlayMode.Loop) {
            return;
        }
        createTimer(playTimeInMin);
    }

    private void createTimer(int playTimeInM) {
        if (mTimer != null) {
            mTimer.cancel();
        }

        long playTimeInMillis = playTimeInM * 60000;
        mTimer = new CountDownTimer(playTimeInMillis, playTimeInMillis) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                stopAudio();
            }
        };
        mTimer.start();
        Log.d(TAG, "start timer : " + playTimeInM);
    }

    private void broadcastPlayStatus(PlayStatus playStatus) {
        Intent intent = new Intent(ACTION_PLAY_STATUS);
        intent.putExtra(EXTRA_PLAY_STATUS, playStatus.toString());
        sendBroadcast(intent);
    }

    //Only used in loop mode
    private final MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            loops++;
            Log.d(TAG, "Audio completed loop: " + loops);
            mediaPlayer.release();

            if (mediaPlayer == players[0]) {
                mMediaPlayer = players[1];
                players[0] = createMediaPlayer();
                mMediaPlayer.setNextMediaPlayer(players[0]);
            } else {
                mMediaPlayer = players[0];
                players[1] = createMediaPlayer();
                mMediaPlayer.setNextMediaPlayer(players[1]);
            }
        }
    };
}
