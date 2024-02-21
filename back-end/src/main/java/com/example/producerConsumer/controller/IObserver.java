package com.example.producerConsumer.controller;
import com.example.producerConsumer.network.network;

public interface IObserver {
    void update(network network);
    void update();
}
