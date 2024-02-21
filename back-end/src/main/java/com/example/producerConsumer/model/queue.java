package com.example.producerConsumer.model;

import com.example.producerConsumer.controller.Manager;
import com.example.producerConsumer.controller.QueueObserver;
import com.example.producerConsumer.network.network;

import java.util.ArrayList;

public class queue {
    String name;
    private ArrayList<product> products;
    private Manager manager;
    int size;

    public queue(String name){
        this.products = new ArrayList<>();
        this.name = name;
        this.manager = Manager.getInstance();
        this.manager.addObserver(this.name, new QueueObserver());
    }

    public synchronized ArrayList<product> getProducts() {
        synchronized (this){
            return this.products;
        }
    }

    public void setProducts(ArrayList<product> products) {
        this.products = products;
    }

    public String getQueueName() {
        return this.name;
    }

    public int getSize() {
        return this.products.size();
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setQueueName(String name) {
        this.name = name;
    }

    public queue Clone(){
        queue newQueue = new queue(this.name);
        for(product p : this.products){
            newQueue.products.add(p.Clone());
        }
        newQueue.size = this.size;
        return newQueue;
    }

    public synchronized void enqueue(product product, network network) throws Exception{
        synchronized (this){
            this.products.add(product);
            System.out.println(this.name+" en "+this.products.size());
            this.manager.notify(this.name, network);
            this.notify();
        }
    }

    public product dequeue(network network) throws Exception{
        synchronized (this){
            while(this.products.isEmpty()) this.wait();
            System.out.println(this.name+" de "+this.products.size());
            this.manager.notify(this.name, network);
            return this.products.remove(0);
        }
    }





}
