package com.qinyadan.monitor.server.tracer.support;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.alibaba.dubbo.tracer.api.Span;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.qinyadan.monitor.server.storage.Storage;
import com.qinyadan.monitor.server.storage.support.ElasticsearchStorage;
import com.qinyadan.monitor.server.tracer.TracerConsumer;

public class RocketMQTracerConsumer implements TracerConsumer {

	private DefaultMQPushConsumer consumer;
	private final Storage storage = new ElasticsearchStorage("127.0.0.1", 9300);
	private static final Map<MessageQueue, Long> OFFSE_TABLE = new HashMap<MessageQueue, Long>();

	public RocketMQTracerConsumer() throws MQClientException {
		this.consumer = new DefaultMQPushConsumer("MonitorConsumer");
		this.consumer.setNamesrvAddr("127.0.0.1:9876");
		this.consumer.subscribe("dst_span_topic", "*");

		this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		this.consumer.registerMessageListener(new MessageListenerConcurrently() {
			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				Map _r = new HashMap<>();
				for(MessageExt mess : msgs){
					JSONArray array = (JSONArray)JSONArray.parse(mess.getBody());
					for(int i = 0;i<array.size();i++ ){
						_r = (Map)array.get(i);
						_r.put("index_name", "application");
						_r.put("type_name", "monitor");
						storage.doStorage(_r);
					}
				}
				
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});

		this.consumer.start();
	}

	@Override
	public void consumer() {

	}

}
