package com.valterc.mindcrackfront.app.main.front.tablet;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mopub.mobileads.MoPubView;
import com.valterc.WebImageView;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.main.front.MindcrackFrontListAdapter;
import com.valterc.mindcrackfront.app.main.front.MindcrackFrontListItem;
import com.valterc.mindcrackfront.app.utils.DateFormatter;
import com.valterc.views.ExpandableGridView;

import java.util.ArrayList;

/**
 * Created by Valter on 07/12/2014.
 */
public class MindcrackFrontListTabletAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MindcrackFrontSectionListItem> sections;
    private OnMindcrackItemClickListener onMindcrackItemClickListener;
    private Typeface typefaceLight;
    private Boolean useMarginInFirstItem;

    public MindcrackFrontListTabletAdapter(Context context, ArrayList<MindcrackFrontSectionListItem> sections) {
        this.context = context;
        this.sections = sections;
        typefaceLight = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
    }

    @Override
    public int getCount() {
        return sections.size();
    }

    @Override
    public Object getItem(int position) {
        return sections.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.list_front_section, null);

            ExpandableGridView expandableGridViewItems = (ExpandableGridView) convertView.findViewById(R.id.expandableGridViewItems);
            expandableGridViewItems.setExpanded(true);
            expandableGridViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MindcrackFrontListItem item = (MindcrackFrontListItem) parent.getAdapter().getItem(position);
                    OnItemClick(item);
                }
            });

        }

        MindcrackFrontSectionListItem item = (MindcrackFrontSectionListItem) getItem(position);

        TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
        ExpandableGridView expandableGridViewItems = (ExpandableGridView) convertView.findViewById(R.id.expandableGridViewItems);

        textViewTitle.setTypeface(typefaceLight);
        textViewTitle.setText(item.title);

        if (expandableGridViewItems.getAdapter() == null) {
            MindcrackFrontListAdapter adapter = new MindcrackFrontListAdapter(context, item.items, true);
            expandableGridViewItems.setAdapter(adapter);
        }

        if (position == 0) {
            ((MindcrackFrontListAdapter) expandableGridViewItems.getAdapter()).setUseMarginInFirstItem(useMarginInFirstItem);
        }

        return convertView;
    }

    private void OnItemClick(MindcrackFrontListItem item) {
        if (onMindcrackItemClickListener != null) {
            onMindcrackItemClickListener.OnItemClick(item);
        }
    }

    public void setUseMarginInFirstItem(Boolean useMarginInFirstItem) {
        this.useMarginInFirstItem = useMarginInFirstItem;
        super.notifyDataSetChanged();
    }

    public void setOnMindcrackItemClickListener(OnMindcrackItemClickListener listener){
        this.onMindcrackItemClickListener = listener;
    }

}
