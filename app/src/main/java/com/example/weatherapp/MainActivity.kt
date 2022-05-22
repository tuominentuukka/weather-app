package com.example.weatherapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    lateinit var searchCity: EditText
    lateinit var search: Button
    lateinit var temperature: TextView
    lateinit var city: TextView
    lateinit var wind: TextView

    lateinit var imageView: ImageView

    lateinit var jsonObject: JSONObject;
    lateinit var coordOb: JSONObject
    lateinit var weatherArray: JSONArray
    lateinit var weatherOb: JSONObject
    lateinit var windOb: JSONObject
    lateinit var mainOb: JSONObject
    lateinit var sysOb: JSONObject


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchCity = findViewById(R.id.searchCity)
        search = findViewById(R.id.search)
        city = findViewById(R.id.city)
        temperature = findViewById(R.id.temperature)
        wind = findViewById(R.id.wind)

        imageView = findViewById(R.id.imageView)
        //Glide.with(this).load("http://openweathermap.org/img/w/01d.png").into(imageView)

        //Picasso.get().load("http://openweathermap.org/img/w/01d.png").into(imageView)




        search.setOnClickListener() {
            thread {
                var cityString: String = searchCity.text.toString()
                var url= "https://api.openweathermap.org/data/2.5/weather?q=$cityString&units=metric&appid=41f2ffd1ca49bbba0e811bcfd6b53b27"
                getUrl(url)
                updateView()
            }
        }

    }


    override fun onResume() {
        //https://api.openweathermap.org/data/2.5/onecall?lat=61.4991&lon=23.7871&exclude=hourly,minutely&units=metric&appid=e25791362111e97f0444b9b2e69d610f
        //https://api.openweathermap.org/data/2.5/weather?q=tampere&units=metric&appid=41f2ffd1ca49bbba0e811bcfd6b53b27
        super.onResume()
        thread {
            getUrl("https://api.openweathermap.org/data/2.5/weather?q=tampere&units=metric&lang=fi&appid=41f2ffd1ca49bbba0e811bcfd6b53b27")
            updateView()
        }

    }

    fun updateView(){
        city.text = jsonObject.get("name").toString()
        temperature.text = mainOb.get("temp").toString() + " °C   " + weatherOb.get("description").toString()
        wind.text = windOb.get("speed").toString() + " m/s"


        when(weatherOb.getString("icon")){
            "01d", "01n" -> imageView.setImageResource(R.drawable.sun)
            "02d", "02n" -> imageView.setImageResource(R.drawable.half_cloydy)
            "03d", "03n" -> imageView.setImageResource(R.drawable.cloudy)
            "04d", "04n" -> imageView.setImageResource(R.drawable.half_cloydy)
            "09d", "09n" -> imageView.setImageResource(R.drawable.shower_rain)
            "10d", "10n" -> imageView.setImageResource(R.drawable.rain)
            "11d", "11n" -> imageView.setImageResource(R.drawable.thunderstorm2)
            "13d", "13n" -> imageView.setImageResource(R.drawable.snow)
            "50d", "50n" -> imageView.setImageResource(R.drawable.mist2)
        }
    }

    fun getUrl(url: String) {
        try {
            var result: String? = null
            val sb = StringBuffer()
            val myUrl = URL(url)
            val connection = myUrl.openConnection() as HttpURLConnection
            val reader = BufferedReader(InputStreamReader(connection.inputStream))

            reader.use {
                var line: String? = null
                do {
                    line = it.readLine()
                    sb.append(line)
                } while (line != null)
                result = sb.toString()
            }
            jsonObject = JSONTokener(result).nextValue() as JSONObject;
            coordOb = jsonObject.get("coord") as JSONObject
            weatherArray = jsonObject.getJSONArray("weather")
            weatherOb = weatherArray.get(0) as JSONObject
            windOb = jsonObject.get("wind") as JSONObject
            mainOb = jsonObject.get("main") as JSONObject
            sysOb = jsonObject.get("sys") as JSONObject
            val icon: String = weatherOb.getString("icon")
            val iconUrl = "http://openweathermap.org/img/w/$icon.png"
            println(iconUrl)

            //Picasso.get().load(iconUrl).into(imageView)



            println(coordOb)
            println(weatherOb)
            println(windOb)
            println(mainOb)
            println(sysOb)
            println(jsonObject)
        } catch (e: Exception){
            print(e.message)
        }

    }


    fun EditText.addMyKeyListener(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }
}
