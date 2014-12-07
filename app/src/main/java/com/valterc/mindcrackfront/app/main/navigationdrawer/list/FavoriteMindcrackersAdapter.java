package com.valterc.mindcrackfront.app.main.navigationdrawer.list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.data.DataManager;
import com.valterc.mindcrackfront.app.data.Mindcracker;
import com.vcutils.bindable.BindableListener;
import com.vcutils.views.PixelImageView;

/**
 * Created by Valter on 18/05/2014.
 */
public class FavoriteMindcrackersAdapter extends BaseAdapter {

    private Context context;
    private DataManager dataManager;

    public FavoriteMindcrackersAdapter(Context context){
        this.context = context;
        this.dataManager = MindcrackFrontApplication.getDataManager();
        this.dataManager.RegisterBinder(DataManager.BIND_FAVORITE_MINDCRACKERS, new BindableListener() {

            @Override
            public void onChange(String property, Object o, Object extra) {
                notifyDataSetChanged();
            }

        });
    }

    @Override
    public int getCount() {
        return this.dataManager.getFavoriteMindcrackers().size();
    }

    @Override
    public Object getItem(int i) {
        return this.dataManager.getFavoriteMindcrackers().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null){
            view = View.inflate(context, R.layout.list_navigation_mindcracker, null);

            FavoriteMindcrackerViewHolder viewHolder = new FavoriteMindcrackerViewHolder();
            viewHolder.imageViewLogo = (PixelImageView) view.findViewById(R.id.imageViewLogo);
            viewHolder.textViewName = (TextView) view.findViewById(R.id.textViewName);
            viewHolder.linearLayoutUnseenVideos = (LinearLayout) view.findViewById(R.id.linearLayoutUnseenVideos);
            viewHolder.textViewUnseenVideosCount = (TextView) view.findViewById(R.id.textViewUnseenVideosCount);

            view.setTag(viewHolder);
        }

        FavoriteMindcrackerViewHolder viewHolder = (FavoriteMindcrackerViewHolder) view.getTag();
        Mindcracker m = (Mindcracker) getItem(i);

        viewHolder.textViewName.setText(m.getName());


        return view;

    }
}
