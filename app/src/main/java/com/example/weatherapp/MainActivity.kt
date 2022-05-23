package com.example.weatherapp

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.annotation.RequiresApi
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

    lateinit var jsonObject: JSONObject
    lateinit var coordOb: JSONObject
    lateinit var weatherArray: JSONArray
    lateinit var weatherOb: JSONObject
    lateinit var windOb: JSONObject
    lateinit var mainOb: JSONObject
    lateinit var sysOb: JSONObject


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchCity = findViewById(R.id.searchCity)
        search = findViewById(R.id.search)
        city = findViewById(R.id.city)
        temperature = findViewById(R.id.temperature)
        wind = findViewById(R.id.wind)

        imageView = findViewById(R.id.imageView)


        search.setOnClickListener() {
            val cityString: String = searchCity.text.toString()
            val url= "https://api.openweathermap.org/data/2.5/weather?q=$cityString&units=metric&lang=fi&appid=41f2ffd1ca49bbba0e811bcfd6b53b27"
            DownloadData(url)


        }

    }


    override fun onResume() {
        super.onResume()

        DownloadData("https://api.openweathermap.org/data/2.5/weather?q=tampere&units=metric&lang=fi&appid=41f2ffd1ca49bbba0e811bcfd6b53b27")


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

    fun DownloadData(url: String) {
        thread {
            try {
                var result: String?
                val sb = StringBuffer()
                val myUrl = URL(url)
                val connection = myUrl.openConnection() as HttpURLConnection
                val reader = BufferedReader(InputStreamReader(connection.inputStream))

                reader.use {
                    var line: String?
                    do {
                        line = it.readLine()
                        sb.append(line)
                    } while (line != null)
                    result = sb.toString()
                }
                jsonObject = JSONTokener(result).nextValue() as JSONObject
                coordOb = jsonObject.get("coord") as JSONObject
                weatherArray = jsonObject.getJSONArray("weather")
                weatherOb = weatherArray.get(0) as JSONObject
                windOb = jsonObject.get("wind") as JSONObject
                mainOb = jsonObject.get("main") as JSONObject
                sysOb = jsonObject.get("sys") as JSONObject
                updateView()

            } catch (e: Exception) {
                print(e.message)
                runOnUiThread {
                    Toast.makeText(applicationContext, "sijaintia ei löydy", Toast.LENGTH_SHORT)
                        .show()
                }
            }
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

