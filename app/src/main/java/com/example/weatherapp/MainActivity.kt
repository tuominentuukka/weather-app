package com.example.weatherapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread





class MainActivity : AppCompatActivity() {
    lateinit var city: EditText
    lateinit var search: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        city = findViewById(R.id.city)
        search = findViewById(R.id.search)


        search.setOnClickListener() {
            thread {
                var cityString: String = city.text.toString()
                var url= "https://api.openweathermap.org/data/2.5/weather?q=$cityString&units=metric&appid=41f2ffd1ca49bbba0e811bcfd6b53b27"
                getUrl(url)
            }
        }

    }

    override fun onResume() {
        //https://api.openweathermap.org/data/2.5/onecall?lat=61.4991&lon=23.7871&exclude=hourly,minutely&units=metric&appid=e25791362111e97f0444b9b2e69d610f
        //https://api.openweathermap.org/data/2.5/weather?q=tampere&units=metric&appid=41f2ffd1ca49bbba0e811bcfd6b53b27
        super.onResume()
        thread {
            getUrl("https://api.openweathermap.org/data/2.5/weather?q=tampere&units=metric&appid=41f2ffd1ca49bbba0e811bcfd6b53b27")
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
            println(result)

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

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherItem(var coord: String? = null){

    override fun toString(): String {
        return "lon = $coord"

    }
}


@JsonIgnoreProperties(ignoreUnknown = true)
data class Weather(var results: MutableList<WeatherItem>? = null)