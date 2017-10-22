package com.yelelen.sfish.Model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by yelelen on 17-10-22.
 */

@Table(database = DB.class)
public class SoundZhuboModel {
    @PrimaryKey
    @Column(name = "_id")
    private int order;
    @Column(name = "cover")
    private String cover;
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "brief")
    private String brief;
    @Column(name = "cover_path")
    private String path;
    @Column(name = "fans_count")
    private int fansCount;
    @Column(name = "follow_count")
    private int followCount;
    @Column(name = "zan_count")
    private int zanCount;
    @Column(name = "sound_count")
    private int soundCount;


    public SoundZhuboModel() {
    }

    public SoundZhuboModel(int order, String cover, String nickname, String brief,
                           int fansCount, int followCount, int zanCount,
                           int soundCount, String path) {
        this.order = order;
        this.cover = cover;
        this.nickname = nickname;
        this.brief = brief;
        this.fansCount = fansCount;
        this.followCount = followCount;
        this.zanCount = zanCount;
        this.soundCount = soundCount;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public int getZanCount() {
        return zanCount;
    }

    public void setZanCount(int zanCount) {
        this.zanCount = zanCount;
    }

    public int getSoundCount() {
        return soundCount;
    }

    public void setSoundCount(int soundCount) {
        this.soundCount = soundCount;
    }

    @Override
    public String toString() {
        return "SoundZhuboModel{" +
                "order=" + order +
                ", cover='" + cover + '\'' +
                ", nickname='" + nickname + '\'' +
                ", brief='" + brief + '\'' +
                ", path='" + path + '\'' +
                ", fansCount=" + fansCount +
                ", followCount=" + followCount +
                ", zanCount=" + zanCount +
                ", soundCount=" + soundCount +
                '}';
    }
}
