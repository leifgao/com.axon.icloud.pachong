package com.axon.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import com.axon.bean.PersonInfoEntity;
import com.axon.configuration.PropertiesConfiguration;
import com.axon.dao.Database;

public class PersonLevelThread extends TimerTask
{

	public void run()
	{
		PropertiesConfiguration proCon = PropertiesConfiguration.getCon();
		String table = proCon.getProperty("table");
		String badwordsql = proCon.getProperty("badwordsql");
		List<String> badKeyWordList = new Database()
				.selectFromBadWord(badwordsql);
		List<PersonInfoEntity> perInfoList = new ArrayList<PersonInfoEntity>();

		String personLevelSql = "select  phone,crawl from " + table
				+ " where crawl is not null and boolean is null";

		System.out.println("关键字的个数为：" + badKeyWordList.size());
		perInfoList = new Database().selectCrawlFromDb(personLevelSql);

		for (PersonInfoEntity person : perInfoList)
		{
			int company = 0;
			String crawl = person.getCrawl();
			String phone = person.getPhone();
			if (crawl.contains("公司") || crawl.contains("保险")
					|| crawl.contains("快递"))
			{
				company = 2;
			} else
			{
				for (String item : badKeyWordList)
				{
					if (crawl.contains(item))
					{
						company = 1;
					}
				}
			}
			String updateSql = "update " + table + " set boolean=" + company
					+ " where phone=" + phone;
			System.out.println(updateSql);
			new Database().updateDb(updateSql);
		}
		new Database()
				.updateDb("update "
						+ table
						+ " set boolean=3 where address like '%360%' or address like '%机构信息%'");
	}

}
