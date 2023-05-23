package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.widget.ImageButton
import android.os.Bundle
import android.content.pm.ActivityInfo
import android.support.v4.content.ContextCompat
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.v4.app.ActivityCompat
import android.os.Build
import android.view.View.OnTouchListener
import android.view.MotionEvent
import android.view.View
import java.io.IOException
import java.util.*

@SuppressWarnings
class MainActivity : AppCompatActivity() {
    var BA: BluetoothAdapter? = null
    var BTSocket: BluetoothSocket? = null
    var HC: BluetoothDevice? = null
    var isConnected = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN),
                    REQUEST_ENABLE_BT)
        }
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_ADMIN),
                    REQUEST_ENABLE_BT)
        }
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADMIN),
                        REQUEST_ENABLE_BT)
            }
        }
        val buttonConnect: ImageButton = findViewById(R.id.buttonConnect)
        buttonConnect.setOnClickListener(View.OnClickListener setOnClickListener@{ view: View? ->
            BA = BluetoothAdapter.getDefaultAdapter()
            if (BA == null) {
                return@setOnClickListener
            } else if (!BA!!.isEnabled) {
                return@setOnClickListener
            }
            val BTPairedDevices: Set<BluetoothDevice>? = BA!!.bondedDevices
            if (BTPairedDevices != null) {
                for (BTDev in BTPairedDevices) {
                    if ("HC-06" == BTDev.name && BTDev.address == "98:DA:60:05:CB:6C") {
                        HC = BTDev
                        try {
                            BTSocket = HC!!.createRfcommSocketToServiceRecord(MY_UUID)
                            val BTConnect = cBluetoothConnect()
                            BTConnect.start()
                            buttonConnect.setBackgroundColor(Color.GREEN)
                        } catch (e: IOException) {
                            return@setOnClickListener
                        } finally {
                            isConnected = true
                        }
                        break
                    }
                }
            }
        })
        val buttonForward: ImageButton = findViewById(R.id.buttonForward)
        //buttonForward.setBackgroundColor(Color.GREEN);
        buttonForward.setOnTouchListener(OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                println("pressed")
                buttonForward.isEnabled()
                write(FORWARD_PRESSED)
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                println("released")
                write(FORWARD_RELEASED)
            }
            false
        })
        val buttonBackward: ImageButton = findViewById(R.id.buttonBackward)
        //buttonBackward.setBackgroundColor(Color.BLUE);
        buttonBackward.setOnTouchListener(OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                println("pressed")
                write(BACKWARD_PRESSED)
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                println("released")
                write(BACKWARD_RELEASED)
            }
            false
        })
        val buttonLeft: ImageButton = findViewById(R.id.buttonLeft)
        //buttonLeft.setBackgroundColor(Color.MAGENTA);
        buttonLeft.setOnTouchListener(OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                println("pressed")
                write(LEFT_PRESSED)
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                println("released")
                write(LEFT_RELEASED)
            }
            false
        })
        val buttonRight: ImageButton = findViewById(R.id.buttonRight)
        //buttonRight.setBackgroundColor(Color.YELLOW);
        buttonRight.setOnTouchListener(OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                println("pressed")
                write(RIGHT_PRESSED)
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                println("released")
                write(RIGHT_RELEASED)
            }
            false
        })
        //TODO refactor code1
        val buttonSignal: ImageButton = findViewById(R.id.buttonSignal)
        buttonSignal.setOnTouchListener(OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                println("pressed")
                write(SIGNAL_PRESSED)
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                println("released")
                write(SIGNAL_RELEASED)
            }
            false
        })
        val buttonShoot: ImageButton = findViewById(R.id.buttonShoot)
        buttonShoot.setOnTouchListener(OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                println("pressed")
                write(SHOOT_PRESSED)
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                println("released")
                write(SHOOT_RELEASED)
            }
            false
        })
    }

    fun write(option: Int) {
        if (BTSocket == null) return
        try {
            if (BTSocket!!.isConnected) BTSocket!!.outputStream.write(option)
        } catch (io: IOException) {
            return
        }
    }

    inner class cBluetoothConnect : Thread() {
        @SuppressLint("MissingPermission")
        override fun run() {
            try {
                BTSocket!!.connect()
            } catch (e: IOException) {
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        const val FORWARD_PRESSED = 1
        const val BACKWARD_PRESSED = 2
        const val LEFT_PRESSED = 3
        const val RIGHT_PRESSED = 4
        const val SIGNAL_PRESSED = 5
        const val SHOOT_PRESSED = 6
        const val FORWARD_RELEASED = -1
        const val BACKWARD_RELEASED = -2
        const val LEFT_RELEASED = -3
        const val RIGHT_RELEASED = -4
        const val SIGNAL_RELEASED = -5
        const val SHOOT_RELEASED = -6
        private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") //00001101-0000-1000-8000-00805F9B34FB
    }
}