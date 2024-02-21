package com.example.producerConsumer.controller;

import java.util.HashMap;
import com.example.producerConsumer.network.network;

public class Manager {
    private static Manager instance = null;
    private HashMap<String, IObserver> observers;

    private Manager(){
        this.observers = new HashMap<>();
    }

    //singleton design pattern
    public static Manager getInstance(){
        if(instance == null){
            return new Manager();
        }else{
            return instance;
        }
    }

    public void addObserver(String concern, IObserver observer) {
        this.observers.put(concern,observer);
    }

    public void removeObserver(String concern,IObserver observer) {
        this.observers.remove(concern,observer);
    }

    public void notify(String concern, network network) {
        if(concern.contains("M") || concern.contains("Q")) {

            observers.get(concern).update(network);
        }
    }
}
