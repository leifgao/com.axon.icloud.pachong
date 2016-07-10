package com.axon.bean;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueueEntity
{
	int messagequeue_capacity = 100000;
	LinkedBlockingQueue<String> mq = new LinkedBlockingQueue<String>();
	
	
	public boolean add(String element)
	{
		return mq.add(element);
	}
	
	public String poll()
	{
		return mq.poll();
	}
	
	public String take() throws InterruptedException
	{
		return mq.take();
	}
	
	public int size()
	{
		return mq.size();
	}
}
