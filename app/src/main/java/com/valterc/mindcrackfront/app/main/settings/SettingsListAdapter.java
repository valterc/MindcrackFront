package com.valterc.mindcrackfront.app.main.settings;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.valterc.mindcrackfront.app.R;

import java.util.ArrayList;

/**
 * Created by Valter on 23/12/2014.
 */
public class SettingsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SettingsListItem<?>> items;
    private Typeface typefaceLight;

    public SettingsListAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
        typefaceLight = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public SettingsListItem<?> getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return SettingsListItem.SETTINGS_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SettingsListItem<?> item = getItem(position);

        switch (item.getType()) {
            case SettingsListItem.TYPE_BUTTON:
                return getViewButton(position, item, convertView);
            case SettingsListItem.TYPE_SWITCH:
                return getViewSwitch(position, (SettingsListItem<Boolean>) item, convertView);
        }

        return convertView;
    }

    private View getViewButton(int position, final SettingsListItem<?> item, View convertView) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.list_settings_switch, null);

            TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewSettingTitle);
            TextView textViewSubTitle = (TextView) convertView.findViewById(R.id.textViewSettingSubtitle);

            textViewTitle.setTypeface(typefaceLight);
            textViewSubTitle.setTypeface(typefaceLight);
        }

        RelativeLayout relativeLayoutSetting = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutSetting);
        if (item.getClickable()) {
            relativeLayoutSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.OnClick();
                }
            });
        } else {
            relativeLayoutSetting.setOnClickListener(null);
            relativeLayoutSetting.setClickable(false);
        }

        TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewSettingTitle);
        TextView textViewSubTitle = (TextView) convertView.findViewById(R.id.textViewSettingSubtitle);

        textViewTitle.setText(item.getTitle());
        textViewSubTitle.setText(item.getSubTitle());

        relativeLayoutSetting.setEnabled(item.getEnabled());
        textViewTitle.setEnabled(item.getEnabled());
        textViewSubTitle.setEnabled(item.getEnabled());

        return convertView;
    }

    private View getViewSwitch(int position, final SettingsListItem<Boolean> item, View convertView) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.list_settings_switch, null);

            TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewSettingTitle);
            TextView textViewSubTitle = (TextView) convertView.findViewById(R.id.textViewSettingSubtitle);
            Switch switchSetting = (Switch) convertView.findViewById(R.id.switchSetting);

            textViewTitle.setTypeface(typefaceLight);
            textViewSubTitle.setTypeface(typefaceLight);
            switchSetting.setSwitchTypeface(typefaceLight);
            switchSetting.setVisibility(View.VISIBLE);
        }

        RelativeLayout relativeLayoutSetting = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutSetting);
        if (item.getClickable()) {
            relativeLayoutSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.OnClick();
                }
            });
        } else {
            relativeLayoutSetting.setOnClickListener(null);
            relativeLayoutSetting.setClickable(false);
        }

        Switch switchSetting = (Switch) convertView.findViewById(R.id.switchSetting);
        switchSetting.setChecked(item.getValue());
        switchSetting.setSwitchTypeface(typefaceLight);
        switchSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setValue(isChecked);
                item.OnValueChanged(isChecked);
            }
        });

        TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewSettingTitle);
        TextView textViewSubTitle = (TextView) convertView.findViewById(R.id.textViewSettingSubtitle);

        textViewTitle.setText(item.getTitle());
        textViewSubTitle.setText(item.getSubTitle());

        relativeLayoutSetting.setEnabled(item.getEnabled());
        switchSetting.setEnabled(item.getEnabled());
        textViewTitle.setEnabled(item.getEnabled());
        textViewSubTitle.setEnabled(item.getEnabled());

        return convertView;
    }

    public void addItem(SettingsListItem<?> item) {
        this.items.add(item);
    }

}
