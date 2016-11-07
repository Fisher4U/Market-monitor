package com.qinyadan.brick.monitor.spi.message.ext.support;


import com.qinyadan.brick.monitor.spi.message.Message;
import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;
import com.qinyadan.brick.monitor.spi.message.ext.TaggedTransaction;
import com.qinyadan.brick.monitor.spi.message.internal.DefaultEvent;
import com.qinyadan.brick.monitor.spi.message.internal.DefaultTransaction;
import com.qinyadan.brick.monitor.spi.message.internal.MessageManager;

public class DefaultTaggedTransaction extends DefaultTransaction implements TaggedTransaction {
	private String m_rootMessageId;

	private String m_parentMessageId;

	private String m_tag;

	public DefaultTaggedTransaction(String type, String name, String tag, MessageManager manager) {
		super(type, name, manager);

		m_tag = tag;

		setStandalone(false);

		MessageTree tree = manager.getThreadLocalMessageTree();

		if (tree != null) {
			m_rootMessageId = tree.getRootMessageId();
			m_parentMessageId = tree.getMessageId();
		}
	}

	@Override
	public void bind(String tag, String childMessageId, String title) {
		DefaultEvent event = new DefaultEvent("RemoteCall", "Tagged");

		if (title == null) {
			title = getType() + ":" + getName();
		}

		event.addData(childMessageId, title);
		event.setTimestamp(getTimestamp());
		event.setStatus(Message.SUCCESS);
		event.setCompleted(true);

		addChild(event);
	}

	@Override
	public String getParentMessageId() {
		return m_parentMessageId;
	}

	@Override
	public String getRootMessageId() {
		return m_rootMessageId;
	}

	@Override
	public String getTag() {
		return m_tag;
	}

	@Override
	public void start() {
		MessageTree tree = getManager().getThreadLocalMessageTree();

		if (tree != null && tree.getRootMessageId() == null) {
			tree.setParentMessageId(m_parentMessageId);
			tree.setRootMessageId(m_rootMessageId);
		}
	}
}
