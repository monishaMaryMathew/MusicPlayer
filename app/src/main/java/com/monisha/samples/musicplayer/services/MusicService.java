package com.monisha.samples.musicplayer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;

import com.monisha.samples.musicplayer.Model.Song;
import com.monisha.samples.musicplayer.R;
import com.monisha.samples.musicplayer.activities.MainActivity;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    private MediaPlayer player;
    private List<Song> songList;
    private int songPosition;
    private final IBinder musicBind = new MusicBinder();

    private String songTitle="";
    private static final int NOTIFY_ID=1;

    private boolean shuffle=false;
    private Random rand;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        songPosition = 0;
        player = new MediaPlayer();
        initMusicPlayer();
        rand=new Random();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(player.getCurrentPosition()==0){
            mediaPlayer.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
  .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    public void setListOfSongs(List<Song> listOfSongs) {
        songList = listOfSongs;
    }

    public class MusicBinder extends Binder{
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong(){
        player.reset();
        Song playSong = songList.get(songPosition);
        songTitle = playSong.getTitle();
        long currSong = playSong.getId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.prepareAsync();
    }

    public void setSong(int songIndex){
        songPosition=songIndex;
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void playPrev(){
        songPosition--;
        if(songPosition==0) songPosition=songList.size()-1;
        playSong();
    }
    public void playNext(){
        if(shuffle){
            int newSong = songPosition;
            while(newSong==songPosition){
                newSong=rand.nextInt(songList.size());
            }
            songPosition=newSong;
        }
        else{
            songPosition++;
            if(songPosition==songList.size()) songPosition=0;
        }
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }
}
