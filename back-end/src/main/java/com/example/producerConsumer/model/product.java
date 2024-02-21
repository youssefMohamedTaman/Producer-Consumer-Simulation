package com.example.producerConsumer.model;

public class product {
    private String color = "black";
    public product(){
        this.color = randomColor.generate();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public product Clone(){
        product copiedProduct = new product();
        copiedProduct.color = this.color;
        return copiedProduct;
    }
}
