package app.audioroot;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.TreeMap;


public class SettingsFragment extends Fragment {

    //
    public interface SettingsFragListen {
         void onResult(Bundle data);
         void closeFrag();
    }




    // Create a Fragment that contains the audio route data
    public static SettingsFragment newInstance(String name, int on, int index) {
        SettingsFragment f = new SettingsFragment();
        // Bundles are used to pass data using a key "index" and a value
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("on", on);
        args.putInt("ind", index);
        // Assign key value to the fragment
        f.setArguments(args);
        return f;
    }


    public final static String EARPIECE = "Earpiece";
    public static final String EXTSPKR = "Ext. Speaker";
    public static final String HP = "Headphones";
    public static  final String BT = "Bluetooth";

    TextView title;
    Switch onoff;
    AudioManager AM;
    SeekBar volume;
    int VOLUME_MAX = 10;
    int LOW = Color.GREEN;
    int MED = Color.YELLOW;
    int HI = Color.RED;
    SettingsFragListen fragListen;

    TreeMap<String, Integer> inStream;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragListen = (SettingsFragListen) activity;
            //   mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }


        //set things
    }


    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.open_input, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
         MainActivity a = (MainActivity)getActivity();
        AM =  (AudioManager) a.getSystemService(Context.AUDIO_SERVICE);
        inStream = new TreeMap<String, Integer>();

        inStream.put(EARPIECE, AudioManager.STREAM_VOICE_CALL);
        inStream.put(EXTSPKR, AudioManager.STREAM_RING);
        inStream.put(HP, AudioManager.STREAM_MUSIC);

        final String inputName = getArguments().getString("name");
        //set the basics
        title = (TextView) getActivity().findViewById(R.id.title);
        onoff = (Switch) getActivity().findViewById(R.id.switch1);
        title.setText(inputName);

        //remember the setting from before
        if(inStream.get(inputName) == getArguments().getInt("on") )
                    onoff.setChecked(true);


        //back button
        Button b = (Button) getActivity().findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragman = getFragmentManager();
                FragmentTransaction ft = fragman.beginTransaction();
                SettingsFragment SF = (SettingsFragment) fragman.findFragmentByTag("add");

                if (SF == null) //not added
                {
                   // Toast.makeText(getActivity(), "no fragment!", Toast.LENGTH_LONG).show();
                    //break;
                } else {
                    ft.remove(SF);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    fragListen.closeFrag();
                    ft.commit();
                }
               // Toast.makeText(getActivity(), SF.title.getText().toString(), Toast.LENGTH_LONG).show();

            }
        });

        //onoff
        onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

              //  Toast.makeText(getActivity(), "changed" + getArguments().getString("name") + " " + getArguments().getInt("ind") , Toast.LENGTH_SHORT).show();
                try {
                    AUDIO_STUFF(getArguments().getString("name"), isChecked, getArguments().getInt("ind"));
                } catch (Exception e) {
                    Log.d("ERROR", "what");
                    e.getStackTrace();
                }
            }
        });

        //volume controls
        volume = (SeekBar) getActivity().findViewById(R.id.seekBar2);

        volume.setMax(AM.getStreamMaxVolume(inStream.get(inputName))); //volumebar correspond to mediavolume

        //volume.setProgress(AM.getStreamVolume(inStream.get(inputName))/volume.getMax());

        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser)
                    {
                        double ratio = progress / (double)seekBar.getMax();
                        if(ratio <= .33)
                        {
                            seekBar.setBackgroundColor(LOW);
                        }
                        else if(ratio > .33 && ratio <= .66)
                        {
                            seekBar.setBackgroundColor(MED);
                        }
                        else if(ratio > .66)
                        {
                            seekBar.setBackgroundColor(HI);
                        }
                        Log.d("volume", "max: " + AM.getStreamMaxVolume(inStream.get(inputName)) + " prog: " + progress);
                        AM.setStreamVolume(
                                inStream.get(inputName),
                                progress,
                                AudioManager.FLAG_PLAY_SOUND);
                    }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void AUDIO_STUFF(String appName, boolean isChecked, int index) {
        //activate
        Bundle b = new Bundle();
        b.putBoolean("on", isChecked);
        b.putString("name", appName);
        b.putInt("ind", index);
        b.putInt("audio", inStream.get(appName));
        fragListen.onResult(b);

    }
}