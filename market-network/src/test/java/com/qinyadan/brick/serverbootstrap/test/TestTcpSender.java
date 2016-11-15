package com.qinyadan.brick.serverbootstrap.test;

import java.util.Date;

import com.qinyadan.brick.monitor.network.Monitor;
import com.qinyadan.brick.monitor.network.command.DefaultCommand;


public class TestTcpSender {
	
	public static void main(String[] args) {
		for(int i =0;i<1000;i++){
			Monitor.logHeartbeat("type", "123124", "red", "123123=12312");;
		}
		
		for(int i =0;i<1000;i++){
			Monitor.logEvent("dwe", "12312312");
		}
		
		for(int i =0;i<1000;i++){
			DefaultCommand commd = new DefaultCommand("command",new Date().getTime());
		}
	}

}
