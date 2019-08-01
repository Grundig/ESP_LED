package com.example.esp_led

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*

class RecyclerAdapter(private val devices: ArrayList<Device>) : RecyclerView.Adapter<RecyclerAdapter.DeviceHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.DeviceHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return DeviceHolder(inflatedView)

    }

    override fun getItemCount() = devices.size

    override fun onBindViewHolder(holder: RecyclerAdapter.DeviceHolder, position: Int) {
        val itemDevice = devices[position]
        holder.bindPhoto(itemDevice)

    }

    class DeviceHolder(private var view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private var device: Device? = null

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val context = itemView.context
            val showDeviceIntent = Intent(context, MainActivity::class.java)
            showDeviceIntent.putExtra(DEVICE_ID, device)
            context.startActivity(showDeviceIntent)

        }
        fun bindPhoto(device: Device) {
            this.device = device
            view.deviceID.text = device.deviceId
            view.voltage.text = device.voltage
            view.led_state.text = device.led_state
        }

        companion object {
            private val DEVICE_ID = "DEVICE"
        }
    }
}



