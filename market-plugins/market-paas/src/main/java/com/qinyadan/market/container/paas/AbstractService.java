package com.qinyadan.market.container.paas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.qinyadan.monitor.agent.Plugin;
import com.qinyadan.monitor.logger.Logger;
import com.qinyadan.monitor.logger.LoggerFactory;
import com.qinyadan.monitor.network.api.DataSender;
import com.qinyadan.monitor.network.api.support.NettyDataSender;
import com.qinyadan.monitor.network.packet.Packet;

public abstract class AbstractService implements Plugin {

	private static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

	private final ExecutorService cm1Excutor = Executors.newFixedThreadPool(1, new ThreadFactory() {
		private AtomicInteger threadIndex = new AtomicInteger(0);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "cm1Excutor_" + this.threadIndex.incrementAndGet());
		}
	});

	private final Timer timer = new Timer("GCcolelctService", true);

	protected BlockingQueue<Packet> packets = new ArrayBlockingQueue<>(1000);

	private final DataSender dataSender;

	public AbstractService() throws IOException {
		this.timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					collect();
				} catch (Exception e) {
					logger.error("timer exception", e);
				}
			}
		}, 1000 * 3, 1000);

		this.cm1Excutor.submit(new CollectTask());

		this.dataSender = new NettyDataSender("127.0.0.1", 8889);

	}

	@Override
	public void collect() {
		Packet t = doCollect();
		if(t != null){
			packets.add(doCollect());
		}
	}

	protected abstract Packet doCollect();

	class CollectTask implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (packets.size() > 0) {
					try {
						Packet p = packets.take();
						List<Packet> datas = new ArrayList<>();
						datas.add(p);
						
						dataSender.send(datas);
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
