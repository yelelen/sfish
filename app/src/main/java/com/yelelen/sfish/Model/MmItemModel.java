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
public class MmItemModel implements Parcelable {
    @PrimaryKey
    @Column(name = "_id")
    private int order;
    @Column(name = "path")
    private String path;
    @Column(name = "fav_num")
    private int favNum;
    @Column(name = "seen_num")
    private int seenNum;
    @Column(name = "total_num")
    private int totalNum;
    @Column(name = "title")
    private String title;
    @Column(name = "tags")
    private String tag;
    @Column(name = "first_url")
    private String url;

    public MmItemModel() {
    }


    public MmItemModel(int order, String path, int favNum, int seenNum, int totalNum, String title, String tag, String url) {
        this.order = order;
        this.path = path;
        this.favNum = favNum;
        this.seenNum = seenNum;
        this.totalNum = totalNum;
        this.title = title;
        this.tag = tag;
        this.url = url;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFavNum() {
        return favNum;
    }

    public void setFavNum(int favNum) {
        this.favNum = favNum;
    }

    public int getSeenNum() {
        return seenNum;
    }

    public void setSeenNum(int seenNum) {
        this.seenNum = seenNum;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "MmItemModel{" +
                "order=" + order +
                ", path='" + path + '\'' +
                ", favNum=" + favNum +
                ", seenNum=" + seenNum +
                ", totalNum=" + totalNum +
                ", title='" + title + '\'' +
                ", tag='" + tag + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(order);
        dest.writeString(path);
        dest.writeInt(favNum);
        dest.writeInt(seenNum);
        dest.writeInt(totalNum);
        dest.writeString(title);
        dest.writeString(tag);
        dest.writeString(url);
    }

    public static final Parcelable.Creator<MmItemModel> CREATOR = new Creator<MmItemModel>() {
        @Override
        public MmItemModel createFromParcel(Parcel source) {
           return new MmItemModel(
                   source.readInt(),
                   source.readString(),
                   source.readInt(),
                   source.readInt(),
                   source.readInt(),
                   source.readString(),
                   source.readString(),
                   source.readString());
        }

        @Override
        public MmItemModel[] newArray(int size) {
            return new MmItemModel[size];
        }
    };
}
