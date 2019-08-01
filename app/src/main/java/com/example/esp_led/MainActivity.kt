package com.example.esp_led

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.file.Paths
import java.nio.file.Files
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.android.volley.toolbox.JsonObjectRequest
import org.jetbrains.anko.toast
import org.json.JSONObject
import kotlin.concurrent.fixedRateTimer
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass



//========GLOBAL VARIABLES==========//
var IpAddress = ""
var EspMacAddress = ""
var messages: List<NdefMessage> = emptyList()
var refreshLoop: Boolean = false

//==================================//


class MainActivity : AppCompatActivity() {
    val TAG:String="MainActivity"

    companion object {
        const val USER = "user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ipText = findViewById<TextView>(R.id.messageIP)
        val macText = findViewById<TextView>(R.id.messageMAC)
        helloMe.setOnClickListener{
            toast("Changing wifi to "+Config.SSID)
            connectToWPAWiFi(Config.SSID,Config.PASS)
        }

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                messages = rawMessages.map { it as NdefMessage }

                // Process the messages array.


                IpAddress = String(messages[0].records[0].payload).drop(3)
                EspMacAddress = String(messages[0].records[1].payload).drop(3)
                Log.v("TAG", IpAddress)
                Log.v("TAG", EspMacAddress)
                ipText.text = IpAddress
                macText.text = EspMacAddress
            }
        }

        fixedRateTimer(name = "status-timer", initialDelay = 0, period = 1000) {
            if (refreshLoop) {

                val statusView = findViewById<TextView>(R.id.status)
                val ledButton = findViewById<Button>(R.id.led_button)
                requestData()
            }
        }
    }

    fun requestData(){
        val queue = Volley.newRequestQueue(applicationContext)
        val url = "https://api.thingspeak.com/channels/830500/feeds.csv?api_key=H7K5MHX1WYU91GHC&results=1"
        // Request a string response from the provided URL.
        val request = StringRequest(Request.Method.GET,url,
            Response.Listener {
                //statusView.text = response.substring(0,4)
                //ledButton.text = it.substringAfterLast(',').dropLast(1)
                Log.v("TAG", it)
                var response = it.lines()
                var fieldNames = response[0].split(',')
                var fieldValues = response[1].split(',')
                fieldValues.forEach { println(it) }
                println(fieldValues.size)

            },
            Response.ErrorListener { })//statusView.text = "That didn't work!" })

        queue.add(request)  // Add the request to the RequestQueue.

    }


    fun refreshStatus(view: View){
        refreshLoop = !refreshLoop
        val buttonOn = findViewById<Button>(R.id.on_button)
        if (!refreshLoop) buttonOn.text = "Start"
        else buttonOn.text = "Stop"
    }

    fun toggleLed(view: View){
        val ledButton = findViewById<Button>(R.id.led_button)
        val queue = Volley.newRequestQueue(this)
        //val url = "http://$IpAddress/LED"
        val url = "https://api.thingspeak.com/update?api_key=3NZVPZOYMT6PL95Y&field1=1"
        val stringRequest = StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response -> response.substring(0)
                // Display the first 500 characters of the response string.
                //ledButton.text = response.substring(5)
                Log.v("TAG", response)
            },
            Response.ErrorListener { status.text = "That didn't work!" })

// Add the request to the RequestQueue.
        queue.add(stringRequest)


    }

    //connects to the given ssid
    fun connectToWPAWiFi(ssid:String,pass:String){
        if(isConnectedTo(ssid)){ //see if we are already connected to the given ssid
            toast("Connected to"+ssid)
            return
        }
        val wm:WifiManager= applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var wifiConfig=getWiFiConfig(ssid)
        if(wifiConfig==null){//if the given ssid is not present in the WiFiConfig, create a config for it
            createWPAProfile(ssid,pass)
            wifiConfig=getWiFiConfig(ssid)
        }
        wm.disconnect()
        wm.enableNetwork(wifiConfig!!.networkId,true)
        wm.reconnect()
        Log.d(TAG,"intiated connection to SSID"+ssid)
    }
    fun isConnectedTo(ssid: String):Boolean{
        val wm:WifiManager= applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if(wm.connectionInfo.ssid == ssid){
            return true
        }
        return false
    }
    fun getWiFiConfig(ssid: String): WifiConfiguration? {
        val wm:WifiManager= applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiList=wm.configuredNetworks
        for (item in wifiList){
            if(item.SSID != null && item.SSID.equals(ssid)){
                return item
            }
        }
        return null
    }
    fun createWPAProfile(ssid: String,pass: String){
        Log.d(TAG,"Saving SSID :"+ssid)
        val conf = WifiConfiguration()
        conf.SSID = ssid
        conf.preSharedKey = pass
        val wm:WifiManager= applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wm.addNetwork(conf)
        Log.d(TAG,"saved SSID to WiFiManger")
    }

    class WiFiChngBrdRcr : BroadcastReceiver(){ // shows a toast message to the user when device is connected to a AP
        private val TAG = "WiFiChngBrdRcr"
        override fun onReceive(context: Context, intent: Intent) {
            val networkInfo=intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
            if(networkInfo.state == NetworkInfo.State.CONNECTED){
                val bssid=intent.getStringExtra(WifiManager.EXTRA_BSSID)
                Log.d(TAG, "Connected to BSSID:"+bssid)
                val ssid=intent.getParcelableExtra<WifiInfo>(WifiManager.EXTRA_WIFI_INFO).ssid
                val log="Connected to SSID:"+ssid
                Log.d(TAG,"Connected to SSID:"+ssid)
                Toast.makeText(context, log, Toast.LENGTH_SHORT).show()
            }
        }
    }


}
