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


import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SettingsFragment.SettingsFragListen, MusicSettingFragment.MSListen, MusicListFragment.MusicListen, InputListFragment.InputListen{


    boolean killed = false;
    int[] songs = getSongPaths();
    int ind = 0;
    String[] musicList = {"Homos In Space", "No Photograph", "C thing"};

    int active;

    /*
        EARPIECE;
        EXTSPKR;
        HP;
        BT;
      */
    updateSong timer;
    public static HashMap<String, Integer> streams;
    int songspot;
    MediaPlayer mp;
    AudioManager AM;
    SwipeDetector swipeDetector ;
    DynamicAdapter outputs;
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

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.combined_land);
            music = null;
            list = null;
            removeOPFrag();
        } else {
            setContentView(R.layout.indivport);
            music = (RadioButton) findViewById(R.id.Music);
            list = (RadioButton) findViewById(R.id.Output);
            ILF = new InputListFragment(false, active);
            setOPFrag();
        }





        songspot = 0;
        swipeDetector = new SwipeDetector();

        mp = new MediaPlayer();//initialize
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.seekTo(songspot);
                //  Toast.makeText(MainActivity.this, Integer.toString(songspot), Toast.LENGTH_SHORT).show();
                if (!mp.isPlaying()) {
                    mp.start();
                    setInfo();
                }
            }
        });
        AM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

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
                      //  Toast.makeText(MainActivity.this, "no fragment! DRAWER", Toast.LENGTH_LONG).show();
                        //break;
                    } else {
                        ft.remove(MS);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                        ft.commit();
                    }
                }
                else
                {
                   // Toast.makeText(MainActivity.this, "openDRAWER", Toast.LENGTH_LONG).show();

                    MusicSettingFragment MS =  (MusicSettingFragment) f.findFragmentByTag("MS");
                    FragmentTransaction ft = f.beginTransaction();
                    if(MS == null)
                    {
                        MS = new MusicSettingFragment(musicList[ind], mp.isPlaying());
                        ft.add(R.id.frameside, MS, "MS").commit();

                    }
                    else
                    {
                        MS.setSong(musicList[ind], mp.isPlaying());
                        ft.replace(R.id.frameside, MS, "MS").commit();
                    }
                    player.setChecked(false);
                }
            }

        });

    }

    public void playSong(int index)
    {

        if(timer != null)
            timer.kill(); //stop checking time, new song needed

        if (mp != null) {
            if(mp.isPlaying())
                mp.stop(); //stop music

            mp.reset();
        }

        try {
            mp.setAudioStreamType(active);
            String x = "android.resource://" + getPackageName() + "/" + songs[ind];
            Log.d("PACKAGENAME", getPackageName());
            mp.setDataSource(getApplicationContext(),
                    Uri.parse(x));
            mp.prepareAsync();
        }catch(Exception e)
        {
            Log.d("ERROR", "shit1");
            e.getStackTrace();
        }
    }


    //MusicListen
    public void play(int ind)
    {
        songspot = 0;
        this.ind = ind; //set index bc chosen song in frag
        playSong(ind);
    }

    //MsListen
    @Override
   public void musicFunction(String action)
    {
        if(action.compareTo("back") == 0)
        {
            songspot = 0;
            ind = (ind - 1 == -1) ? songs.length - 1 : ind - 1;
            playSong(ind);
        }
        if(action.compareTo("forward") == 0)
        {
            songspot = 0;
            ind = (ind + 1 == songs.length) ? 0 : ind + 1;
            playSong(ind);
        }
        if(action.compareTo("play") == 0)
        {
            if(mp.isPlaying())
                mp.pause();
            else
                mp.start();
        }
}

    public void setInfo() {

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

        maintainAudio(data.getInt("audio"));
    }

    public void closeFrag()
    {
        this.frag = false;
        ILF.updatefrag(this.frag);
    }

    public void maintainAudio(int streamtype) {
       // mp.pause();
        int equiv = streamtype;
        switch(streamtype)
        {

            case  AudioManager.STREAM_MUSIC:

                if(!AM.isWiredHeadsetOn()) {
                    equiv = AudioManager.STREAM_RING;
                    Toast.makeText(this, "Headset not plugged in, playing from Speakerphone", Toast.LENGTH_LONG).show();
                }
                break; // only plays in headset

            case  AudioManager.STREAM_RING:
                break; // plays in headset + speaker

            case  AudioManager.STREAM_VOICE_CALL: //earpiece
                break;

            case -1 : printd(d, "ERROR MEDIA SWITCH");
                break;
        }

        active = equiv;
        ILF.updateact(active);

        if(mp.isPlaying()) {
            songspot = mp.getCurrentPosition();
            //mp.pause();
            playSong(ind);
        }
    }


    public void setButtonsSwitch()
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

    public void removeMusicFrag()
    {
        FragmentManager fragman = getFragmentManager();
        FragmentTransaction ft = fragman.beginTransaction();
        MusicListFragment MLF = (MusicListFragment) fragman.findFragmentByTag("MLF");

        if (MLF == null) //not added
        {
            //Toast.makeText(this, "no fragment mlf!", Toast.LENGTH_LONG).show();
            //break;
        } else {
            ft.remove(MLF);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            //ragListen.closeFrag();
            ft.commit();
        }
    }



    public void setOPFrag() {
        removeMusicFrag();
        //music fragment overlay;
        ILF  = new InputListFragment(frag, active);
        FragmentManager fragman = getFragmentManager();
        FragmentTransaction ft = fragman.beginTransaction();

        if(list != null)
            ft.replace(R.id.FL, ILF, "ILF").commit();
        else
            ft.replace(R.id.input, ILF, "ILF").commit();
    }


    public void removeOPFrag()
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
    public void setMusicFrag()
    {
        removeOPFrag();
        //music fragment overlay;
        MusicListFragment MLF = new MusicListFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
       // ft.setTransition(Frag)
        ft.replace(R.id.FL, MLF, "MLF").commit();
        //frag = true;

      //  Toast.makeText(this, "SETTING MUSIC FRAG", Toast.LENGTH_LONG).show();
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
