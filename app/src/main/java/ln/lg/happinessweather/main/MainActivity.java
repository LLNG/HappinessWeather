package ln.lg.happinessweather.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ln.lg.happinessweather.DB.HappinessWeatherDB;
import ln.lg.happinessweather.R;
import ln.lg.happinessweather.modle.City;
import ln.lg.happinessweather.utils.HttpCallbackListener;
import ln.lg.happinessweather.utils.RequestWithHttp;

import static ln.lg.happinessweather.utils.RequestWithHttp.ParseXML;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

	private TextView title;
	private ListView content;
	HappinessWeatherDB happinessWeatherDB;
	private List<String> dataList=new ArrayList<String>();
	private List<City> cityList;
	private ArrayAdapter<String> adapter;
	private ProgressDialog progressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		title= (TextView) findViewById(R.id.tv_title);
		content= (ListView) findViewById(R.id.lv_content);
		happinessWeatherDB = HappinessWeatherDB.getInstance(this);

		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, dataList);
		content.setAdapter(adapter);
		queryCities();
		content.setOnItemClickListener(this);
	}

	private void queryCities() {
		cityList= happinessWeatherDB.loadCities();
		if (cityList.size()>0){
			dataList.clear();
			for (City c:cityList){
				if (c.getCityName()!=null){
					dataList.add(c.getCityName());
				}
			}
			adapter.notifyDataSetChanged();
			title.setText("中国");
		}else {
			queryFromServer();
		}
	}

	private void queryFromServer() {
		String address="http://api.k780.com:88/?app=weather.city&&appkey=19598&sign=b7acfb71143e9a0a0729bf6cbef77b6e&format=xml";
		showProgressDialog();
		RequestWithHttp.sendRequestWithHttpURLConnection(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				result = ParseXML(response, happinessWeatherDB);
				if (result) {
					// 通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							queryCities();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});

			}
		});
	}


	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int weatherId=cityList.get(position).getWeather_id();
		Intent intent=new Intent(MainActivity.this,WeatherActivity.class);
		intent.putExtra("weatherId",weatherId);
		startActivity(intent);
		finish();
	}
}
