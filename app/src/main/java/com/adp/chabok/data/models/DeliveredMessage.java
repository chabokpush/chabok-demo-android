package com.adp.chabok.data.models;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DeliveredMessage implements Serializable{

    private Map<String, Integer> map = new HashMap<>();

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }
}
