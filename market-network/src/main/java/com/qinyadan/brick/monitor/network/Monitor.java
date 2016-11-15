package com.qinyadan.brick.monitor.network;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.qinyadan.brick.monitor.CatConstants;
import com.qinyadan.brick.monitor.config.ClientConfigManager;
import com.qinyadan.brick.monitor.domain.ClientConfig;
import com.qinyadan.brick.monitor.domain.Domain;
import com.qinyadan.brick.monitor.domain.Server;
import com.qinyadan.brick.monitor.spi.message.Event;
import com.qinyadan.brick.monitor.spi.message.Heartbeat;
import com.qinyadan.brick.monitor.spi.message.MessageFactory;
import com.qinyadan.brick.monitor.spi.message.Trace;
import com.qinyadan.brick.monitor.spi.message.Transaction;
import com.qinyadan.brick.monitor.spi.message.ext.ForkedTransaction;
import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;
import com.qinyadan.brick.monitor.spi.message.ext.TaggedTransaction;
import com.qinyadan.brick.monitor.spi.message.internal.MessageManager;



/**
 * This is the main entry point to the system.
 */
public class Monitor {
	
	private static Monitor instance = new Monitor();

	private static volatile boolean init = false;

	private final MessageFactory producer;

	private final MessageManager manager;

	private static void checkAndInitialize() {
		if (!init) {
			synchronized (instance) {
				if (!init) {
					initialize("127.0.0.1:8889");
					log("WARN", "Monitor is lazy initialized!");
					init = true;
				}
			}
		}
	}

	public static String createMessageId() {
		return Monitor.getProducer().createMessageId();
	}

	public static void destroy() {
		instance = new Monitor();
	}

	public static String getCurrentMessageId() {
		MessageTree tree = Monitor.getManager().getThreadLocalMessageTree();

		if (tree != null) {
			String messageId = tree.getMessageId();

			if (messageId == null) {
				messageId = Monitor.createMessageId();
				tree.setMessageId(messageId);
			}
			return messageId;
		} else {
			return null;
		}
	}

	public static Monitor getInstance() {
		return instance;
	}

	public static MessageManager getManager() {
		checkAndInitialize();
		return instance.manager;
	}

	public static MessageFactory getProducer() {
		checkAndInitialize();
		return instance.producer;
	}

	public static void initialize(String... servers) {
		try {
			ClientConfig config = new ClientConfig().setMode("client");
			for (String server : servers) {
				config.addServer(new Server(server));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isInitialized() {
		return init;
	}

	static void log(String severity, String message) {
		MessageFormat format = new MessageFormat("[{0,date,MM-dd HH:mm:ss.sss}] [{1}] [{2}] {3}");

		System.out.println(format.format(new Object[] { new Date(), severity, "cat", message }));
	}

	public static void logError(String message, Throwable cause) {
		Monitor.getProducer().logError(message, cause);
	}

	public static void logError(Throwable cause) {
		Monitor.getProducer().logError(cause);
	}

	public static void logEvent(String type, String name) {
		Monitor.getProducer().logEvent(type, name);
	}

	public static void logEvent(String type, String name, String status, String nameValuePairs) {
		Monitor.getProducer().logEvent(type, name, status, nameValuePairs);
	}

	public static void logHeartbeat(String type, String name, String status, String nameValuePairs) {
		Monitor.getProducer().logHeartbeat(type, name, status, nameValuePairs);
	}

	public static void logMetric(String name, Object... keyValues) {
		// TO REMOVE ME
	}

	/**
	 * Increase the counter specified by <code>name</code> by one.
	 * 
	 * @param name
	 *           the name of the metric default count value is 1
	 */
	public static void logMetricForCount(String name) {
		logMetricInternal(name, "C", "1");
	}

	/**
	 * Increase the counter specified by <code>name</code> by one.
	 * 
	 * @param name
	 *           the name of the metric
	 */
	public static void logMetricForCount(String name, int quantity) {
		logMetricInternal(name, "C", String.valueOf(quantity));
	}

	/**
	 * Increase the metric specified by <code>name</code> by <code>durationInMillis</code>.
	 * 
	 * @param name
	 *           the name of the metric
	 * @param durationInMillis
	 *           duration in milli-second added to the metric
	 */
	public static void logMetricForDuration(String name, long durationInMillis) {
		logMetricInternal(name, "T", String.valueOf(durationInMillis));
	}

	/**
	 * Increase the sum specified by <code>name</code> by <code>value</code> only for one item.
	 * 
	 * @param name
	 *           the name of the metric
	 * @param value
	 *           the value added to the metric
	 */
	public static void logMetricForSum(String name, double value) {
		logMetricInternal(name, "S", String.format(Locale.US, "%.2f", value));
	}

	/**
	 * Increase the metric specified by <code>name</code> by <code>sum</code> for multiple items.
	 * 
	 * @param name
	 *           the name of the metric
	 * @param sum
	 *           the sum value added to the metric
	 * @param quantity
	 *           the quantity to be accumulated
	 */
	public static void logMetricForSum(String name, double sum, int quantity) {
		logMetricInternal(name, "S,C", String.format(Locale.US, "%s,%.2f", quantity, sum));
	}

	private static void logMetricInternal(String name, String status, String keyValuePairs) {
		Monitor.getProducer().logMetric(name, status, keyValuePairs);
	}

	public static void logRemoteCallClient(Context ctx) {
		MessageTree tree = Monitor.getManager().getThreadLocalMessageTree();
		String messageId = tree.getMessageId();

		if (messageId == null) {
			messageId = Monitor.createMessageId();
			tree.setMessageId(messageId);
		}

		String childId = Monitor.createMessageId();
		Monitor.logEvent(CatConstants.TYPE_REMOTE_CALL, "", Event.SUCCESS, childId);

		String root = tree.getRootMessageId();

		if (root == null) {
			root = messageId;
		}

		ctx.addProperty(Context.ROOT, root);
		ctx.addProperty(Context.PARENT, messageId);
		ctx.addProperty(Context.CHILD, childId);
	}

	public static void logRemoteCallServer(Context ctx) {
		MessageTree tree = Monitor.getManager().getThreadLocalMessageTree();
		String messageId = ctx.getProperty(Context.CHILD);
		String rootId = ctx.getProperty(Context.ROOT);
		String parentId = ctx.getProperty(Context.PARENT);

		if (messageId != null) {
			tree.setMessageId(messageId);
		}
		if (parentId != null) {
			tree.setParentMessageId(parentId);
		}
		if (rootId != null) {
			tree.setRootMessageId(rootId);
		}
	}

	public static void logTrace(String type, String name) {
		Monitor.getProducer().logTrace(type, name);
	}

	public static void logTrace(String type, String name, String status, String nameValuePairs) {
		Monitor.getProducer().logTrace(type, name, status, nameValuePairs);
	}

	public static Event newEvent(String type, String name) {
		return Monitor.getProducer().newEvent(type, name);
	}

	public static ForkedTransaction newForkedTransaction(String type, String name) {
		return Monitor.getProducer().newForkedTransaction(type, name);
	}

	public static Heartbeat newHeartbeat(String type, String name) {
		return Monitor.getProducer().newHeartbeat(type, name);
	}

	public static TaggedTransaction newTaggedTransaction(String type, String name, String tag) {
		return Monitor.getProducer().newTaggedTransaction(type, name, tag);
	}

	public static Trace newTrace(String type, String name) {
		return Monitor.getProducer().newTrace(type, name);
	}

	public static Transaction newTransaction(String type, String name) {
		return Monitor.getProducer().newTransaction(type, name);
	}

	// this should be called when a thread ends to clean some thread local data
	public static void reset() {
		// remove me
	}

	// this should be called when a thread starts to create some thread local data
	public static void setup(String sessionToken) {
		Monitor.getManager().setup();
	}

	private Monitor() {
		ClientConfigManager config = new DefaultClientConfigManager();
		this.manager = new DefaultMessageManager(config);
		this.producer = new DefaultMessageProducer(manager);
	}
	
	public static interface Context {

		public final String ROOT = "_MonitorRootMessageId";

		public final String PARENT = "_MonitorParentMessageId";

		public final String CHILD = "_MonitorChildMessageId";

		public void addProperty(String key, String value);

		public String getProperty(String key);
	}
	
	private class DefaultClientConfigManager implements ClientConfigManager{

		@Override
		public Domain getDomain() {
			Domain domain = new Domain();
			domain.setEnabled(true);
			domain.setId("test");
			domain.setIp("127.0.0.1");
			return domain;
		}

		@Override
		public int getMaxMessageLength() {
			return 0;
		}

		@Override
		public long getServerAddressRefreshInterval() {
			return 0;
		}

		@Override
		public String getServerConfigUrl() {
			return null;
		}

		@Override
		public List<Server> getServers() {
			List<Server> servers = new ArrayList<Server>();
			Server server = new Server();
			server.setIp("127.0.0.1");
			server.setPort(8889);
			server.setEnabled(true);
			server.setHttpPort("8080");
			servers.add(server);
			return servers;
		}

		@Override
		public int getTaggedTransactionCacheSize() {
			return 0;
		}

		@Override
		public void initialize(File configFile) throws Exception {
			
		}

		@Override
		public boolean isCatEnabled() {
			return true;
		}

		@Override
		public boolean isDumpLocked() {
			return false;
		}
		
	}

}
