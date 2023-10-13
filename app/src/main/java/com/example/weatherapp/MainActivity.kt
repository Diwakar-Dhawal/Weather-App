package com.example.weatherapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//  6037abe5ef69ede0e857965f373d5e9e
class MainActivity : AppCompatActivity() {
    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Delhi")
        SeachCity()
    }

    private fun SeachCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
    private fun changeImage(condition:String)
    {
        when(condition)
        {
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animview.setAnimation(R.raw.sun)
            }
            "Haze", "Partly Clouds","Clouds", " Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.animview.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.animview.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.animview.setAnimation(R.raw.snow)
            }
            else->
            {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animview.setAnimation(R.raw.sun)
            }
        }

        binding.animview.playAnimation()
    }

    private fun fetchWeatherData(cityname:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityname,"6037abe5ef69ede0e857965f373d5e9e","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody!=null)
                {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val windspeed = responseBody.wind.speed.toString()
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val sea = responseBody.main.pressure.toString()
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown".toString()
                    val tempmax = responseBody.main.temp_max.toString()
                    val tempmin = responseBody.main.temp_min.toString()

                    binding.tempTv.text = "$temperature °C"
                    binding.weatherTv.text = "$condition"
                    binding.conditionTv.text = "$condition"
                    binding.maxTemp.text = "Max Temp: $tempmax °C"
                    binding.minTemp.text = "Min Temp: $tempmin °C"
                    binding.humidityTv.text = "$humidity %"
                    binding.windspeedTv.text = windspeed+" m/s"
                    binding.sunriseTv.text = "${time(sunset)}"
                    binding.sunsetTv.text = "${time(sunRise)}"
                    binding.seaTv.text = "$sea hPa"
                    binding.dateTv.text=date(System.currentTimeMillis())
                    binding.dayTv.text = dayName(System.currentTimeMillis())
                    binding.locationTv.text="$cityname"

                    //Log.d("TAG", "onResponse: $temperature")
                    changeImage(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                //binding.dateTv.text = t.message
                Toast.makeText(this@MainActivity,"Error in getting location", Toast.LENGTH_SHORT).show()
            }

        })

    }
    fun date(timestamp: Long):String{
        val sdf = SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        return sdf.format(Date())
    }
    fun time(timestamp: Long):String{
        val sdf = SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdf.format(Date(timestamp*1000))

    }
    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE",Locale.getDefault())
        return sdf.format(Date())

    }
}


