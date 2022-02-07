package com.example.weatherapp.Fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.weatherapp.Model.AppConstants
import com.example.weatherapp.R
import com.example.weatherapp.Model.Weather
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL


class HomeFragment : Fragment(), View.OnClickListener {
    private var txtBatteryInfo: TextView? = null
    private var batteryLevel: Int = 0
    private var progressBar: CircularProgressIndicator? = null
    private var tvCity: TextView? = null
    private var tvCountry: TextView? = null
    private var tvTemperature: TextView? = null
    private var tvDescription: TextView? = null
    var position=0
    private val list: MutableList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        txtBatteryInfo = view.findViewById(R.id.tvBatteryInfo)
        progressBar = view.findViewById(R.id.progressbar)
        tvTemperature = view.findViewById(R.id.tvTemperature)
        tvCity = view.findViewById(R.id.tvCity)
        tvCountry = view.findViewById(R.id.tvCountry)
        tvDescription = view.findViewById(R.id.tvDescription)
        val connectionManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo
        context?.registerReceiver(batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        list.add(AppConstants.BEIJING)
        list.add(AppConstants.BERLIN)
        list.add(AppConstants.CARDIFF)
        list.add(AppConstants.EDINBURGH)
        list.add(AppConstants.LONDON)
        list.add(AppConstants.NOTTINGHAM)

        if (networkInfo != null && networkInfo.isConnected) { // Network check
            doNetworkCall(AppConstants.BASE_URL+list.get(position))
        } else {
            noNetworkDialog()
        }
        val btn: Button = view.findViewById(R.id.btnAppLauncher)
        btn.setOnClickListener(this)
        return view
    }
    private var batteryInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent) {
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val technology = intent.extras!!.getString(BatteryManager.EXTRA_TECHNOLOGY)
            progressBar?.progress = batteryLevel
            val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            txtBatteryInfo!!.text = """
                Battery Info
                Level: $batteryLevel %
                Technology: $technology
                Voltage: $voltage
                """.trimIndent()
        }
    }

    override fun onClick(p0: View?) { // click to AppLauncher Screen
        Navigation.findNavController(p0!!).navigate(R.id.action_homeToAppLauncher)
    }

    @DelicateCoroutinesApi
    @SuppressLint("SetTextI18n")
    fun doNetworkCall(url: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val connection = URL(url).openConnection() as HttpURLConnection

            var data =""
            try{
                data = connection.inputStream.bufferedReader().readText()

            } catch(e: FileNotFoundException){

            }

            withContext(Dispatchers.Main) {
                    setUi(data) // code for update UI
                }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n")
    private fun setUi(data: String) {
        val gson: Gson = Gson()
        if(data.isEmpty()){
            tvCity?.text = AppConstants.NO_RECORDS_FOUND
            tvCountry?.text =""
            tvDescription?.text = ""
            tvTemperature?.text = ""

        }else {
            val weather: Weather = gson.fromJson(data, Weather::class.java)
            tvCity?.text = weather.city
            tvCountry?.text = weather.country
            tvDescription?.text = weather.description
            tvTemperature?.text = weather.temperature.toString() + AppConstants.DEGREE_CELSIUS
        }
            val ha = Handler()
            if (position >= list.size - 1) {
                position = 0
            } else {
                position++
            }

            ha.postDelayed(java.lang.Runnable {
                doNetworkCall(AppConstants.BASE_URL + list[position])
            }, 10000)
    }

    private fun noNetworkDialog() {
        AlertDialog.Builder(context)
            .setTitle(AppConstants.ERROR)
            .setMessage(AppConstants.NO_NETWORK_AVAILABLE)
            .setCancelable(false)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setNeutralButton(
                AppConstants.OK
            ) { dialog, which ->  activity?.finish() }.show()
    }
}
