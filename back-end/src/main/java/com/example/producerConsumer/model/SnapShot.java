package com.example.producerConsumer.model;

import java.util.ArrayList;
public class SnapShot {
    private ArrayList<Object> network;
    public SnapShot(ArrayList<Object> network){
        this.network = network;
    }
    public ArrayList<Object> getNetwork() {
        return this.network;
    }
}