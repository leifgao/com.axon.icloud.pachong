package com.axon.bean;

public class PersonInfoEntity
{
	//对应数据库中的各个字段
	private String phone;
	private String crawl;
	private int company;
	private String address;
	
	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getCrawl()
	{
		return crawl;
	}

	public void setCrawl(String crawl)
	{
		this.crawl = crawl;
	}

	public int getCompany()
	{
		return company;
	}

	public void setCompany(int company)
	{
		this.company = company;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}
	
	
}
