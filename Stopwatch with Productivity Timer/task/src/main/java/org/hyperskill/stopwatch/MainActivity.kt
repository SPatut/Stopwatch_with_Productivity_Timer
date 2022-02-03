package org.hyperskill.stopwatch

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color.GRAY
import android.graphics.Color.RED
import android.os.Build
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random
import android.text.Editable
import android.text.InputType
import androidx.core.app.NotificationCompat


class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var editTextValue: Editable
    private var seconds = 0
    private var running = false
    private var setting = false
    val CHANNEL_ID = "org.hyperskill"
    val CHANNEL_NAME = "channelName"
    val NOTIFICATION_ID = 393939

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        textView.text = timeFormatting(seconds)
        settingsButton.isEnabled = !running

        createNotificationChannel()
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.clock)
            .setContentTitle("Delivery")
            .setContentText("Food is ready!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle())

            //.setContentIntent(pendingIntent)
            .build()

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder)



    }

    fun onClickReset(view: View) {
        running = false
        seconds = 0
        textView.text = timeFormatting(seconds)
        progressBar.visibility = View.INVISIBLE
        textView.setTextColor(GRAY)
        settingsButton.isEnabled = !running
    }

    fun onClickStart (view: View) {
        progressBar.visibility = View.VISIBLE
        if (!setting) alertDialog()
        setting = true

        if (!running) {
            running = true
            runTimer()
        }
        settingsButton.isEnabled = !running
    }

    fun onClickSettings (view: View) {
        setting = true
        alertDialog()

    }

    private fun runTimer() {
       textView = findViewById(R.id.textView)

        val handler = Handler()
        val run = object : Runnable {
            override fun run() {
                if (running){
                    setProgressBarColor()
                    handler.postDelayed(this, 1000)
                    val time = timeFormatting(seconds)
                    textView.text = time
                    if (editTextValue.isNotEmpty() && seconds > editTextValue.toString().toInt()){ textView.setTextColor(RED)}
                    seconds++
                }
                if (!running){
                    handler.removeCallbacks(this)
                }
            }
        }
        handler.post(run)
    }

    private fun setProgressBarColor() {
        val rnd = Random
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        val states = arrayOf(intArrayOf(android.R.attr.state_enabled))
        val colors = intArrayOf(color)
        val myProgressBarColorList = ColorStateList(states, colors)
        progressBar.indeterminateTintList = myProgressBarColorList
    }

    private fun timeFormatting (seconds: Int): String {
        return (String.format("%02d:%02d", seconds % 3600 / 60, seconds % 60))
    }

    private fun alertDialog() {

        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.alertDialog)

        val upperLimitEditText = EditText(this)

        upperLimitEditText.inputType = InputType.TYPE_CLASS_NUMBER //установит клавиатуру для ввода номера телефона
        editTextValue = upperLimitEditText.editableText


        builder.setView(upperLimitEditText)
        upperLimitEditText.id = R.id.upperLimitEditText

        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        builder.setPositiveButton(R.string.ok) {
                _, _ ->
            editTextValue = upperLimitEditText.editableText
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

        builder.setNegativeButton(getString(R.string.cancel)) {
                _, _ ->
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

        builder.setOnCancelListener {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

        val dialog: AlertDialog = builder.create()

        if(setting) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1)
            upperLimitEditText.inputType = InputType.TYPE_CLASS_NUMBER
            dialog.show()
        }
        else {
            dialog.hide()
        }

    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "Your delivery status"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = descriptionText
                lightColor = Color.GREEN
                enableLights(true)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}


