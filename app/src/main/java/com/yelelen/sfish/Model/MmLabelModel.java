package com.yelelen.sfish.Model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by yelelen on 17-9-5.
 */
@Table(database = DB.class)
public class MmLabelModel {
    @PrimaryKey
    @Column(name = "_id")
    private int order;
    @Column(name = "label")
    private String label;
    @Column(name = "cover")
    private String cover;
    @Column(name = "path")
    private String path;

    public MmLabelModel() {
    }

    public MmLabelModel(int order, String label, String cover, String path) {
        this.order = order;
        this.label = label;
        this.cover = cover;
        this.path = path;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "MmLabelModel{" +
                "order=" + order +
                ", label='" + label + '\'' +
                ", cover='" + cover + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
