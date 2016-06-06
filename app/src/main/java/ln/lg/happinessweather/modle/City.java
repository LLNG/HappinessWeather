package ln.lg.happinessweather.modle;

/**
 * Created by LG on 2016/5/31 0031.
 */
public class City {

	public int getWeather_id() {
		return weather_id;
	}

	public void setWeather_id(int weather_id) {
		this.weather_id = weather_id;
	}

	private int weather_id;

	private String cityName;

	public String getCity_Code() {
		return city_Code;
	}

	public void setCity_Code(String city_Code) {
		this.city_Code = city_Code;
	}

	private String city_Code;

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

}
