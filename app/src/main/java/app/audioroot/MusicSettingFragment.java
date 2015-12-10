package app.audioroot;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicSettingFragment extends Fragment {

    public interface MSListen{
         void musicFunction(String action);
    }

    MSListen listener;
    String song;
    boolean playing = false;

    // Create a Fragment that contains the audio route data
    public MusicSettingFragment(String s, boolean isPlaying) {
            song = s;
            playing = isPlaying;
    }


    public void setSong(String s, boolean isPlaying)
    {
        this.song = s;

        TextView t = (TextView) getActivity().findViewById(R.id.textView);
        if(isPlaying)
            t.setText(song);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (MSListen) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.drawer_frag, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
       Button back = (Button)view.findViewById(R.id.back);
        Button forward = (Button)view.findViewById(R.id.forward);
        Button play = (Button)view. findViewById(R.id.play);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.musicFunction("back");
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.musicFunction("forward");
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.musicFunction("play");
            }
        });

       setSong(this.song, playing);

    }
}
