package com.example.producerConsumer.controller;
import com.example.producerConsumer.network.network;

public class QueueObserver implements IObserver {
    public QueueObserver(){}

    public void update(network network){
        network.setChange(true);
    }
    public void update(){}
}
