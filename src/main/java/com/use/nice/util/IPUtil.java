package com.use.nice.util;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPUtil {

	private static final String IP_PATTERN = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";

	public static String getInternetIP() {

		Random r = new Random();

		String IP = null;

		if (r.nextBoolean()) {
			IP = getIPFromTaoBao(false);
			if (isIP(IP)) {
				return IP;
			}
		}

		if (r.nextBoolean()) {
			IP = getIPFromIPapi(false);
			if (isIP(IP)) {
				return IP;
			}
		}

		IP = getIPFromIP138();
		if (isIP(IP)) {
			return IP;
		}
		return null;
	}

	public static boolean isIP(String ipAddress) {
		try {
			if (TextUtils.isEmpty(ipAddress)) {
				return false;
			}
			ipAddress = ipAddress.trim();
			Pattern pattern = Pattern.compile(IP_PATTERN);
			Matcher matcher = pattern.matcher(ipAddress);
			return matcher.matches();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// <body style="margin:0px"><center>���IP��ַ�ǣ�[218.104.71.178]
	// </center></body></html>
	public static String getIPFromIP138() {
		String IP = "";
		try {
			String address = "http://city.ip138.com/ip2city.asp";
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(false);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();
				// 将流转化为字符串
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String tmpString = "";
				StringBuilder retJSON = new StringBuilder();
				while ((tmpString = reader.readLine()) != null) {
					retJSON.append(tmpString + "\n");
				}
				Pattern pattern = Pattern.compile(IP_PATTERN);
				Matcher matcher = pattern.matcher(retJSON.toString());

				if (matcher.find()) {
					IP = matcher.group();
				}
			} else {
				IP = "";
			}
		} catch (Exception e) {
			IP = "";
		}
		return IP;
	}

	// <h1 class="page-title">My IP Address is 61.190.89.191</h1>
	public static String getIPFromCmyIP() {
		String IP = "";
		try {
			String address = "http://www.cmyip.com/";
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(false);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();
				// 将流转化为字符串
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String tmpString = "";
				StringBuilder retJSON = new StringBuilder();
				while ((tmpString = reader.readLine()) != null) {
					retJSON.append(tmpString + "\n");
				}

				String resultStr = retJSON.toString();

			//	LogUtil.printLog(true, "get IP From cMyIP :" + IP);

			} else {
				IP = "";
			}
		} catch (Exception e) {
			IP = "";
		}
		return IP;
	}

	// 61.190.89.191
	public static String getIPFromIPapi(boolean countryCode) {
		String IP = "";
		try {
			String address = "http://ip-api.com/json";
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(false);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();
				// 将流转化为字符串
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String tmpString = "";
				StringBuilder retJSON = new StringBuilder();
				while ((tmpString = reader.readLine()) != null) {
					retJSON.append(tmpString + "\n");
				}
				JSONObject jsonObject = new JSONObject(retJSON.toString());

				if (countryCode) {
					String country = jsonObject.optString("countryCode");
					return country;
				} else {
					String ip = jsonObject.optString("query");
					return ip;
				}
			} else {
				IP = "";
			}
		} catch (Exception e) {
			IP = "";
		}
		return IP;
	}

	// 61.190.89.191
	public static String getIPFromTaoBao(boolean country_id) {
		String IP = "";
		try {
			String address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(false);

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();

				// 将流转化为字符串
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				String tmpString = "";
				StringBuilder retJSON = new StringBuilder();
				while ((tmpString = reader.readLine()) != null) {
					retJSON.append(tmpString + "\n");
				}

				JSONObject jsonObject = new JSONObject(retJSON.toString());
				String code = jsonObject.getString("code");
				if (code.equals("0")) {
					JSONObject data = jsonObject.getJSONObject("data");
					if (country_id) {
						return data.getString("country_id");
					} else {
						IP = data.getString("ip");
					}
				} else {
					IP = "";
				}
			} else {
				IP = "";
			}
		} catch (Exception e) {
			IP = "";
		}
		return IP;
	}

	public static String getCountryFromLT() {

		String ip = getInternetIP();

		if (isIP(ip)) {
			try {
				String address = "http://location.appscomeon.com/location/index/ip/" + ip;
				URL url = new URL(address);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setUseCaches(false);

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream in = connection.getInputStream();
					// 将流转化为字符串
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));

					String tmpString = reader.readLine();

					return tmpString;
				} else {
					return "";
				}
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}

	public static String getIpFrom168() {
		URL infoUrl = null;
		InputStream inStream = null;
		String ipLine = "";
		HttpURLConnection httpConnection = null;
		try {
			infoUrl = new URL("http://ip168.com/");
			URLConnection connection = infoUrl.openConnection();
			httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inStream = httpConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
				StringBuilder strber = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null)
					strber.append(line + "\n");

				Pattern pattern = Pattern.compile(IP_PATTERN);
				Matcher matcher = pattern.matcher(strber.toString());

				if (matcher.find()) {
					ipLine = matcher.group();
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != inStream) {
					inStream.close();
				}
				if (null != httpConnection) {
					httpConnection.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	//	LogUtil.printLog(true, "getIpFrom168(" + ipLine + ")");

		return ipLine;
	}

	public static String getCountryCode() {

//		getCountryFromLT();

		Random r = new Random();
		String countryCode = "";
		boolean firstTaoBao = r.nextBoolean();
		if (firstTaoBao) {
			countryCode = getIPFromTaoBao(true);
			if (TextUtils.isEmpty(countryCode)) {
				countryCode = getIPFromIPapi(true);
				if (TextUtils.isEmpty(countryCode)) {
					getCountryFromLT();
				}
			}
		} else {
			countryCode = getIPFromIPapi(true);
			if (TextUtils.isEmpty(countryCode)) {
				countryCode = getIPFromTaoBao(true);
				if (TextUtils.isEmpty(countryCode)) {
					getCountryFromLT();
				}
			}
		}
		return countryCode;
	}
}