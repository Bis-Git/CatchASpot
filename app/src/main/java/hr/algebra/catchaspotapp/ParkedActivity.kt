package hr.algebra.catchaspotapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Chronometer
import hr.algebra.catchaspotapp.framework.startActivityAndClearStack
import kotlinx.android.synthetic.main.activity_parked.*
import java.util.*

private const val R_CODE = 3

class ParkedActivity : AppCompatActivity() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var stopwatch: Chronometer
    private var price: Double = 0.0
    private var address: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parked)

        stopwatch = findViewById(R.id.chronometer)

        startTimer()
        init()
        setupListeners()

    }

    private fun init() {
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        price = intent.extras!!.getDouble("price")
        address = intent.extras!!.getString("address")!!
    }

    private fun setupListeners() {

        btnSetAlarm.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.SECOND, 1)

            val intent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, R_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

        btnLeave.setOnClickListener {
            stopwatch.stop()

            val time = stopwatch.text.toString()

            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("time", time)
            intent.putExtra("price", price)
            intent.putExtra("address", address)
            startActivity(intent)

        }

    }

    private fun startTimer() {

        var hour: String
        var min: String
        var sec: String

        stopwatch.setOnChronometerTickListener {
            var countUp = (SystemClock.elapsedRealtime() - it.base) / 1000
            hour = if ((countUp / 3600).toString().length < 2){
                "0" + (countUp / 3600).toString()
            }
            else{
                (countUp / 3600).toString()
            }
            min = if ((countUp / 60).toString().length < 2){
                "0" + (countUp / 60).toString()
            } else{
                (countUp / 60).toString()
            }
            sec = if ((countUp % 60).toString().length < 2){
                "0" + (countUp % 60).toString()
            } else{
                (countUp % 60).toString()
            }
            var asText = "$hour:$min:$sec"
            stopwatch.text = asText
        }

        stopwatch.start()
    }
}