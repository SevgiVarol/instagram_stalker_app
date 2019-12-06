package com.example.appinsta.models;

import java.util.List;

public class DataWithOffsetIdModel<T> {
    public List<T> items;
    public String nextMaxId;

    public DataWithOffsetIdModel(List<T> Items, String nextMaxId) {
        this.items = Items;
        this.nextMaxId = nextMaxId;
    }
}
