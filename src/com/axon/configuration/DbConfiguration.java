package com.axon.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class DbConfiguration
{
	public Connection conn;
	private String connectString;
	private static Logger logger = Logger.getLogger(DbConfiguration.class
			.getName());

	// 获取数据库的连接
	public final Connection getConnection()
	{
		try
		{
			connectString = new PropertiesConfiguration()
					.getProperty("connectString");
			conn = DriverManager.getConnection(connectString);
			return conn;
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return conn;
	}
}
