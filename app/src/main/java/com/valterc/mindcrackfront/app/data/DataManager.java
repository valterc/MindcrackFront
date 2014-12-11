package com.valterc.mindcrackfront.app.data;

import android.content.Context;
import android.util.Log;

import com.valterc.mindcrackfront.app.data.storage.DataSource;
import com.vcutils.bindable.BindableBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Valter on 18/05/2014.
 */
public class DataManager extends BindableBase {

    public static final String BIND_FAVORITE_MINDCRACKERS = "favoriteMindcrackers";
    public static final String BIND_MINDCRACKERS = "mindcrackers";

    private DataSource dataSource;
    private ArrayList<Mindcracker> mindcrackers;
    private ArrayList<Mindcracker> favoriteMindcrackers;

    public DataManager(Context context) {
        dataSource = new DataSource(context);

        mindcrackers = dataSource.getMindcrackers();
        favoriteMindcrackers = dataSource.getFavoriteMindcrackers();
    }

    public void dispose() {
        dataSource.updateMindcrackers(getMindcrackers());
        dataSource.updateFavoriteMindcrackers(getFavoriteMindcrackers());
        dataSource.dispose();
    }


    public ArrayList<Mindcracker> getMindcrackers() {
        return mindcrackers;
    }

    public ArrayList<Mindcracker> getFavoriteMindcrackers() {
        return favoriteMindcrackers;
    }

    public Mindcracker getMindcracker(String mindcrackerId) {

        for (int i = 0; i < mindcrackers.size(); i++) {
            Mindcracker mindcracker = mindcrackers.get(i);
            if (mindcracker.getId().equals(mindcrackerId)) {
                return mindcracker;
            }
        }

        return null;
    }

    public Mindcracker getMindcrackerYoutubeId(String youtubeId) {
        for (int i = 0; i < mindcrackers.size(); i++) {
            Mindcracker mindcracker = mindcrackers.get(i);
            if (mindcracker.getYoutubeId() != null && mindcracker.getYoutubeId().equals(youtubeId)) {
                return mindcracker;
            }
        }

        return null;
    }

    public Mindcracker getMindcrackerTwitchId(String twitchId) {
        for (int i = 0; i < mindcrackers.size(); i++) {
            Mindcracker mindcracker = mindcrackers.get(i);
            if (mindcracker.getTwitchId() != null && mindcracker.getTwitchId().toLowerCase().equals(twitchId.toLowerCase())) {
                return mindcracker;
            }
        }

        return null;
    }

    public ArrayList<String> getMindcrackersYoutubeId() {
        ArrayList<String> youtubeIds = new ArrayList<>(mindcrackers.size());

        for (Mindcracker m : mindcrackers) {
            youtubeIds.add(m.getYoutubeId());
        }

        return youtubeIds;
    }

    public ArrayList<String> getRecommendedMindcrackersYoutubeId() {

        ArrayList<String> mindcrackersYoutubeIds = new ArrayList<>();

        for (Mindcracker mindcracker : favoriteMindcrackers) {
            mindcrackersYoutubeIds.add(mindcracker.getYoutubeId());
        }

        ArrayList<Mindcracker> sortedMindcrackers = new ArrayList<>(mindcrackers);

        Collections.sort(sortedMindcrackers, new Comparator<Mindcracker>() {
            @Override
            public int compare(Mindcracker lhs /*-1*/, Mindcracker rhs /*1*/) {

                if (lhs.getHits() > rhs.getHits()) {
                    return -1;
                }

                if (lhs.getHits() < rhs.getHits()) {
                    return 1;
                }

                return 0;
            }
        });

        List<Mindcracker> recommendedMindcrackers = sortedMindcrackers.subList(0, 5);

        for (Mindcracker mindcracker : recommendedMindcrackers){
            if (!mindcrackersYoutubeIds.contains(mindcracker.getYoutubeId())){
                mindcrackersYoutubeIds.add(mindcracker.getYoutubeId());
            }
        }

        Collections.shuffle(mindcrackersYoutubeIds);

        return mindcrackersYoutubeIds;
    }


    public void addFavorite(Mindcracker m) {
        //If already in favorites do nothing
        for (Mindcracker mindcracker : favoriteMindcrackers) {
            if (mindcracker.getId().equals(m.getId())) {
                return;
            }
        }

        getFavoriteMindcrackers().add(m);
        NotifyChange(BIND_FAVORITE_MINDCRACKERS, favoriteMindcrackers, 1);
    }

    public boolean isMindcrackerFavorite(Mindcracker m) {
        for (Mindcracker mindcracker : favoriteMindcrackers) {
            if (mindcracker.getId().equals(m.getId())) {
                return true;
            }
        }

        return false;
    }

    public void removeFavorite(Mindcracker m) {
        for (int i = 0; i < favoriteMindcrackers.size(); i++) {
            Mindcracker mindcracker = favoriteMindcrackers.get(i);
            if (mindcracker.getId().equals(m.getId())) {
                favoriteMindcrackers.remove(i);
                break;
            }
        }

        NotifyChange(BIND_FAVORITE_MINDCRACKERS, favoriteMindcrackers, -1);
    }


}
