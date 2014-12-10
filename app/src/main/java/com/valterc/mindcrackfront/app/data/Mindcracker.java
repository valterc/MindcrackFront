package com.valterc.mindcrackfront.app.data;

import com.valterc.mindcrackfront.app.R;

import java.util.Date;

/**
 * Created by Valter on 18/05/2014.
 */
public class Mindcracker {

    private String id;
    private String name;
    private String youtubeName;
    private String youtubeId;
    private String youtubePlaylistId;
    private String twitchId;
    private Boolean showTitleOnList;
    private Boolean notificationsEnabled;
    private int unseenVideoCount;
    private long hits;
    private String lastVideoId;
    private Date lastVideoDate;
    private Boolean isDirty;


    public Mindcracker(String id, String name, String youtubeName, String youtubeId, String youtubePlaylistId, String twitchId, Boolean showTitleOnList, Boolean notificationsEnabled, int unseenVideoCount, long hits, String lastVideoId, Date lastVideoDate) {
        this.id = id;
        this.name = name;
        this.youtubeName = youtubeName;
        this.youtubeId = youtubeId;
        this.youtubePlaylistId = youtubePlaylistId;
        this.twitchId = twitchId;
        this.showTitleOnList = showTitleOnList;
        this.notificationsEnabled = notificationsEnabled;
        this.unseenVideoCount = unseenVideoCount;
        this.hits = hits;
        this.lastVideoId = lastVideoId;
        this.lastVideoDate = lastVideoDate;
        this.isDirty = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYoutubeName() {
        return youtubeName;
    }

    public void setYoutubeName(String youtubeName) {
        this.youtubeName = youtubeName;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getYoutubePlaylistId() {
        return youtubePlaylistId;
    }

    public void setYoutubePlaylistId(String youtubePlaylistId) {
        this.youtubePlaylistId = youtubePlaylistId;
    }

    public String getTwitchId() {
        return twitchId;
    }

    public void setTwitchId(String twitchId) {
        this.twitchId = twitchId;
    }

    public Boolean getShowTitleOnList() {
        return showTitleOnList;
    }

    public void setShowTitleOnList(Boolean showTitleOnList) {
        this.showTitleOnList = showTitleOnList;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public int getUnseenVideoCount() {
        return unseenVideoCount;
    }

    public void setUnseenVideoCount(int unseenVideoCount) {
        this.unseenVideoCount = unseenVideoCount;
    }

    public long getHits() {
        return hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public String getLastVideoId() {
        return lastVideoId;
    }

    public void setLastVideoId(String lastVideoId) {
        this.lastVideoId = lastVideoId;
    }

    public Date getLastVideoDate() {
        return lastVideoDate;
    }

    public void setLastVideoDate(Date lastVideoDate) {
        this.lastVideoDate = lastVideoDate;
    }

    public Boolean getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(Boolean isDirty) {
        this.isDirty = isDirty;
    }

    public int getImageResourceId(){

        switch (this.id){
            case "adlingtont": return R.drawable.adlingtont;
            case "anderzel": return R.drawable.anderzel;
            case "arkas": return R.drawable.arkas;
            case "Avidya": return R.drawable.avidya;
            case "BdoubleO100": return R.drawable.bdoubleo100;
            case "BlameTC": return R.drawable.blametc;
            case "docm77": return R.drawable.docm77;
            case "Etho": return R.drawable.etho;
            case "Generikb": return R.drawable.generikb;
            case "Guude": return R.drawable.guude;
            case "SethBling": return R.drawable.sethbling;
            case "jsano19": return R.drawable.jsano19;
            case "kurtmac": return R.drawable.kurtmac;
            case "mcgamer": return R.drawable.mcgamer;
            case "Mhykol": return R.drawable.mhykol;
            case "Millbee": return R.drawable.millbee;
            case "Nebris": return R.drawable.nebris;
            case "pakratt0013": return R.drawable.pakratt0013;
            case "PaulSoaresJr": return R.drawable.paulsoaresjr;
            case "pauseunpause": return R.drawable.pauseunpause;
            case "Pyrao": return R.drawable.pyrao;
            case "thejims": return R.drawable.thejims;
            case "Vechs": return R.drawable.vechs;
            case "VintageBeef": return R.drawable.vintagebeef;
            case "W92Baj": return R.drawable.w92baj;
            case "Zisteau": return R.drawable.zisteau;
            case "Aureylian": return R.drawable.aureylian;
            case "Coestar": return R.drawable.coestar;
            case "Sevadus": return R.drawable.sevadus;
            case "OMGChad": return R.drawable.omgchad;
            case "MindCrackNetwork": return R.drawable.mindcrack_logo;
        }

        return -1;
    }

}
