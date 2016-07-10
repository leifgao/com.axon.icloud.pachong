package com.axon.service;


// 这个线程主要是用来维护mq这个消息队列的，启动这个线程之后，会一直轮训mq这个 消息队列，当队列的值为0的时候，会从数据库中
// 查询address为null的号码，将号码再次添加到mq这个消息队列中

import java.util.List;
import java.util.TimerTask;

import com.axon.bean.MessageQueueEntity;
import com.axon.bean.PersonInfoEntity;
import com.axon.configuration.PropertiesConfiguration;
import com.axon.dao.Database;

public class MessageQueueDamonThread extends TimerTask
{
	private MessageQueueEntity mq = new MessageQueueEntity();

	private PropertiesConfiguration pcon = PropertiesConfiguration.getCon();

	private String sql = pcon.getProperty("crawlsql");

	public MessageQueueDamonThread(MessageQueueEntity mq)
	{
		this.mq = mq;
	}

	public void run()
	{
		if (mq.size() == 0)
		{
			List<PersonInfoEntity> list = new Database().selectFromDb(sql);

			for (PersonInfoEntity perInfoEntity : list)
			{
				String phone = perInfoEntity.getPhone();
				mq.add(phone);
			}
		}

	}

}
