package ln.lg.happinessweather.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ln.lg.happinessweather.R;
import ln.lg.happinessweather.utils.HttpCallbackListener;
import ln.lg.happinessweather.utils.RequestWithHttp;

/**
 * Created by LG on 2016/6/4 0004.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

	private LinearLayout weatherInfoLayout;
	/**
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	/**
	 * 用于显示发布时间
	 */
	private TextView publishText;
	/**
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	/**
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	/**
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	/**
	 * 用于显示当前日期
	 */
	private TextView currentDateText;
	/**
	 * 用于显示当前温度
	 */
	private TextView currentTemperature;
	/**
	 * 用于显示湿度
	 */
	private TextView humidity;
	/**
	 * 用于显示当前星期
	 */
	private TextView week;
	/**
	 * 用于显示风向
	 */
	private TextView wind;
	/**
	 * 用于显示风级
	 */
	private TextView winp;

	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_info);
		// 初始化各控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		currentTemperature= (TextView) findViewById(R.id.tv_temp_curr);
		wind= (TextView) findViewById(R.id.tv_wind);
		winp= (TextView) findViewById(R.id.tv_winp);
		week= (TextView) findViewById(R.id.week);
		humidity= (TextView) findViewById(R.id.tv_humidity);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		int id=1;
		String weatherId = String.valueOf(getIntent().getIntExtra("weatherId",id));
		if (!TextUtils.isEmpty(weatherId)) {
			// 有县级代号时就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherInfo(weatherId);
		} else {
			// 没有县级代号时就直接显示本地天气
			showWeather();
		}
	}

	private void queryWeatherInfo(String weatherId) {
		String weather_address = "http://api.k780.com:88/?app=weather.today&weaid="+weatherId+"&&appkey=19598&sign=b7acfb71143e9a0a0729bf6cbef77b6e&format=json";
		queryFromServer(weather_address, "weatherId");
	}

	private void queryFromServer(String weather_address, String weatherId) {
		RequestWithHttp.sendRequestWithHttpURLConnection(weather_address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				RequestWithHttp.handleWeatherResponse(WeatherActivity.this,response);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showWeather();
					}
				});

			}

			@Override
			public void onError(Exception e) {

			}
		});
	}

	private void showWeather() {
		SharedPreferences prefs = PreferenceManager. getDefaultSharedPreferences(this);
		cityNameText.setText( prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp_low", ""));
		temp2Text.setText(prefs.getString("temp_high", ""));
		weatherDespText.setText(prefs.getString("weather", ""));
//		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		currentDateText.setText(prefs.getString("days", ""));
		currentTemperature.setText(prefs.getString("temperature_curr",""));
		humidity.setText(prefs.getString("humidity",""));
		wind.setText(prefs.getString("wind",""));
		winp.setText(prefs.getString("winp",""));
		week.setText(prefs.getString("week",""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
//		Intent intent = new Intent(this, AutoUpdateService.class);
	}


	@Override
	public void onClick(View v) {

	}

	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
