package bee.corp.kwepp

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.system.exitProcess

class WeatherRetriever(application: Application) : AndroidViewModel(application) {
    private var disposable: CompositeDisposable = CompositeDisposable()
    private var mutableWeatherLiveData: MutableLiveData<Array<String>> = MutableLiveData<Array<String>>()
    private var okHttpClient: OkHttpClient = OkHttpClient()
    private var app: Application

    init {
        app = application
    }

    fun retrieveWeather(city: String?, apikey: String) {
        val request: Request = Request.Builder().url("https://api.openweathermap.org/geo/1.0/direct?q=$city&limit=5&appid=$apikey").build()
        okHttpClient.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                //Toast.makeText(app.applicationContext, "Unable to Retrieve Weather", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful) {
                    try {
                        val jsonArray = JSONArray(response.body?.string())
                        val lon: String = jsonArray.getJSONObject(0).getString("lon")
                        val lat: String = jsonArray.getJSONObject(0).getString("lat")
                        disposable.add(getWeatherFromCords(lon, lat, apikey)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { result ->
                                mutableWeatherLiveData.value = result
                            })
                        getWeatherFromCords(lon, lat, apikey)
                    } catch (e: Exception) {}
                }
            }
        })
    }

    private fun getWeatherFromCords(lon: String, lat: String, apikey: String) : Observable<Array<String>> {
        return Observable.create { emitter ->
            val request: Request = Request.Builder().url("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$apikey").build()
            okHttpClient.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //Toast.makeText(app.applicationContext, "Unable to Retrieve Weather", Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val jsonArray = JSONObject(response.body?.string() ?: "null")
                        val temperature: Int = jsonArray.getJSONObject("main").getString("temp").toFloat().toInt() - 273
                        val description: String = jsonArray.getJSONArray("weather").getJSONObject(0).getString("description")
                        val name: String = jsonArray.getString("name")
                        emitter.onNext(arrayOf(temperature.toString(), description, name))
                        emitter.onComplete()
                    } catch (e: Exception) {}
                }
            })
        }
    }

    fun getMutableWeatherLiveData() : MutableLiveData<Array<String>> {
        return this.mutableWeatherLiveData
    }

}