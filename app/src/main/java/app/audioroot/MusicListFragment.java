package app.audioroot;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicListFragment extends Fragment {

public interface MusicListen
{
    void play(int index, int place);
}
    MusicListen listener;

    public MusicListFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
             listener = (MusicListen) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }


        //set things
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.musiclist, container, false);

    }

    ListView songs;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        songs = (ListView)view.findViewById(R.id.listView2);
        String[] musicList = getResources().getStringArray(R.array.song_names);  //songs!

        ArrayAdapter LA = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, musicList);
        songs.setAdapter(LA);

        final SwipeDetector swipeDetector = new SwipeDetector();
        songs.setOnTouchListener(swipeDetector);
        songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (swipeDetector.swipeDetected()) {

                    if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        //quickswipe
                    }
                    if (swipeDetector.getAction() == SwipeDetector.Action.LR) { // remove media player fragment
                        FragmentManager fragman = getFragmentManager();
                        MusicSettingFragment MS = (MusicSettingFragment) fragman.findFragmentByTag("MS");
                        FragmentTransaction ft = fragman.beginTransaction();
                        if (MS == null) //not added
                        {
                        } else {
                            ft.remove(MS);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                            ((MainActivity) listener).closeFrag();
                            ft.commit();
                        }
                    }
                }
                else{
                    listener.play(i,0); //play the selected song in the music list, send to main
                }
            }
        }) ;
    }


}
