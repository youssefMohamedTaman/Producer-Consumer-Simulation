package com.example.producerConsumer.controller;
import com.example.producerConsumer.network.network;

public class MachineObserver implements IObserver {
    private String machineName;
    public MachineObserver(String machineName){
        this.machineName = machineName;
    }

    public void update(network network){
        network.setChange(true);
    }

    public void update(){
    }
}
