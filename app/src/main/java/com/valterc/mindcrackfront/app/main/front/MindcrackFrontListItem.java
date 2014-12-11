package com.valterc.mindcrackfront.app.main.front;

import com.google.api.services.youtube.model.PlaylistItem;
import com.valterc.mindcrackfront.app.youtube.GDataYoutubeVideo;

/**
 * Created by Valter on 07/12/2014.
 */
public class MindcrackFrontListItem {

    public static final int TYPE_TITLE = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_AD = 2;

    public int type;
    public String title;
    public GDataYoutubeVideo video;

    public MindcrackFrontListItem(int type){
        this.type = type;
    }


    public MindcrackFrontListItem(String title){
        this.type = TYPE_TITLE;
        this.title = title;
    }

    public MindcrackFrontListItem(GDataYoutubeVideo video){
        this.type = TYPE_VIDEO;
        this.video = video;
    }


}
