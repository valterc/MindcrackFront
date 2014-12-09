package com.valterc.mindcrackfront.app.main.front;

import com.google.api.services.youtube.model.PlaylistItem;

/**
 * Created by Valter on 07/12/2014.
 */
public class MindcrackFrontListItem {

    public static final int TYPE_TITLE = 0;
    public static final int TYPE_VIDEO = 1;

    public int type;
    public String title;
    public PlaylistItem playlistItem;

    public MindcrackFrontListItem(String title){
        this.type = TYPE_TITLE;
        this.title = title;
    }

    public MindcrackFrontListItem(PlaylistItem playlistItem){
        this.type = TYPE_VIDEO;
        this.playlistItem = playlistItem;
    }


}
