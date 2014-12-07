package com.valterc.mindcrackfront.app.main.navigationdrawer.list;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.data.DataManager;
import com.valterc.mindcrackfront.app.data.Mindcracker;
import com.vcutils.bindable.BindableListener;
import com.vcutils.views.PixelImageView;

import java.util.ArrayList;

/**
 * Created by Valter on 23/05/2014.
 */
public class NavigationDrawerAdapter extends BaseAdapter {

    private static final int TYPE_COUNT = 4;

    private Context context;
    private DataManager dataManager;
    private ArrayList<NavigationDrawerListItem> items;
    private int selectedPosition;

    private BindableListener favoriteMindcrackersListener;
    private BindableListener mindcrackersListener;

    public NavigationDrawerAdapter(Context context){
        this.context = context;
        this.dataManager = MindcrackFrontApplication.getDataManager();

        buildListItems();

        selectedPosition = -1;

        this.favoriteMindcrackersListener = new BindableListener() {

            @Override
            public void onChange(String property, Object o, Object extra) {
                selectedPosition = -1;
                buildListItems();
                notifyDataSetChanged();
            }

        };

        this.mindcrackersListener = new BindableListener() {

            @Override
            public void onChange(String property, Object o, Object extra) {
                if (extra != null){
                    Integer inte = (Integer) extra;
                    if (inte == 1)
                        selectedPosition++;
                    else
                        selectedPosition--;
                }
                buildListItems();
                notifyDataSetChanged();
            }

        };

        this.dataManager.RegisterBinder(DataManager.BIND_FAVORITE_MINDCRACKERS, favoriteMindcrackersListener);
        this.dataManager.RegisterBinder(DataManager.BIND_MINDCRACKERS, mindcrackersListener);

    }

    private void buildListItems() {
        items = new ArrayList<>();
        items.add(new NavigationDrawerListItem(NavigationDrawerListItem.TYPE_TITLE, "Favorites"));

        if (this.dataManager.getFavoriteMindcrackers() == null || this.dataManager.getFavoriteMindcrackers().isEmpty()){
            items.add(new NavigationDrawerListItem(NavigationDrawerListItem.TYPE_EMPTY));
        } else {
            for (int i = 0; i < this.dataManager.getFavoriteMindcrackers().size(); i++) {
                Mindcracker mindcracker = this.dataManager.getFavoriteMindcrackers().get(i);
                items.add(new NavigationDrawerListItem(NavigationDrawerListItem.TYPE_FAVORITE, mindcracker));
            }
        }

        items.add(new NavigationDrawerListItem(NavigationDrawerListItem.TYPE_TITLE, "Mindcrackers"));

        for (int i = 0; i < this.dataManager.getMindcrackers().size(); i++) {
            Mindcracker mindcracker = this.dataManager.getMindcrackers().get(i);
            items.add(new NavigationDrawerListItem(NavigationDrawerListItem.TYPE_NORMAL, mindcracker));
        }

        items.add(new NavigationDrawerListItem(NavigationDrawerListItem.TYPE_TITLE, "Settings"));

    }

    public void setSelection(int position){
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == NavigationDrawerListItem.TYPE_FAVORITE || getItemViewType(position) == NavigationDrawerListItem.TYPE_NORMAL;
    }

    @Override
    public int getItemViewType(int position) {
        NavigationDrawerListItem item = (NavigationDrawerListItem) getItem(position);
        return item.type;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        NavigationDrawerListItem item = (NavigationDrawerListItem) getItem(i);
        switch (item.type)  {
            case 0: return getTitleView(i, view, viewGroup);
            case 1: return getEmptyView(i, view, viewGroup);
            case 2: return getFavoriteView(i, view, viewGroup);
            case 3: return getNormalView(i, view, viewGroup);
        }

        return null;
    }


    private View getTitleView(int i, View view, ViewGroup viewGroup){
        if (view == null) {
            view = View.inflate(context, R.layout.list_navigation_title, null);
        }

        NavigationDrawerListItem m = (NavigationDrawerListItem) getItem(i);

        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewTitle.setText(m.title);

        return view;
    }

    private View getEmptyView(int i, View view, ViewGroup viewGroup){

        if (view == null) {
            view = View.inflate(context, R.layout.list_navigation_nofavorites, null);
        }

        return view;
    }

    private View getFavoriteView(int i, View view, ViewGroup viewGroup){

        if (view == null){
            view = View.inflate(context, R.layout.list_navigation_mindcracker, null);

            FavoriteMindcrackerViewHolder viewHolder = new FavoriteMindcrackerViewHolder();
            viewHolder.relativeLayoutBack = (RelativeLayout) view.findViewById(R.id.relativeLayoutBack);
            viewHolder.imageViewLogo = (PixelImageView) view.findViewById(R.id.imageViewLogo);
            viewHolder.textViewName = (TextView) view.findViewById(R.id.textViewName);
            viewHolder.linearLayoutUnseenVideos = (LinearLayout) view.findViewById(R.id.linearLayoutUnseenVideos);
            viewHolder.textViewUnseenVideosCount = (TextView) view.findViewById(R.id.textViewUnseenVideosCount);

            view.setTag(viewHolder);
        }

        FavoriteMindcrackerViewHolder viewHolder = (FavoriteMindcrackerViewHolder) view.getTag();
        NavigationDrawerListItem m = (NavigationDrawerListItem) getItem(i);

        viewHolder.textViewName.setText(m.mindcracker.getName());
        viewHolder.imageViewLogo.setImageResource(m.mindcracker.getImageResourceId());
        viewHolder.linearLayoutUnseenVideos.setVisibility(m.mindcracker.getUnseenVideoCount() != 0 ? View.VISIBLE : View.INVISIBLE);
        viewHolder.textViewUnseenVideosCount.setText("" + m.mindcracker.getUnseenVideoCount());

        if (selectedPosition == i) {
            viewHolder.relativeLayoutBack.setBackgroundColor(0xffdadada);
        } else {
            viewHolder.relativeLayoutBack.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    private View getNormalView(int i, View view, ViewGroup viewGroup){

        if (view == null){
            view = View.inflate(context, R.layout.list_navigation_mindcracker, null);

            MindcrackersViewHolder viewHolder = new MindcrackersViewHolder();
            viewHolder.relativeLayoutBack = (RelativeLayout) view.findViewById(R.id.relativeLayoutBack);
            viewHolder.imageViewLogo = (PixelImageView) view.findViewById(R.id.imageViewLogo);
            viewHolder.textViewName = (TextView) view.findViewById(R.id.textViewName);

            view.setTag(viewHolder);
        }

        MindcrackersViewHolder viewHolder = (MindcrackersViewHolder) view.getTag();
        NavigationDrawerListItem m = (NavigationDrawerListItem) getItem(i);

        viewHolder.textViewName.setText(m.mindcracker.getName());
        viewHolder.imageViewLogo.setImageResource(m.mindcracker.getImageResourceId());

        if (selectedPosition == i) {
            viewHolder.relativeLayoutBack.setBackgroundColor(0xffdadada);
        } else {
            viewHolder.relativeLayoutBack.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }


}
