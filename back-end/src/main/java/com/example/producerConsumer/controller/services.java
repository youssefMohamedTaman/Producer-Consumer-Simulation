package com.example.producerConsumer.controller;

import com.example.producerConsumer.model.CareTaker;
import com.example.producerConsumer.model.Generator;
import com.example.producerConsumer.model.machine;
import com.example.producerConsumer.model.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import com.example.producerConsumer.network.network;
import org.springframework.stereotype.Service;

@Service
public class services {
    public ArrayList<machine> machines = new ArrayList<>();
    public ArrayList<queue> queues = new ArrayList<>();
    private Generator generator = new Generator();
    private CareTaker careTaker = new CareTaker();
    network Network;

    public boolean constructNetworkAndRun(Map<String, ArrayList<String>> myNetwork){
        if(myNetwork.isEmpty()) {
            System.out.println("empty network!");
            return false;
        }
        for (String key : myNetwork.keySet()) {
           if(key.contains("M")){
               machine m = new machine(key);
               m.setServiceTime(ThreadLocalRandom.current().nextInt(500, 6000));
               m.setNextQueue(myNetwork.get(key).get(0));
               List<String> prevQs = new ArrayList<>();
               for(int i = 1; i < myNetwork.get(key).size(); i++){
                   prevQs.add(myNetwork.get(key).get(i));
               }
               m.setPrevQueues(prevQs);
               System.out.println(myNetwork.get(key).toString());
               machines.add(m);
           }
           else {
               queue q = new queue(key);
               queues.add(q);
           }
        }
        Network = new network();
        Network.setMachines(machines);
        Network.setQueues(queues);

        try {
            Network.play();
            return true;
        }catch (Exception e){
            System.out.println("Error in run!");
            return false;
        }
    }

    public Map<String, Object> polling(){
        //System.out.println(x);
            return Network.getNetwork();
    }

    public boolean stop(){
        try {
            Network.stop();
            machines = new ArrayList<>();
            queues = new ArrayList<>();
            return true;
        }catch (Exception e){
            return false;
        }

    }

    public boolean inputStop(){
        try {
            Network.inputStop();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean clear(){
        try {
            machines = new ArrayList<>();
            queues = new ArrayList<>();
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public ArrayList<ArrayList<Object>> replay(){
        ArrayList<ArrayList<Object>> networks = new ArrayList<>();
        for(int i=0; i < this.careTaker.GetSize();i++){
            this.generator.GetFromMem(this.careTaker.LoadSnapshot(i));
            networks.add(this.generator.getNetwork());
        }
        return networks;
    }


}
