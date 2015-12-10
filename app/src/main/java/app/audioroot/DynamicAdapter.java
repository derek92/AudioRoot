package app.audioroot;

/**
 * Created by Derek on 10/7/2015.
 */

import android.widget.BaseAdapter;
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;
import java.util.ArrayList;



public class DynamicAdapter extends BaseAdapter {
    private ArrayList<AppNode> rData = new ArrayList<AppNode>();
    private LayoutInflater mInflater;

    public DynamicAdapter(Context mContext) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final AppNode rr) {
        rData.add(rr);
        notifyDataSetChanged();
    }
    public void removeItem(int position) {
        rData.remove(position);
        notifyDataSetChanged();
    }

    public void activate(int index, boolean b)
    {
        rData.get(index).Activate(b);
    }

    @Override
    public long getItemId(int position) {
        return rData.get(position).hashCode();
    }
    @Override
    public int getCount() {
        return rData.size();
    }

    @Override
    public AppNode getItem(int position) {
        return rData.get(position);
    }

    protected LinearLayout makeView(LinearLayout theView, int position, ViewGroup parent) {
        // We retrieve the text from the array
        AppNode rec = rData.get(position);
        String input = rec.getAppName();

        // Get the TextView we want to edit
                 TextView tv = (TextView) theView.findViewById(R.id.name);

        tv.setText(input);
        // Get the ImageView in the layout
                 ImageView theImageView = (ImageView)theView.findViewById(R.id.input);
        //theImageView.setImageBitmap(BitmapCache.defaultThumbnailBitmap);

        return theView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout view;
        if (convertView == null) {
            view = (LinearLayout) mInflater.inflate(R.layout.list_row, parent, false);
        } else {
            view = (LinearLayout) convertView;
        }
        view = makeView(view, position, parent);
        return view;
    }

}
