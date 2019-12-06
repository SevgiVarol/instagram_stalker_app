package com.example.appinsta.models;

import java.util.List;

public class DataWithOffsetIdModel<T> {
    public List<T> items;
    public String nextMaxId;

    public DataWithOffsetIdModel(List<T> items, String nextMaxId) {
        this.items = items;
        this.nextMaxId = nextMaxId;
    }
}
