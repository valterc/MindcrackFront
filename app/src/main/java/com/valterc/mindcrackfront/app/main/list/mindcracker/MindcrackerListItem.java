package com.valterc.mindcrackfront.app.main.list.mindcracker;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

/**
 * Created by Valter on 28/05/2014.
 */
public class MindcrackerListItem {

    public static final int TYPE_AD = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_LOADING = 2;
    public static  final int TYPE_ERROR = 3;

    public SearchResult searchResult;
    public int type;

    public MindcrackerListItem(int type) {
        this.type = type;
    }

    public MindcrackerListItem(SearchResult searchResult, int type) {
        this.searchResult = searchResult;
        this.type = type;
    }
}
