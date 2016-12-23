package com.qinyadan.monitor.server.storage.support;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.qinyadan.monitor.server.storage.Storage;

public class ElasticsearchStorage implements Storage {

	public static final String CLUSTER_NAME = "elasticsearch";

	public String index_name;
	public String type_name;

	private static TransportClient client;

	public ElasticsearchStorage(String host, int port) {
		Settings settings = Settings.settingsBuilder().put("cluster.name", CLUSTER_NAME)
				.put("client.transport.sniff", true).build();
		try {
			client = TransportClient.builder().settings(settings).build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doStorage(Map map) {
		String id = UUID.randomUUID().toString();

		this.setIndex_name(map.get("index_name").toString());
		this.setType_name(map.get("type_name").toString());

		// 检查index是否存在不存在则先创建

		GetResponse get = client.prepareGet(index_name, type_name, id).execute().actionGet();

		index(index_name, type_name, id, map);
	}

	public void index(String index, String type, String id, Map<String, Object> data) {
		try {
			XContentBuilder xBuilder = jsonBuilder().startObject();
			Set<Entry<String, Object>> sets = data.entrySet();
			for (Entry<String, Object> entry : sets) {
				if (entry.getKey().contains(".")) {
					xBuilder.field(entry.getKey().replace(".", "-")).value(entry.getValue().toString());
				} else {
					xBuilder.field(entry.getKey()).value(entry.getValue().toString());
				}
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

	public void setIndex_name(String index_name) {
		this.index_name = index_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}

	/**
	 * 判断指定的索引名是否存在
	 * 
	 * @param indexName
	 *            索引名
	 * @return 存在：true; 不存在：false;
	 */
	public boolean isExistsIndex(String indexName) {
		IndicesExistsResponse response = client.admin().indices()
				.exists(new IndicesExistsRequest().indices(new String[] { indexName })).actionGet();
		return response.isExists();
	}

	/**
	 * 判断指定的索引的类型是否存在
	 * 
	 * @param indexName
	 *            索引名
	 * @param indexType
	 *            索引类型
	 * @return 存在：true; 不存在：false;
	 */
	public boolean isExistsType(String indexName, String indexType) {
		TypesExistsResponse response = client .admin().indices()
				.typesExists(new TypesExistsRequest(new String[] { indexName }, indexType)).actionGet();
		return response.isExists();
	}

}
