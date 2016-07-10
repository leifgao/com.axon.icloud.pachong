package com.axon.startup;

import com.axon.configuration.PropertiesConfiguration;
import com.axon.service.TaskServer;

public class PersonSearchStartup
{
	
	public static void main(String[] args)
	{
		
		try
		{
			PropertiesConfiguration.setArgs(args);
			TaskServer taskManager = new TaskServer().getTaskServer();
			if(taskManager == null)
			{
				System.out.println("启动失败");
			}
			taskManager.run();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

}
