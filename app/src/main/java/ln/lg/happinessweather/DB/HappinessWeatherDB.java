package ln.lg.happinessweather.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ln.lg.happinessweather.modle.City;


/**
 * Created by LG on 2016/5/19 0019.
 */
public class HappinessWeatherDB {

	/**
	 * 数据库名
	 */
	public static final String DB_NAME ="happiness weather";

	/**
	 * 数据库版本
	 */
	public static final int VERSION =1;
	private static HappinessWeatherDB happinessWeatherDB;
	private SQLiteDatabase db;

	/**
	 * 将构造方法私有化
	 */
	public HappinessWeatherDB(Context context) {
		HappinessWeatherOpenHelper dbHelper=new HappinessWeatherOpenHelper(context,DB_NAME,null,VERSION);
		db=dbHelper.getWritableDatabase();
	}

	/**
	 * 获取CoolWeatherDB的实例
	 */
	public synchronized static HappinessWeatherDB getInstance(Context context){
		if (happinessWeatherDB ==null){
			happinessWeatherDB =new HappinessWeatherDB(context);
		}
		return happinessWeatherDB;
	}


	/**
	 * 将City实例存储到数据库。
	 * @param city
	 */
	public void saveCity(City city){
		if (city!=null){
			ContentValues values=new ContentValues();
			values.put("weatherId",city.getWeather_id());
			values.put("cityName",city.getCityName());
			values.put("cityCode",city.getCity_Code());
			db.insert("City",null,values);
		}
	}

	/**
	 * 从数据库读取某省下所有的城市信息。
	 */
	public List<City> loadCities(){
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("City",null,null,null,null,null,null);
		if (cursor.moveToFirst()){
			do {
				City city=new City();
				city.setWeather_id(cursor.getInt(cursor.getColumnIndex("weatherId")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
				city.setCity_Code(cursor.getString(cursor.getColumnIndex("cityCode")));
				list.add(city);
			}while (cursor.moveToNext());
		}
		return list;
	}



}
