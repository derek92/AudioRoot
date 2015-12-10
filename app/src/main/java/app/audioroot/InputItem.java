package app.audioroot;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * Created by Derek on 10/8/2015.
 */
public class InputItem extends AppCompatActivity{
    public interface Callback {
        public void maintainAudio(int streamtype);
    }

    TextView title;
    Switch onoff;
    AudioManager AM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_input);


        title = (TextView) findViewById(R.id.title);
        onoff = (Switch) findViewById(R.id.switch1);
        AM = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        String stitle =getIntent().getExtras().getString("name"); //value sent in from main
        Boolean on = getIntent().getExtras().getBoolean("onoff");


        title.setText(stitle);

        onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Toast.makeText(InputItem.this,"changed", Toast.LENGTH_SHORT).show();
                try {
                    AUDIO_STUFF(title.getText().toString(), isChecked);
                } catch (Exception e) {
                    Log.d("ERROR", "what");
                    e.getStackTrace();
                }
            }
        });
    }


    public void AUDIO_STUFF(String appName, boolean isChecked) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(isChecked)
        {
            AM.setWiredHeadsetOn(false);
            AM.setSpeakerphoneOn(true);
            // AM.setSpeakerphoneOn(true);
            //AM.setStreamVolume(AudioManager.STREAM_MUSIC,AM.getStreamVolume(AudioManager.STREAM_MUSIC) - 1, AudioManager.FLAG_PLAY_SOUND);

            //   AM.setStreamMute(AudioManager.STREAM_MUSIC, true);
            // AM.setSpeakerphoneOn(false);
        }
        else
        {
            AM.setWiredHeadsetOn(true);
            AM.setSpeakerphoneOn(false);
            // AudioManager.ACTION_HEADSET_PLUG
            //AM.set
        }
        //.maintainAudio(AudioManager.STREAM_RING);

        //  AudioManager.STREAM_RING

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
        if (id == R.id.home) {
            goback();
            super.onBackPressed();
            return true;
        }
        if(id == R.id.up)
        {

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    //adapted from prof Witchel's slides on intents and activites
    public void goback()
    {
        //go back to activitymain
        Intent goingBack = new Intent();

        setResult(RESULT_OK, goingBack); //of course it is ok, just buttons
        // Close this Activity
        finish();
    }
}
