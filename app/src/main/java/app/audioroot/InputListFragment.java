package app.audioroot;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class InputListFragment extends Fragment{// implements SettingsFragment.SettingsFragListen{

    public interface InputListen
    {
        void input(Bundle b);
    }
    InputListen listener;


    public InputListFragment() {

    }

    public InputListFragment(boolean frag, int active) {
        this.frag = frag;
        this.on = active;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (InputListen) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        //set things
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.inputlist, container, false);

    }

    boolean frag;
    int on;
    public final static String EARPIECE = "Earpiece";
    public static final String EXTSPKR = "Ext. Speaker";
    public static final String HP = "Headphones";

    ListView inputs;
    DynamicAdapter outputs;
    SwipeDetector swipeDetector;

    public void updatefrag(boolean fg)
    {
        this.frag = fg;
    }
    public void updateact(int active)
    {
        this.on = active;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //this.frag = false;

        swipeDetector = new SwipeDetector();
        inputs = (ListView)getActivity().findViewById(R.id.listView3);
        outputs = new DynamicAdapter(getActivity().getApplicationContext());
        // inputs
        outputs.addItem(new AppNode(EARPIECE));//create list entries
        outputs.addItem(new AppNode(EXTSPKR));
        outputs.addItem(new AppNode(HP));

        inputs.setAdapter(outputs);

        inputs.setOnTouchListener(swipeDetector);
        inputs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (swipeDetector.swipeDetected()) {

                    if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        //quickswipe
                    }
                    if (swipeDetector.getAction() == SwipeDetector.Action.LR) {  //kill the mediaplayer side fragment
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

                            //open settings/things
                            AppNode rr = outputs.getItem(i);
                            if (!frag) {
                                SettingsFragment SF = SettingsFragment.newInstance(rr.getAppName(), on, i);
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.add(R.id.mainFL, SF, "add").commit();
                                frag = true;
                            }
                        }
                    }
                }) ;
    }
}


