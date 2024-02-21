package com.example.producerConsumer.model;

import com.example.producerConsumer.controller.Manager;
import com.example.producerConsumer.controller.MachineObserver;
import com.example.producerConsumer.network.network;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class machine{

    String name;
    List<String> prevQueues;
    String nextQueue;
    long serviceTime;

    public long getServiceTime() {
        return serviceTime;
    }

    private product product;
    private String color;
    private boolean isconsumed = false;
    private Manager manager;
    private Thread produceThread;
    private Thread consumeThread;
    private final Object object = new Object();

    public machine(String name) {
        this.name = name;
        this.manager = Manager.getInstance();
        this.serviceTime = ThreadLocalRandom.current().nextInt(500, 6000);
        manager.addObserver(this.name, new MachineObserver(this.name));
    }

    public void setServiceTime(long serviceTime){
        this.serviceTime = serviceTime;
    }
    public String getName(){
        return this.name;
    }
    public List<String> getPrevQueues(){return this.prevQueues;}
    public void setPrevQueues(List<String> prevQueues){this.prevQueues = prevQueues;}
    public String getNextQueue(){return this.nextQueue;}
    public void setNextQueue(String nextQueue){this.nextQueue = nextQueue;}
    public product getProduct() {
        return this.product;
    }

    public void setProduct(product product) {
        this.product = product;
        if(product != null)
            this.color = product.getColor();
    }


    public void active(queue prevQueue, queue nextQueue, network network){
        Runnable consumer = () -> {
            System.out.println("inside active consumer");
            while (!consumeThread.isInterrupted()) {
                synchronized (object) {
                    try {
                        while (prevQueue.getProducts().isEmpty()) {
                            manager.notify(this.name, network);
                            object.wait();
                        }
                        this.setProduct(prevQueue.dequeue(network));
                        System.out.println(this.product.getColor()+" "+this.serviceTime);
                        manager.notify(this.name, network);
                        isconsumed = true;
                        object.wait();
                        object.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(network.stop) {
                    this.consumeThread.interrupt();
                }
            }
        };

        Runnable producer = () -> {
            while (!produceThread.isInterrupted()) {
                //System.out.println("inside active producer");
                synchronized (object) {
                    try {
                        if (!prevQueue.getProducts().isEmpty() && !isconsumed) {
                            object.notifyAll();
                        }
                        while (isconsumed && product != null) {
//                            System.out.println(this.product.getColor()+" "+this.serviceTime);
                            Thread.sleep(this.serviceTime);
                            nextQueue.enqueue(product, network);
                            object.notifyAll();
                            this.setProduct(null);
                            isconsumed = false;
                            object.wait();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(network.stop) {
                    this.produceThread.interrupt();
                }
            }
        };
        this.produceThread = new Thread(producer);
        this.consumeThread = new Thread(consumer);
        consumeThread.start();
        produceThread.start();

    }
}
