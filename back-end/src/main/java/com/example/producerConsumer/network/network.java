package com.example.producerConsumer.network;

import com.example.producerConsumer.model.*;

import java.util.*;

public class network {
    private boolean onChange = false;
    public boolean stop = false;
    public boolean inputStop = false;
    private static ArrayList<machine> machines;
    private static ArrayList<queue> queues;
    public static List<Thread> threads = new LinkedList<Thread>();
    public void flipChange(){
        this.onChange = !this.onChange;
    }
    public void setChange(boolean change){
        this.onChange = change;
    }
    public boolean getChange(){
        return this.onChange;
    }

    public ArrayList<machine> getMachines() {
        return machines;
    }

    public void setMachines(ArrayList<machine> machines) {
        network.machines = machines;
    }

    public ArrayList<queue> getQueues() {
        return queues;
    }

    public void setQueues(ArrayList<queue> newQueues) {
        queues = newQueues;
    }

    public network(){
        machines = new ArrayList<>();
        queues = new ArrayList<>();
    }

    public void play(){
        this.stop = false;


        try {
            System.out.println("called play");

            InputThread inputThread = new InputThread();
            inputThread.addProduct(queues.getFirst(), this);
            for(machine m:machines) {
                int indexInQueues1 = Integer.parseInt(m.getNextQueue().replaceAll("[^0-9]", ""));
                queue q1 = queues.get(indexInQueues1);
                for (String prevQueue: m.getPrevQueues()){
                        int indexInQueues = Integer.parseInt(prevQueue.replaceAll("[^0-9]", ""));
                        queue q = queues.get(indexInQueues);
                        m.active(q,q1,this);
                    }
                }
            }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /*public static void stop() {
        for (int i = 0; i < threads.size(); i++) {
            try {
                System.out.println(Thread.activeCount());
                threads.get(i).interrupt();

               } catch (Exception e) {
                System.out.println("Destroying!!!!!!");
                System.out.println(e.getMessage());
            }

        }
        clear();
    }*/
    public void stop(){
        this.stop = true;

    }
    public void inputStop(){
        this.inputStop = true;
    }

    public void clear(){
        threads = new LinkedList<>();
        machines = new ArrayList<>();
        queues = new ArrayList<>();
    }

    public Map<String, Object> getNetwork(){
        Map<String, Object> res = new HashMap<>();
        for(machine m : machines){
            res.put(m.getName(), m);
        }
        for(queue q: queues){
            res.put(q.getQueueName(), q);
        }
        return res;
    }
    private Generator generator = new Generator();
    private CareTaker careTaker = new CareTaker();

    public SnapShot createSnapshot() {
        ArrayList<Object> networkState = new ArrayList<>();
        networkState.add(new ArrayList<>(machines));
        networkState.add(new ArrayList<>(queues));
        return new SnapShot(networkState);
    }
    // Method to restore the state from a snapshot

    public void restoreSnapshot(SnapShot snapshot) {
        ArrayList<Object> networkState = snapshot.getNetwork();
        machines = (ArrayList<machine>) networkState.get(0);
        queues = (ArrayList<queue>) networkState.get(1);
    }

    public void saveState() {
        careTaker.AddSnapshot(createSnapshot());
    }

    // Load a specific state from the Memento
    public void loadState(int index) {
        restoreSnapshot(careTaker.LoadSnapshot(index));
    }

}
