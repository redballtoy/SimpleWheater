package com.gmail.redballtoy.simple_weather

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val token = "5059ff57-0e49-4c65-89c0-5ae4e23b3f51"
    private val lat = //52.52000659999999
        55.8057
    private val lon = //13.404953999999975
        37.5889


    final var TAG = "myLog"


    private val retrofitImpl: RetrofitImpl = RetrofitImpl() //implementation retrofit class


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendServerRequest()
        tv_weather_when.setOnClickListener {
            Toast.makeText(this, "Sent query", Toast.LENGTH_SHORT).show()
            sendServerRequest()

        }
    }

    //send server request
    private fun sendServerRequest() {
        retrofitImpl.getWeatherApi() //call interface methoh that return result query
            .getWeather(token, lat, lon)
            .enqueue( //asynchronous request
                object :
                    Callback<DataModel> {
                    //called if the request went away and a response was received
                    override fun onResponse(
                        call: Call<DataModel>,
                        response: Response<DataModel>//class with data is returned
                    ) {//checking that the answer is correct and does not include an error or null
                        if (response.isSuccessful && response.body() != null) {
                            renderData(response.body(), null)
                        } else {
                            renderData(null, Throwable("Ответ от сервера пустой"))
                        }
                    }

                    //called if the request failed
                    override fun onFailure(call: Call<DataModel>, t: Throwable) {
                        renderData(null, t)//send error information t
                    }
                }
            )

    }

    //processing of received data
    private fun renderData(dataModel: DataModel?, error: Throwable?) {
        if (dataModel == null || dataModel.fact == null || error != null) {
            Toast.makeText(this, error?.message, Toast.LENGTH_LONG).show()//error
        } else {
            Toast.makeText(
                this,
                "temp = " + dataModel.fact.temp.toString()+
                "\nfeel_like = "+dataModel.fact.feels_like.toString()+
                "\nobs_time = " + dataModel.fact.obs_time.toString()
                , Toast.LENGTH_LONG
            ).show()
//            Log.d(TAG, "temp = " + dataModel.fact.temp.toString())
//            Log.d(TAG, "\nfeel_like = " + dataModel.fact.feels_like.toString())
//            Log.d(TAG, "\nobs_time = " + dataModel.fact.obs_time.toString())


            val fact = dataModel.fact
            val temp = fact.temp
            if (temp == null) {
                tv_current_temperature.text = getString(R.string.nothing_value)
            } else {
                tv_current_temperature.text = temp.toString()
            }
            val feels_like = fact.feels_like
            if (temp == null) {
                tv_feels_like.text = getString(R.string.nothing_value)
            } else {
                tv_feels_like.text = feels_like.toString()
            }
            val obs_time = fact.obs_time
            if (temp == null) {
                tv_icon.text = getString(R.string.nothing_value)
            } else {
                val todayDate = java.util.Date(obs_time!! * 1000)
                val sdf = SimpleDateFormat()
                sdf.applyLocalizedPattern(getString(R.string.pattern_date_format))
                val timeZone = TimeZone.getTimeZone(getString(R.string.local_time_zone))
                sdf.format(todayDate)
                tv_weather_when.text = (getString(R.string.header_weather_today)
                        + todayDate.toString())
            }
            val condition = fact.condition
            if (condition.isNullOrEmpty()) {
                tv_icon.text = getString(R.string.nothing_value)
            } else {
                when (condition) {
                    "clear" -> tv_icon.text = getString(R.string.clear_ru)
                    "partly-cloudy" -> tv_icon.text = getString(R.string.partly_cloudy_ru)
                    "cloudy" -> tv_icon.text = getString(R.string.cloudy_ru)
                    "overcast" -> tv_icon.text = getString(R.string.overcast_ru)
                    "drizzle" -> tv_icon.text = getString(R.string.drizzle_ru)
                    "light-rain" -> tv_icon.text = getString(R.string.light_rain_ru)
                    "rain" -> tv_icon.text = getString(R.string.rain_ru)
                    "moderate-rain" -> tv_icon.text = getString(R.string.moderate_rain_ru)
                    "heavy-rain" -> tv_icon.text = getString(R.string.heavy_rain_ru)
                    "continuous-heavy-rain" -> tv_icon.text =
                        getString(R.string.continuous_heavy_rain_ru)
                    "showers" -> tv_icon.text = getString(R.string.showers_ru)
                    "wet-snow" -> tv_icon.text = getString(R.string.wet_snow_ru)
                    "light-snow" -> tv_icon.text = getString(R.string.light_snow_ru)
                    "snow" -> tv_icon.text = getString(R.string.snow_ru)
                    "snow-showers" -> tv_icon.text = getString(R.string.snow_showers_ru)
                    "hail" -> tv_icon.text = getString(R.string.hail_ru)
                    "thunderstorm" -> tv_icon.text = getString(R.string.thunderstorm_ru)
                    "thunderstorm-with-rain" -> tv_icon.text =
                        getString(R.string.thunderstorm_with_rain_ru)
                    "thunderstorm-with-hail" -> tv_icon.text =
                        getString(R.string.thunderstorm_with_hail_ru)
                    else -> tv_icon.text = getString(R.string.nothing_value)
                }
                //show icons
                when (condition) {
                    "clear" ->
                        iv_show_icons.setImageDrawable(getDrawable(R.drawable.sun))
                    "overcast","cloudy","partly-cloudy" ->
                        iv_show_icons.setImageDrawable(getDrawable(R.drawable.cloudiness))
                    "continuous-heavy-rain","heavy-rain",
                    "moderate-rain","rain","light-rain","drizzle" ->
                        iv_show_icons.setImageDrawable(getDrawable(R.drawable.rain))
                    "hail","snow-showers","snow","light-snow","wet-snow","showers" ->
                        iv_show_icons.setImageDrawable(getDrawable(R.drawable.snow))
                    "thunderstorm-with-hail","thunderstorm-with-rain","thunderstorm" ->
                        iv_show_icons.setImageDrawable(getDrawable(R.drawable.thunderstorm))
                    else ->
                        iv_show_icons.setImageDrawable(getDrawable(R.drawable.fog))
                }
            }
        }
    }
}

//data model creation
data class DataModel(
    val fact: Fact?
)

//JSON object containing the required variables
data class Fact(
    val temp: Int?, //temperature fact
    val feels_like: Int?, //temperature feel
    val condition: String?, //condition weather
    val obs_time: Long? //Measurement time of weather data in Unixtime format.
)

//description of the request interface
/*Sample query format:
GET https://api.weather.yandex.ru/v2/informers?lat=55.75396&lon=37.620393
*/
interface WeatherAPI { //query format
    @GET("v2/informers")//format of query and end point and end point
    fun getWeather(
        @Header("X-Yandex-API-Key") token: String, //header (developer API key)
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Call<DataModel> //result query will be Data model
}

//a class that will create and send a request
class RetrofitImpl {
    //returns a ready request for which the getWheater method can be called
    fun getWeatherApi(): WeatherAPI {
        //creating an instance of the retrofit class using a static method
        val retrofit = Retrofit.Builder() //get instance retrofit class
            .baseUrl("https://api.weather.yandex.ru/")//passing the base link
            .addConverterFactory(
                //adding a JSON converter factory
                //automatic convert JSON result from server to DataModel
                GsonConverterFactory.create(GsonBuilder().setLenient().create())
            )
            .build() //create retrofit object
        return retrofit.create(WeatherAPI::class.java)//pass interface
    }
}


