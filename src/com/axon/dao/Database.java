package com.axon.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.axon.bean.PersonInfoEntity;
import com.axon.configuration.DbConfiguration;

public class Database
{
	// 获取数据库的连接
	private Connection con = new DbConfiguration().getConnection();

	/**
	 * 从数据库中读取用户表
	 * 
	 * @param sql 要执行的sql文
	 *            
	 * @return 将表中的用户添加到list集合中返回
	 */
	public List<PersonInfoEntity> selectFromDb(String sql)
	{
		List<PersonInfoEntity> perInfoList = new ArrayList<PersonInfoEntity>();
		try
		{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				String phone = rs.getString(1);
				PersonInfoEntity perInfoEntiy = new PersonInfoEntity();
				perInfoEntiy.setPhone(phone);
				perInfoList.add(perInfoEntiy);
			}
			stmt.close();
			rs.close();
			return perInfoList;
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return perInfoList;
	}
	
	/**
	 * 从用户表中获取phone和crawl字段
	 * 
	 * @param sql 要执行的sql文
	 * @return 
	 */
	public List<PersonInfoEntity> selectCrawlFromDb(String sql)
	{
		List<PersonInfoEntity> perInfoList = new ArrayList<PersonInfoEntity>();
		try
		{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				String phone = rs.getString(1);
				System.out.println("电话是:"+phone);
				String crawl = rs.getString(2);
				PersonInfoEntity perInfoEntity = new PersonInfoEntity();
				perInfoEntity.setPhone(phone);
				perInfoEntity.setCrawl(crawl);
				perInfoList.add(perInfoEntity);
			}
			stmt.close();
			rs.close();
			return perInfoList;
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return perInfoList;
	}

	/**
	 * 从坏词表中获取坏词的内容
	 * 
	 * @param sql
	 * @return
	 */
	public List<String> selectFromBadWord(String sql)
	{
		List<String> badKeyWordList = new ArrayList<String>();
		try
		{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				String badword = rs.getString(1).replace("%", "");
				badKeyWordList.add(badword);
			}
			System.out.println("坏词的个数为：" + badKeyWordList.size());
			stmt.close();
			con.close();
			return badKeyWordList;
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return badKeyWordList;
	}

	/**
	 * 更新数据库用户表中crawl,address字段的值
	 * 
	 * @param phone
	 * @param resultString
	 * @param addressString
	 * @param tableString
	 */
	public void insertIntoDb(String phone, String resultString,
			String addressString, String tableString)
	{
		try
		{
			Statement stmt = con.createStatement();
			String sqlString = "replace into " + tableString
					+ " (phone,crawl,address)values (" + phone + ",'"
					+ resultString.replace("'", "#") + "','" + addressString
					+ "')";
			System.out.println(sqlString);
			// 当crwal和address都为空值，不执行插入
			// 也就是只要当中有一个值不为空，就执行插入数据
			if ((!(resultString.equals(""))) || (!(addressString.equals(""))))
			{
				stmt.execute(sqlString);
				stmt.close();
			}
			con.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void updateDb(String sql)
	{
		try
		{
			Statement stmt = con.createStatement();
			System.out.println(sql);
			stmt.execute(sql);
			stmt.close();
			con.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
