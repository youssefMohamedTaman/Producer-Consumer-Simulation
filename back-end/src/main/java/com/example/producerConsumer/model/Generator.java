package com.example.producerConsumer.model;
import java.util.ArrayList;
import java.util.List;

public class Generator {
        private ArrayList<Object> network;
        public void setNetwork(ArrayList<Object> network){this.network=network;}
        public ArrayList<Object> getNetwork(){
            return this.network;
        }
        public SnapShot savingNetwork()
        {
            return new SnapShot(this.network);
        }
        public void GetFromMem(SnapShot memento){
            this.network=memento.getNetwork();
        }
    }

