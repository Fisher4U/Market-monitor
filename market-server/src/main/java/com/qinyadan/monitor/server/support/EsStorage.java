package com.qinyadan.monitor.server.support;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import com.qinyadan.monitor.server.Storage;

public class EsStorage implements Storage {

	public static final String CLUSTER_NAME = "elasticsearch";
	public static final String index_name = "application-001";
	public static final String jvm_name = "jvm";

	private static TransportClient client;

	public EsStorage(String host, int port) {
		Settings settings = Settings.settingsBuilder().put("cluster.name", CLUSTER_NAME)
				.put("client.transport.sniff", true).build();
		try {
			client = TransportClient.builder().settings(settings).build().addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName(host), port));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doStorage(Map map) {
		String id = UUID.randomUUID().toString();
		GetResponse get = client.prepareGet(index_name, jvm_name,id).execute()
				.actionGet();
		index(index_name, jvm_name,id,map);
	}
	
	public void index(String index, String type, String id, Map<String, Object> data) {
		try {
			XContentBuilder xBuilder = jsonBuilder().startObject();
			Set<Entry<String, Object>> sets = data.entrySet();
			for (Entry<String, Object> entry : sets) {
				xBuilder.field(entry.getKey().replace(".", "-")).value(entry.getValue().toString());
			}
			xBuilder.endObject();
			
			client.prepareIndex(index, type).setId(id).setSource(xBuilder).execute().get();
		} catch (ElasticsearchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
