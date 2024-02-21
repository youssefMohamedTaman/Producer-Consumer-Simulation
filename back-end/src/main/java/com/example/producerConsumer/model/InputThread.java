package com.example.producerConsumer.model;

import java.util.concurrent.ThreadLocalRandom;
import com.example.producerConsumer.network.network;

public class InputThread {

    private Thread inputThread;

    public void addProduct(queue queue, network network){
        Runnable input = () -> {
            int products = ThreadLocalRandom.current().nextInt(40, 600);
            int check = 0;
            while(!inputThread.isInterrupted()){
                synchronized (this){
                    try{
                        if(check > products)
                            break;
                        long rate = ThreadLocalRandom.current().nextInt(300, 1000);
                        queue.getProducts().add(new product());
                        Thread.sleep(rate);
                        check++;
                    }
                    catch (Exception e){
                        System.out.println();
                    }
                }
                if(network.inputStop){
                    this.inputThread.interrupt();
                }
            }
        };
        this.inputThread = new Thread(input);
        this.inputThread.start();
    }
}
