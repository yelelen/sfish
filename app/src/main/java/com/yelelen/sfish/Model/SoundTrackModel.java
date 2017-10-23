package com.yelelen.sfish.Model;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by yelelen on 17-10-16.
 */

@Table(database = DB.class)
public class SoundTrackModel implements Comparable<SoundTrackModel>{
    @PrimaryKey
    @Column(name = "_id")
    private int order;
    @Column(name = "title")
    private String title;
    @Column(name = "duration")
    private int duration;
    @Column(name = "paths")
    private String paths;
    @Column(name = "play_count")
    private int playCount;
    @Column(name = "fav_count")
    private int favCount;


    public SoundTrackModel() {
    }

    public SoundTrackModel(int order, String title, int duration, String paths, int playCount, int favCount) {
        this.order = order;
        this.title = title;
        this.duration = duration;
        this.paths = paths;
        this.playCount = playCount;
        this.favCount = favCount;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }

    @Override
    public String toString() {
        return "SoundTrackModel{" +
                "order=" + order +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", paths='" + paths + '\'' +
                ", playCount=" + playCount +
                ", favCount=" + favCount +
                '}';
    }

    @Override
    public int compareTo(@NonNull SoundTrackModel o) {
        if (this.order > o.getOrder())
            return 1;
        else
            return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj instanceof SoundTrackModel) {
            if (this.order == ((SoundTrackModel) obj).order)
                return true;
        }
        return false;
    }
}
