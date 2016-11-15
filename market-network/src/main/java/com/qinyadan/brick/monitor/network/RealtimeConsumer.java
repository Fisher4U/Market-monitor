package com.qinyadan.brick.monitor.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;
import com.qinyadan.brick.monitor.statistic.ServerStatisticManager;


public class RealtimeConsumer  implements MessageConsumer{
	
	private static final Logger logger = LoggerFactory.getLogger(RealtimeConsumer.class);
	
	public static final long MINUTE = 60 * 1000L;
	public static final long HOUR = 60 * MINUTE;
	
	//private MessageAnalyzerManager m_analyzerManager;

	private ServerStatisticManager m_serverStateManager;

	//private BlackListManager m_blackListManager;

	//private PeriodManager m_periodManager;

	private long m_black = -1;

	public RealtimeConsumer(){
	}

	@Override
	public void consume(MessageTree tree) {
		String domain = tree.getDomain();
		String ip = tree.getIpAddress();

		//if (!m_blackListManager.isBlack(domain, ip)) {
			long timestamp = tree.getMessage().getTimestamp();
			logger.info(String.valueOf(timestamp));
			logger.info(tree.getMessageId());
			//Period period = m_periodManager.findPeriod(timestamp);

			/*if (period != null) {
				period.distribute(tree);
			} else {
				m_serverStateManager.addNetworkTimeError(1);
			}*/
		//} else {
		//	m_black++;

		//	if (m_black % CatConstants.SUCCESS_COUNT == 0) {
		//		Monitor.logEvent("Discard", domain);
		//	}
		//}
	}

	@Override
	public void doCheckpoint() {
		
	}
}