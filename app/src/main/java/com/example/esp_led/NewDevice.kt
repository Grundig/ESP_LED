package com.example.esp_led

import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NewDevice : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_device)
        val macText = findViewById<TextView>(R.id.messageMAC)


        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                messages = rawMessages.map { it as NdefMessage }

                // Process the messages array.


                EspMacAddress = String(messages[0].records[1].payload).drop(3)
                Log.v("TAG", EspMacAddress)
                macText.text = EspMacAddress
            }
        }
    }



    companion object {
        const val DEVICE = "device number"
    }
}


