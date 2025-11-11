package edu.temple.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.os.Handler
import android.widget.TextView

lateinit var timerTextView : TextView
lateinit var timerBinder : TimerService.TimerBinder
var isConnected = false

val timerHandler = Handler(Looper.getMainLooper()) {
    timerTextView.text = it.what.toString()
    true
}
val serviceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        timerBinder = service as TimerService.TimerBinder
        timerBinder.setHandler(timerHandler)
        isConnected = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        isConnected = false
    }
}
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById<TextView>(R.id.textView)

        val timerIntent = Intent(this,TimerService::class.java)
        bindService(timerIntent,serviceConnection, Context.BIND_AUTO_CREATE)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            if(!timerBinder.isRunning){
                timerBinder.start(100)
                findViewById<Button>(R.id.startButton).text = "PAUSE"
            } else{
                timerBinder.pause()
                findViewById<Button>(R.id.startButton).text = "PAUSE"
                if(timerBinder.paused) findViewById<Button>(R.id.startButton).text = "RESUME"
            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            timerBinder.stop()
            findViewById<Button>(R.id.startButton).text = "START"
        }
    }

    override fun onDestroy(){
        unbindService(serviceConnection)
        super.onDestroy()
    }
}