package com.qinyadan.brick.monitor.agent;

public class Main {
	public static void main(String[] args) {
		for(int i =0 ;i< 600000;i++){
			System.out.println("q");
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
