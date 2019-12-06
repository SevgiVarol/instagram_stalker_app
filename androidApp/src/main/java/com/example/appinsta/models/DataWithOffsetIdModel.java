package com.example.appinsta.models;

import java.util.List;

public class DataWithOffsetIdModel<T> {
    public List<T> Items;
    public String nextMaxId;

    public DataWithOffsetIdModel(List<T> Items, String nextMaxId) {
        this.Items = Items;
        this.nextMaxId = nextMaxId;
    }
}
