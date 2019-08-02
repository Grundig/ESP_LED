package com.example.esp_led

import java.util.*
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

class Device(deviceJSON: JSONObject) : Serializable {

    lateinit var deviceId: String
        private set


    lateinit var voltage: String
        private set

    lateinit var led_state: String
        private set

    init {
        try {
            deviceId = deviceJSON.getString(DEVICE_ID)
            voltage = deviceJSON.getString(VOLTAGE)
            led_state = deviceJSON.getString(LED_STATE)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
        companion object {
            private val DEVICE_ID = "device_id"
            private val VOLTAGE = "voltage"
            private val LED_STATE = "led_state"

        }
    }
