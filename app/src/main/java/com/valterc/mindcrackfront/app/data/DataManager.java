package com.valterc.mindcrackfront.app.data;

import android.content.Context;
import android.util.Log;

import com.valterc.mindcrackfront.app.data.storage.DataSource;
import com.vcutils.bindable.BindableBase;

import java.util.ArrayList;

/**
 * Created by Valter on 18/05/2014.
 */
public class DataManager extends BindableBase {

    public static final String BIND_FAVORITE_MINDCRACKERS = "favoriteMindcrackers";
    public static final String BIND_MINDCRACKERS = "mindcrackers";

    private DataSource dataSource;
    private ArrayList<Mindcracker> mindcrackers;
    private ArrayList<Mindcracker> favoriteMindcrackers;

    public DataManager(Context context){
        dataSource = new DataSource(context);

        mindcrackers = dataSource.getMindcrackers();
        favoriteMindcrackers = dataSource.getFavoriteMindcrackers();
    }

    public void dispose(){
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

    public void addFavorite(Mindcracker m){
        //If already in favorites do nothing
        for (Mindcracker mindcracker : favoriteMindcrackers){
            if (mindcracker.getId().equals(m.getId())){
                return;
            }
        }

        getFavoriteMindcrackers().add(m);
        NotifyChange(BIND_FAVORITE_MINDCRACKERS, favoriteMindcrackers, 1);
    }

    public boolean isMindcrackerFavorite(Mindcracker m){
        for (Mindcracker mindcracker : favoriteMindcrackers){
            if (mindcracker.getId().equals(m.getId())){
                return true;
            }
        }

        return false;
    }

    public void removeFavorite(Mindcracker m) {
        for (int i = 0; i < favoriteMindcrackers.size(); i++) {
            Mindcracker mindcracker = favoriteMindcrackers.get(i);
            if (mindcracker.getId().equals(m.getId())){
                favoriteMindcrackers.remove(i);
                break;
            }
        }

        NotifyChange(BIND_FAVORITE_MINDCRACKERS, favoriteMindcrackers, -1);
    }


}