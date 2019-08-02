package com.example.esp_led

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.device_list.*
import java.util.*
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.view.View

import com.example.esp_led.MainActivity.Companion.requestData


class DeviceList : AppCompatActivity() {
    private var deviceList: ArrayList<Device> = ArrayList()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerAdapter


    //private val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()

    companion object {
        const val USER = "user"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_list)
        MainActivity.setContext(this)
        requestData()

        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        recyclerView.layoutManager = linearLayoutManager
        adapter = RecyclerAdapter(deviceList)
        recyclerView.adapter = adapter
        setRecyclerViewScrollListener()

        }
    override fun onStart() {
        super.onStart()
        //if (deviceList.size == 0) {
          //  requestDevice()
        //}

    }
    private fun setRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager!!.itemCount
            //    if (!imageRequester.isLoadingData && totalItemCount == lastVisibleItemPosition + 1) {
              //      requestPhoto()
                //}
            }
        })
    }

    fun addDevice(view: View){
        val addDeviceIntent = Intent(this, NewDevice::class.java)
        addDeviceIntent.putExtra(NewDevice.DEVICE, deviceList.size)

        startActivity(addDeviceIntent)
    }


}