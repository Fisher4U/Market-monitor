package com.qinyadan.market.plugins.tomcat78;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.qinyadan.monitor.agent.AbstractService;
import com.qinyadan.monitor.agent.JmxPlugin;
import com.qinyadan.monitor.agent.jmx.JMXManager;
import com.qinyadan.monitor.agent.jmx.vo.JMXMetricsValueInfo;
import com.qinyadan.monitor.agent.jmx.vo.JMXObjectNameInfo;
import com.qinyadan.monitor.network.packet.Packet;
import com.qinyadan.monitor.network.packet.SendPacket;

public class TomcatPluginService extends AbstractService implements JmxPlugin {

	public static final String NAME = "tomcat";

	public TomcatPluginService() throws IOException{
		super();
	}

	@Override
	public String jmxServerName() {
		return "org.apache.catalina.startup.Bootstrap";
	}

	@Override
	protected List<Packet> doCollect() {
		List<JMXMetricsValueInfo> jmxMetricsValueInfos = JMXManager.getJmxMetricValue(jmxServerName());
		List<Map> r = new ArrayList<>();
		for (JMXMetricsValueInfo jmxMetricsValueInfo : jmxMetricsValueInfos) {
			jmxMetricsValueInfo.getJmxConnectionInfo().setName(jmxServerName());
			for(JMXObjectNameInfo object : jmxMetricsValueInfo.getJmxObjectNameInfoList()){
				r.add(object.getMetricsValue());
			}
		}
		List<Packet>  cps = new ArrayList<>();
		for(Map _r : r){
			_r.put("index_name", "application");
			_r.put("type_name", "tomcat");
			SendPacket cp = new SendPacket(getSerializedBytes(_r));
			cps.add(cp);
		}
		return cps;
	}
	
	private static byte[] getSerializedBytes(Map list) {
		if (null == list || list.size() < 0)
			return null;
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bo);
			os.writeObject(list);
			return bo.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}
}
