package com.yelelen.sfish.Model;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

@Table(database = DB.class)
public class SoundLabelIndexModel {
    @PrimaryKey
    @Column(name = "_id")
    private String label;
    @Column(name = "orders")
    private String orders;

    public SoundLabelIndexModel() {
    }

    public SoundLabelIndexModel(String label, String orders) {
        this.label = label;
        this.orders = orders;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "SoundLabelIndexModel{" +
                "label='" + label + '\'' +
                ", orders='" + orders + '\'' +
                '}';
    }
}