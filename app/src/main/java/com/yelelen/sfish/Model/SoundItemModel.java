package com.yelelen.sfish.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by yelelen on 17-9-5.
 */
@Table(database = DB.class)
public class SoundItemModel implements Parcelable {
    @PrimaryKey
    @Column(name = "_id")
    private int order;
    @Column(name = "zhubo_id")
    private int zhuboId;
    @Column(name = "cover")
    private String cover;
    @Column(name = "title")
    private String title;
    @Column(name = "tag")
    private String tag;
    @Column(name = "last_update")
    private String lastUpdateTime;
    @Column(name = "play_count")
    private String playCount;
    @Column(name = "play_num")
    private long playNum;
    @Column(name = "desc")
    private String desc;
    @Column(name = "sounds")
    private String sounds;
    @Column(name = "path")
    private String path;

    public SoundItemModel() {
    }

    public SoundItemModel(int order, int zhuboId, long playNum, String cover, String title, String tag,
                          String lastUpdateTime, String playCount, String desc, String sounds,String path) {
        this.order = order;
        this.zhuboId = zhuboId;
        this.cover = cover;
        this.title = title;
        this.tag = tag;
        this.lastUpdateTime = lastUpdateTime;
        this.playCount = playCount;
        this.playNum = playNum;
        this.desc = desc;
        this.sounds = sounds;
        this.path = path;
    }

    public long getPlayNum() {
        return playNum;
    }

    public void setPlayNum(long playNum) {
        this.playNum = playNum;
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

    public int getZhuboId() {
        return zhuboId;
    }

    public void setZhuboId(int zhuboId) {
        this.zhuboId = zhuboId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSounds() {
        return sounds;
    }

    public void setSounds(String sounds) {
        this.sounds = sounds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(order);
        dest.writeInt(zhuboId);
        dest.writeLong(playNum);
        dest.writeString(cover);
        dest.writeString(title);
        dest.writeString(tag);
        dest.writeString(lastUpdateTime);
        dest.writeString(playCount);
        dest.writeString(desc);
        dest.writeString(sounds);
        dest.writeString(path);
    }

    public static final Creator<SoundItemModel> CREATOR = new Creator<SoundItemModel>() {
        @Override
        public SoundItemModel createFromParcel(Parcel source) {
           return new SoundItemModel(
                   source.readInt(),
                   source.readInt(),
                   source.readLong(),
                   source.readString(),
                   source.readString(),
                   source.readString(),
                   source.readString(),
                   source.readString(),
                   source.readString(),
                   source.readString(),
                   source.readString());
        }

        @Override
        public SoundItemModel[] newArray(int size) {
            return new SoundItemModel[size];
        }
    };
}
