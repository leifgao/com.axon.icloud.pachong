package com.axon.service;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.axon.bean.MessageQueueEntity;
import com.axon.bean.PersonInfoEntity;
import com.axon.configuration.PropertiesConfiguration;
import com.axon.dao.Database;

public class TaskServer
{
	MessageQueueEntity mq = new MessageQueueEntity();
	ExecutorService clawlService;
	int crawlThreadNumber = 10;
	public static PropertiesConfiguration proCon = PropertiesConfiguration.getCon();
	private static TaskServer instance = null;
	
	public  TaskServer getTaskServer()
	{
		if(instance == null)
		{
			instance = new TaskServer();
			if(proCon==null)
			{
				instance = null;
			}
		}
		return instance;
	}
	public void run() throws InterruptedException
	{
		// 创建一个线程池
		clawlService = Executors.newFixedThreadPool(crawlThreadNumber);
		for (int i = 0; i < crawlThreadNumber; i++)
		{
			CrawlThread crawlThread = new CrawlThread();
			crawlThread.setMq(mq);
			;
			Thread.sleep(200);
			clawlService.execute(crawlThread);
		}

		String sql = proCon.getProperty("crawlsql");
		System.out.println(sql);
		List<PersonInfoEntity> list = new Database().selectFromDb(sql);

		for (PersonInfoEntity perInfoEntity : list)
		{
			String phone = perInfoEntity.getPhone();
			mq.add(phone);
		}
		
		Timer timer = new Timer();
		timer.schedule(new PersonLevelThread(), 1000,60000);
		//启动一个mq的守护进程，维护mq队列
		timer.schedule(new MessageQueueDamonThread(mq), 1000,60000);
	}
}
