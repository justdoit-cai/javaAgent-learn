package com.just;

public class MyClass {
    public static void main(String[] args) throws InterruptedException {
        while(true){
            sayNice();
            Thread.sleep(1000);
        }
    }
    public static void sayNice(){
        System.out.println("Nice!");
    }
}
