package com.nttuyen.android.umon.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

/**
 * @author nttuyen266@gmail.com
 */
public class Mp3Player extends RelativeLayout implements View.OnClickListener, MediaPlayer.OnCompletionListener {
    protected LayoutInflater mInflater;
    protected RelativeLayout rootView;
    protected Button playerButton;
    protected ProgressBar playerTimeline;
    protected MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    protected boolean repeatable = true;
    protected boolean completed = false;
    private String currentDataSource = null;
    private String dataSource = null;
    private boolean isPlaying = false;

    protected Listener listener;
    protected final UIContextHelper contextHelper;

    public Mp3Player(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = (RelativeLayout) mInflater.inflate(R.layout.umon_widget_mp3_player, null);
        this.addView(rootView);

        this.playerTimeline = (ProgressBar) rootView.findViewById(R.id.playerTimeline);
        this.playerButton = (Button) rootView.findViewById(R.id.playerButton);
        this.playerButton.setOnClickListener(this);

        this.mediaPlayer = null;
        this.contextHelper = new UIContextHelper(this.getContext());
    }

    public void setDataSource(String datasource) {
        this.setDataSource(datasource, true);
    }
    public void setDataSource(String dataSource, boolean repeatable) {
        this.dataSource = dataSource;
        this.repeatable = repeatable;
    }

    public void pause() {
        if(this.listener != null) {
            this.listener.onPaused(this);
        }
        this.setIsPlaying(false);
        if(this.mediaPlayer != null) {
            this.mediaPlayer.pause();
        }
        this.playerButton.setText("Play");
    }

    public void play() {
        try {
            if(this.listener != null) {
                this.listener.onPlayStart(this);
            }

            if(this.dataSource == null || this.dataSource.isEmpty()) {
                return;
            }

            this.setIsPlaying(true);
            if(this.mediaPlayer == null) {
                this.mediaPlayer = new MediaPlayer();
                this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        contextHelper.dismissLoading();
                        playerTimeline.setMax(mediaPlayer.getDuration());
                        updateProgressBar();
                        mediaPlayer.start();
                    }
                });
                this.mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                        contextHelper.dismissLoading();
                        contextHelper.showErrDialog("Error", "Error on play mp3!");
                        return false;
                    }
                });
            }
            if(!this.dataSource.equals(this.currentDataSource)) {
                this.currentDataSource = this.dataSource;
                this.mediaPlayer.stop();
                this.mediaPlayer.reset();

                this.currentDataSource = this.dataSource;
                this.mediaPlayer.setDataSource(this.currentDataSource);
                this.mediaPlayer.prepareAsync();
                this.contextHelper.showLoading();
            } else {
                this.mediaPlayer.start();
            }
            playerButton.setText("Pause");
        } catch (Exception e) {
            this.contextHelper.showErrDialog("Error", e.getMessage());
        }
    }

    public void destroy() {
        if(this.mediaPlayer != null) {
            try {
                this.mediaPlayer.release();
                this.mediaPlayer = null;
            } catch (Exception e) {}
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(this.isPlaying()) {
            this.pause();
        } else {
            this.play();
        }
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        if(this.listener != null) {
            this.listener.onPlayCompleted(this);
        }

        this.completed = true;
        playerButton.setText("Play");
        this.playerButton.setEnabled(this.repeatable);
    }

    private boolean isPlaying() {
        synchronized (this) {
            return this.isPlaying;
        }
    }
    private void setIsPlaying(boolean b) {
        synchronized (this) {
            this.isPlaying = b;
        }
    }

    private Runnable onEverySecond=new Runnable() {
        public void run() {
            if (mediaPlayer!=null) {
                playerTimeline.setProgress(mediaPlayer.getCurrentPosition());
            }
            handler.postDelayed(this, 1000);
        }
    };
    private void updateProgressBar() {
        handler.postDelayed(onEverySecond, 1000);
    }

    @Override
    protected void finalize() throws Throwable {
        this.destroy();
        super.finalize();
    }

    public interface Listener {
        public void onPlayStart(Mp3Player player);
        public void onPlayCompleted(Mp3Player player);
        public void onPaused(Mp3Player player);
    }
}
