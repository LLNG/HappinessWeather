package ln.lg.happinessweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ln.lg.happinessweather.DB.HappinessWeatherDB;
import ln.lg.happinessweather.modle.City;

/**
 * Created by LG on 2016/6/4 0004.
 */


public class RequestWithHttp {

	/**
	 * sendRequestWithHttpURLConnection
	 * @param address  网络访问地址
	 * @param listener  回调监听器
	 */
	public static void sendRequestWithHttpURLConnection(final String address, final HttpCallbackListener listener) {
		// 开启线程来发起网络请求
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = null;
					try {
						url = new URL(address);
						connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						connection.setConnectTimeout(80000);
						connection.setReadTimeout(80000);
						InputStream in = connection.getInputStream();
						//下面对获取到的输入流进行读取
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						StringBuilder response = new StringBuilder();
						String line;
						while ((line = reader.readLine())!= null) {
							response.append(line);
						}
						if (listener!=null){
							// 回调onFinish()方法
							listener.onFinish(response.toString());
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}



	/**
	 * 解析网络返回的XML数据
	 * @param response  网络返回数据字符串
	 * @param happinessWeatherDB    数据库实例对象
	 * @return  如果response不为空，解析完成并存储在数据库，返回true；如果response为空，不进行解析，返回false
	 */
	public synchronized static boolean ParseXML(String response, HappinessWeatherDB happinessWeatherDB) {
		try {
			if (!TextUtils.isEmpty(response)){

				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				XmlPullParser xmlPullParser = factory.newPullParser();
				xmlPullParser.setInput(new StringReader(response));
				int eventType = xmlPullParser.getEventType();

				City city=new City();

				while (eventType != xmlPullParser.END_DOCUMENT) {
					String str = xmlPullParser.getName();
					switch (eventType) {
						case XmlPullParser.START_TAG: {
							if ("weaid".equals(xmlPullParser.getName())) {
								int id = Integer.parseInt(xmlPullParser.nextText());
								city.setWeather_id(id);
							} else
							if ("citynm".equals(xmlPullParser.getName())) {
								String name = xmlPullParser.nextText();
								city.setCityName(name);
							} else if ("cityid".equals(xmlPullParser.getName())) {
								String code = xmlPullParser.nextText();
								city.setCity_Code(code);
							}
							break;
						}
						case XmlPullParser.END_TAG: {
							// 将解析出来的数据存储到City表
							if(str.startsWith("item_")) {
								happinessWeatherDB.saveCity(city);
							}
							break;
						}
						default:
							break;
					}
					eventType = xmlPullParser.next();
				}
				return true;
			}
		}
		catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
	 * @param context
	 * @param response  网络返回数据字符串
	 */
	public static void handleWeatherResponse(Context context, String response){
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("result");
			String date=weatherInfo.getString("days");      //当前日期
			String week=weatherInfo.getString("week");      //星期
			String cityName=weatherInfo.getString("citynm");      //城市名
			String temperature_curr=weatherInfo.getString("temperature_curr");  //当前温度
			String humidity=weatherInfo.getString("humidity");   //湿度
			String weather=weatherInfo.getString("weather");  //天气情况
			String wind=weatherInfo.getString("wind");  //风向
			String winp=weatherInfo.getString("winp");  //风级
			String temp_high = weatherInfo.getString("temp_high");
			String temp_low = weatherInfo.getString("temp_low");
			saveWeatherInfo(context, date, week,cityName,temperature_curr, humidity, weather, wind,winp,temp_high,temp_low);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
	 * @param context
	 * @param date  日期
	 * @param week  星期
	 * @param cityName  城市名
	 * @param temperature_curr  当前温度
	 * @param humidity  湿度
	 * @param weather   天气情况
	 * @param wind  风向
	 * @param winp  风力
	 * @param temp_high 最高温度
	 * @param temp_low  最低温度
	 */
	private static void saveWeatherInfo(Context context, String date, String week, String cityName, String temperature_curr, String humidity, String weather, String wind, String winp, String temp_high, String temp_low) {
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
		SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("days",date);
		editor.putString("week",week);
		editor.putString("city_name", cityName);
		editor.putString("temperature_curr", temperature_curr);
		editor.putString("humidity", humidity);
		editor.putString("weather", weather);
		editor.putString("wind", wind);
		editor.putString("winp", winp);
		editor.putString("temp_high", temp_high);
		editor.putString("temp_low", temp_low);
		editor.commit();
	}

}
