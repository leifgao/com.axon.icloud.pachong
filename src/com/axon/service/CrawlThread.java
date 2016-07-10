package com.axon.service;

//这个是执行从网络抓取数据的线程

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.axon.bean.MessageQueueEntity;
import com.axon.configuration.PropertiesConfiguration;
import com.axon.dao.Database;
import com.axon.tool.encrypt.AxonEncrypt;

public class CrawlThread implements Runnable
{
	// 消息队列，用来存还未抓取的号码
	private MessageQueueEntity mq = new MessageQueueEntity();
	// 获取连接
	private PropertiesConfiguration proCon = PropertiesConfiguration.getCon();
	// 要查询的电话号码
	String searchphoneString = "";
	// 要操作的数据库
	private String tableString = proCon.getProperty("table");

	public void setMq(MessageQueueEntity mq)
	{
		this.mq = mq;
	}

	public void run()
	{
		AxonEncrypt aEncrypt = new AxonEncrypt();

		while (true)
		{
			String phone = "";
			try
			{
				phone = mq.take();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			// 如果电话是18位的，则是加密的数据
			if (phone.length() == 18)
			{
				searchphoneString = aEncrypt.decrypt(phone);
				searchphoneString = searchphoneString.substring(2, 13);
			} else
			{
				// 如果不是，则是明文数据
				searchphoneString = phone;
				searchphoneString = searchphoneString.substring(2, 13);
			}
			String url = "https://www.haosou.com/s?ie=utf-8&shb=1&src=360sou_newhome&q="
					+ searchphoneString;
			try
			{
				URL serverUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) serverUrl
						.openConnection();
				conn.setConnectTimeout(1000);
				String cookie = "QiHooGUID=ACEF8F680CBB941147A450DEC294EA2E.1438677883088; __guid=238775686.2542080674759798300.1438677883016.8748; GUID=XKKVGG7dn4P+y3ueZ6M5SM7QfmXOuzOUNswWxTHurldAilrxCphUhzNWsLnmGMmEYww8nx8soXfOSJsDnw==|a|1438677895.0919; tso_Anoyid=11143867789522100675; stc_haosou_home=8af45c19ecaa; __sid=238775686.2542080674759798300.1438677883016.8748.1439444272506; HSPK=828e85fde0076caf6fcc25540e6aa9a.1439444355; count=4; test_cookie_enable=null";
				conn.addRequestProperty("Cookie", cookie);
				// conn.addRequestProperty("Accept",
				// "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				// conn.addRequestProperty("Accept-Encoding",
				// "gzip, deflate, sdch");
				// conn.addRequestProperty("Accept-Language",
				// "zh-CN,zh;q=0.8,en;q=0.6");

				// conn.addRequestProperty("Host", "www.haosou.com");

				conn.addRequestProperty(
						"Referer",
						"https://www.haosou.com/s?psid=521f67eb63019b319171cfffa25c24d8&q=13023523519&src=srp&fr=360sou_newhome");
				conn.addRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
				conn.setDoOutput(true);

				trustAllHttpsCertificates();
				HttpsURLConnection.setDefaultHostnameVerifier(hv);

				conn.connect();
				InputStream ins = conn.getInputStream();
				String charset = "utf8";
				InputStreamReader inr = new InputStreamReader(ins, charset);
				BufferedReader br = new BufferedReader(inr);
				StringBuffer sb = new StringBuffer();
				String lineresult = "";
				do
				{
					sb.append(lineresult);
					lineresult = br.readLine();
				} while (lineresult != null);
				processContent(sb.toString(), phone);

			} catch (Exception ex)
			{
				System.out.println(ex);
			}

		}
	}

	private synchronized void processContent(String txt, String phone)
	{
		System.out.println(phone);
		String txtcontent = txt.replace("\\t", "");
		txtcontent = txtcontent.replace("\\r", ",");
		txtcontent = txtcontent.replace("\\n", ",");

		//这个正则表达式是匹配搜索出来的结果
		Pattern pattern = Pattern.compile("<h3 class=\"res-title \">(.*?)快照");
		Matcher matcher = pattern.matcher(txtcontent);
		String resultString = "";
		int i = 0;
		while (matcher.find() && i < 5)
		{
			String result = matcher.group(1);
			result = result.replaceAll("<[^>]+>|</[^>]+>", "");
			System.out.println(result);
			i++;
			resultString += result;
		}

		// 这段处理是提取出号码的归属地信息
		String addressString = "";
		Pattern p360 = Pattern
				.compile("<td class=\"mohe-mobileInfoContent(.*?)(提供|纠错)");
		Matcher m360 = p360.matcher(txtcontent);
		if (m360.find())
		{
			String result = m360.group(0);
			result = result.replaceAll("<[^>]+>|</[^>]+>", "");
			result = result.replaceAll("&nbsp;", "");
			result = result.replaceAll("请输入有效的电话号码（座机号码请加区号）归属地数据由360手机卫士提供",
					"");
			result = result.replaceAll(searchphoneString, "");
			result = result.replaceAll("shouji.360.cn/", "");

			addressString = result;
		}

		System.out.println(addressString);

		new Database().insertIntoDb(phone, resultString, addressString,
				tableString);
	}

	HostnameVerifier hv = new HostnameVerifier()
	{

		public boolean verify(String arg0, SSLSession arg1)
		{
			// TODO Auto-generated method stub
			return false;
		}
	};

	private static void trustAllHttpsCertificates() throws Exception
	{
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());
	}

	static class miTM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager
	{
		public java.security.cert.X509Certificate[] getAcceptedIssuers()
		{
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs)
		{
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs)
		{
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException
		{
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException
		{
			return;
		}
	}
}
