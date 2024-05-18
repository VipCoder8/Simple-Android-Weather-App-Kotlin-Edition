package bee.corp.kwepp

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager

class MainActivity : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private lateinit var cityTitleView: TextView
    private lateinit var weatherIndicatorImageView: ImageView
    private lateinit var temperatureView: TextView
    private lateinit var weatherRetriever: WeatherRetriever
    private lateinit var wepLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModels()
        initViews()
    }
    private fun initViewModels() {
        weatherRetriever = ViewModelProvider(this)[WeatherRetriever::class.java]
        weatherRetriever.getMutableWeatherLiveData().observe(this) {
            wepLayout.visibility = View.VISIBLE
            temperatureView.text = it[0]
            cityTitleView.text = it[2]
        }
    }
    private fun initViews() {
        wepLayout = findViewById(R.id.wep_layout)
        searchView = findViewById(R.id.search_field)
        searchView.setOnQueryTextListener(object: OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                weatherRetriever.retrieveWeather(query, "43e1a2bea110324efd9b6dcedfbcabbc")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        cityTitleView = findViewById(R.id.city_title_view)
        weatherIndicatorImageView = findViewById(R.id.weather_indicator_image_view)
        temperatureView = findViewById(R.id.temperature_view)
    }
}