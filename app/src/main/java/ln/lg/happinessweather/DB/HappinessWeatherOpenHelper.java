package ln.lg.happinessweather.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LG on 2016/5/19 0019.
 */
public class HappinessWeatherOpenHelper extends SQLiteOpenHelper {


	/**
	 * City表  建表语句
	 */
	public static final String CREATE_CITY ="create table City(" +
			"weatherId integer primary key," +
			"cityName text," +
			"cityCode text)";


	public HappinessWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CITY);         // 创建City表
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
