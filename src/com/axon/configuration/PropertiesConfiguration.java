package com.axon.configuration;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesConfiguration
{
	private static PropertiesConfiguration instance = null;
	private Properties props = new Properties();
	Logger logger = Logger.getLogger(PropertiesConfiguration.class.getName());
	private static String[] args;
	private static String workDirectory = null;
	
	public static void setArgs(String[] args)
	{
		PropertiesConfiguration.args = args;
	}
	
	public  PropertiesConfiguration()
	{
		//当在linux中执行的时候执行
		//String workDirectory = System.getenv("RUN_HOME");
		//在windows下的时候执行
		 workDirectory ="E:\\sourcecode.java\\com.axon.icloud.pachong";
		if ((workDirectory == null) && (args != null) && (args.length > 0))
		{
			workDirectory = args[0];
			System.out.println("RUN_HOME=" + workDirectory);
		}
		if(loadConfig())
		{
			System.out.println("读取成功");
		}else
		{
			System.out.println("读取失败");
		}
	}
	
	private boolean loadConfig()
	{
		try
		{
			InputStreamReader in = new InputStreamReader(new FileInputStream(workDirectory + "/conf/searchphone.properties"),"UTF-8");
			props.load(in);
			return true;
		} catch (Exception e )
		{
			e.printStackTrace();
			return false;
		}
	}
	public static PropertiesConfiguration getCon()
	{
		if (instance == null)
		{
			instance = new PropertiesConfiguration();
		}
		return instance;
	}
	
	public static void setWorkDirectory(final String workDirectory)
	{
		PropertiesConfiguration.workDirectory = workDirectory;
	}
	/**
	 * @return 获得运行目录
	 */
	public static String getWorkDirectory()
	{
		return workDirectory;
	}

	/**
	 * @param 配置文件key值
	 * @return key对应的value值
	 */
	public String getProperty(final String string)
	{
		return props.getProperty(string);
	}

	/**
	 * @param name    配置文件参数名称
	 * @return        整形的参数值
	 */
	public int getProperty2Int(final String name)
	{
		int num = -1;
		try
		{
			num = Integer.valueOf(getProperty(name));
		} catch (NumberFormatException e)
		{
			logger.info(name + " is not Integer.");
		}
		return num;
	}
}
