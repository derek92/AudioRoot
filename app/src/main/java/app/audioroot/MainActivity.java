package app.audioroot;
import static app.audioroot.GetSongPaths.getSongPaths;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;

import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;

import android.widget.CompoundButton;

import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SettingsFragment.SettingsFragListen, MusicSettingFragment.MSListen, MusicListFragment.MusicListen, InputListFragment.InputListen{


    boolean killed = false;
    int[] songs = getSongPaths();
    int ind = 0;
    String[] musicList = {"Homos In Space", "No Photograph", "Prelude in C Major"};

    int active;

    /*
        EARPIECE;
        EXTSPKR;
        HP;
        BT;
      */
    updateSong timer;
    int songspot;
    MediaPlayer mp;
    AudioManager AM;
    SwipeDetector swipeDetector ;
    RadioButton list;
    RadioButton music;
    RadioButton player;
    //progbar and times
    SeekBar songBar;
    TextView timeleft;
    TextView timespent;
    InputListFragment ILF;

    public boolean frag = false;
    public static final boolean d = true;
    public static void printd(boolean d, String msg){
        if(d) Log.d("DEVOUTPUT: " , msg);}

    FragmentManager f = getFragmentManager();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        active = AudioManager.STREAM_RING; //default before user picks setting

        setContentView(R.layout.indivport);
        music = (RadioButton) findViewById(R.id.Music);
        list = (RadioButton) findViewById(R.id.Output);
        ILF = new InputListFragment(false, active);
        setOPFrag();

        songspot = 0;
        swipeDetector = new SwipeDetector();

        mp = new MediaPlayer();//initialize
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {  //mediaplayer prepared to play
                if (!mp.isPlaying()) {
                    playSong();
                }
            }
        });
        AM = (AudioManager) getSystemService(Context.AUDIO_SERVICE); //set our audiomanager for outputs

        //mediaplayer progressbuttons
        //progbar and times
        songBar = (SeekBar) findViewById(R.id.seekBar4);
        timeleft = (TextView) findViewById(R.id.timeLeft);
        timespent = (TextView) findViewById(R.id.timeSpent);

        songBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekBar.setProgress(progress);
                    mp.seekTo(progress * 1000);
                    songspot = progress * 1000;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                timer.kill();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                timer.resume();
            }
        });


        if (list != null) {
            list.setChecked(true);
            setButtonsSwitch();
        }

        player = (RadioButton) findViewById(R.id.player);
        player.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    MusicSettingFragment MS = (MusicSettingFragment) f.findFragmentByTag("MS");
                    FragmentTransaction ft = f.beginTransaction();
                    if (MS == null) //not added
                    {
                    } else { //remove, button doesnt work right
                        ft.remove(MS);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                        ft.commit();
                    }
                }
                else   //open! and dont overlap and use up memory
                {

                    MusicSettingFragment MS =  (MusicSettingFragment) f.findFragmentByTag("MS");
                    FragmentTransaction ft = f.beginTransaction();
                    if(MS == null)
                    {
                        MS = new MusicSettingFragment(musicList[ind], mp.isPlaying());  //player initialization
                        ft.add(R.id.frameside, MS, "MS").commit();

                    }
                    else
                    {
                        MS.setSong(musicList[ind], mp.isPlaying()); //update the song title in window
                        ft.replace(R.id.frameside, MS, "MS").commit();
                    }
                    player.setChecked(false);
                }
            }

        });

    }

    public void playSong()
    {
        try {

            if(songspot > 0)
                mp.seekTo(songspot);
            mp.start();
            setInfo();
        }catch(Exception e)
        {
            e.getStackTrace();
        }
    }


    //MusicListen
    public void play(int ind, int place) {
        songspot = place;
        this.ind = ind; //set index bc chosen song in fragment list is a new song.


        if(timer != null)
            timer.kill(); //stop checking time, new song needed

        if (mp != null) {
            if(mp.isPlaying())
                mp.stop(); //stop music

            mp.reset(); //good measure
        }

        String x = "android.resource://" + getPackageName() + "/" + songs[ind];
      try {
          mp.setDataSource(getApplicationContext(),
                  Uri.parse(x)); //song

          mp.prepareAsync();
      }
      catch (Exception e)
      {
          e.getStackTrace();
      }
    }

    //MsListen
    @Override
   public void musicFunction(String action)
    {   //buttons
        if(action.compareTo("back") == 0)
        {
            ind = (ind - 1 == -1) ? songs.length - 1 : ind - 1;
            play(ind, 0);
        }
        if(action.compareTo("forward") == 0)
        {
            ind = (ind + 1 == songs.length) ? 0 : ind + 1;
            play(ind,0);
        }
        if(action.compareTo("play") == 0)
        {
            if(mp.isPlaying())
            {
                mp.pause();
            }
            else
                mp.start();
        }
}

    public void setInfo() {  //set ingo for songs on the fragment player

        MusicSettingFragment MS =  (MusicSettingFragment) f.findFragmentByTag("MS");
        if(MS != null) {
            MS.setSong(musicList[ind], mp.isPlaying());
        }

            final int duration = mp.getDuration() / 1000; //milliseconds / 1000 = seconds
            songBar.setMax(duration);
            timer = new updateSong(duration);//run timer for songs to update seekbar and time
    }

    //InputListener
    public void input(Bundle b)
    {
        onResult(b);
    }

    //SettingsFragmentListener
    public void onResult(Bundle data)
    {
        String title = data.getString("name");
        boolean on = data.getBoolean("on");

        maintainAudio(data.getInt("audio"), on);
    }

    public void closeFrag()
    {
        this.frag = false;
        ILF.updatefrag(this.frag);
    }

    public void maintainAudio(int streamtype, boolean on) {
        int equiv = streamtype; //the chosen audio by the user from the list
        switch(streamtype)
        {
            case  AudioManager.STREAM_MUSIC: // only plays in headset
                if(!AM.isWiredHeadsetOn()) {
                    equiv = AudioManager.STREAM_RING;
                    Toast.makeText(this, "Headset not plugged in, playing from Speakerphone", Toast.LENGTH_LONG).show();
                }
                break;

            case  AudioManager.STREAM_RING:  // plays in headset + speaker
                break;

            case  AudioManager.STREAM_VOICE_CALL: //earpiece
                break;

            case -1 : printd(d, "ERROR MEDIA SWITCH");
                break;
        }

         active = (on)? equiv :  AudioManager.STREAM_MUSIC;
        ILF.updateact(active);

        if(mp.isPlaying()) { //if the music is playing, keep the position, switch output, continue song
            songspot = mp.getCurrentPosition();
            mp.pause();
            mp.setAudioStreamType(active);  //correct output speaker
            play(ind, mp.getCurrentPosition());
        }
    }


    public void setButtonsSwitch()   //switch between a list of music and a list of outputs
    {
        View.OnClickListener fragSwitch = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.isChecked()) {
                    list.setChecked(false);
                    //music.setChecked(true);
                    setMusicFrag();
                }
                else
                {
                    list.setChecked(true);
                    music.setChecked(false);
                    setOPFrag();
                }
            }};

        list.setOnClickListener(fragSwitch);
        music.setOnClickListener(fragSwitch);
    }

    public void removeMusicFrag()  //remove music list
    {
        FragmentManager fragman = getFragmentManager();
        FragmentTransaction ft = fragman.beginTransaction();
        MusicListFragment MLF = (MusicListFragment) fragman.findFragmentByTag("MLF");

        if (MLF == null) //not added
        {

        } else {
            ft.remove(MLF);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);

            ft.commit();
        }
    }



    public void setOPFrag() {  //set output fragment list
        removeMusicFrag();

        ILF  = new InputListFragment(frag, active);
        FragmentManager fragman = getFragmentManager();
        FragmentTransaction ft = fragman.beginTransaction();

        if(list != null)
            ft.replace(R.id.FL, ILF, "ILF").commit();
        else
            ft.replace(R.id.input, ILF, "ILF").commit();
    }


    public void removeOPFrag()  //remove output list to make room
    {
        FragmentManager fragman = getFragmentManager();
        FragmentTransaction ft = fragman.beginTransaction();
        InputListFragment ILF = (InputListFragment) fragman.findFragmentByTag("ILF");

        if (ILF == null) //not added
        {

        } else {
            ft.remove(ILF);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            //ragListen.closeFrag();
            ft.commit();
        }
    }
    public void setMusicFrag() {  //display music
        removeOPFrag();
        MusicListFragment MLF = new MusicListFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.FL, MLF, "MLF").commit();
    }


    //adapted from listview lesson in class, witchel. and musicplayer project
    class updateSong {
        private int millis = 1000; //run every second
        private Runnable periodicTask;
        private Handler handler = new Handler();


        public updateSong(final int duration) {
            killed = false;
            //prepare
            periodicTask = new Runnable() {
                @Override
                public void run() {
                    int time =  mp.getCurrentPosition()/1000;
                    int minp, secp, minl, secl;

                    if (time <= duration) {
                        minp = time/60; //minsplayed
                        secp = time%60; //secsplayed
                        minl = (duration/60) - minp; //mins left
                        secl = (duration - (minp*60+secp)) %60; //secs left
                        showChanges(time, minp, secp, minl, secl);

                    }
                    handler.postDelayed(this, millis); //loop runnable every second
                }
            };

            //do things, like, now!
            handler.postDelayed(periodicTask, 0);
        }

        protected void showChanges(Integer... value)
        {
                songBar.setProgress(value[0]);
                String formSpent = String.format("%02d : %02d", value[1], value[2]);
                String formLeft = String.format("%02d : %02d", value[3], value[4]);
                timespent.setText(formSpent);
                timeleft.setText(formLeft);
        }

        public void kill() //stop timer! we dont need it to run and mess up our seeking
        {
            handler.removeCallbacks(periodicTask);
        }

        public void resume() //resume timer from point in song
        {
            timer = new updateSong(mp.getDuration()/1000);
        }

    }//class updateSong end bracket

}
