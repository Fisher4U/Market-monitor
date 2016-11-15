package com.qinyadan.brick.monitor.spi.message.ext.support;

import com.qinyadan.brick.monitor.Monitor;
import com.qinyadan.brick.monitor.spi.message.ext.ForkedTransaction;
import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;
import com.qinyadan.brick.monitor.spi.message.internal.DefaultTransaction;
import com.qinyadan.brick.monitor.spi.message.internal.MessageManager;

public class DefaultForkedTransaction extends DefaultTransaction implements ForkedTransaction {
	
	private String m_rootMessageId;

	private String m_parentMessageId;

	private String m_forkedMessageId;

	public DefaultForkedTransaction(String type, String name, MessageManager manager) {
		super(type, name, manager);

		setStandalone(false);

		MessageTree tree = manager.getThreadLocalMessageTree();

		if (tree != null) {
			m_rootMessageId = tree.getRootMessageId();
			m_parentMessageId = tree.getMessageId();

			// Detach parent transaction and this forked transaction, by calling linkAsRunAway(), at this earliest moment,
			// so that thread synchronization is not needed at all between them in the future.
			m_forkedMessageId = Monitor.createMessageId();
		}
	}

	@Override
	public void fork() {
		MessageManager manager = getManager();

		manager.setup();
		manager.start(this, false);

		MessageTree tree = manager.getThreadLocalMessageTree();

		if (tree != null) {
			// Override tree.messageId to be forkedMessageId of current forked transaction, which is created in the parent
			// thread.
			tree.setMessageId(m_forkedMessageId);
			tree.setRootMessageId(m_rootMessageId == null ? m_parentMessageId : m_rootMessageId);
			tree.setParentMessageId(m_parentMessageId);
		}
	}

	@Override
	public String getForkedMessageId() {
		return m_forkedMessageId;
	}
}
