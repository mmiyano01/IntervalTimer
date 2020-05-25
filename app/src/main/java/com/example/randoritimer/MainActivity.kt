package com.example.randoritimer

import android.app.AlertDialog
import android.app.Dialog
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.*
import android.view.LayoutInflater
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.NumberPicker
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import kotlinx.android.synthetic.main.number_picker_dialog.*
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class MainActivity : AppCompatActivity() {
    var context = this;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        var center_text = findViewById<TextView>(R.id.center_text)
        var mode_text = findViewById<TextView>(R.id.mode_text)

        var pre_cercled_progress_bar = findViewById<ProgressBar>(R.id.pre_circled_progressbar)
        var cercled_progress_bar = findViewById<ProgressBar>(R.id.circled_progressbar)
        var break_cercled_progress_bar = findViewById<ProgressBar>(R.id.break_circled_progressbar)

        var start_button = findViewById<Button>(R.id.start_button)
        var pause_button = findViewById<Button>(R.id.pause_button)
        var stop_button = findViewById<Button>(R.id.stop_button)
        var work_time_settings_button = findViewById<Button>(R.id.work_time_settings_button)
        var break_time_settings_button = findViewById<Button>(R.id.break_time_settings_button)

        val mp = MediaPlayer.create (this, R.raw.whistle)

        var timer_running = false
        var timer_paused = false
        var timer_stopped = false
        var pre_timer_running = false
        var timer_max_sec = 60
        var break_time_max_sec = 25
        cercled_progress_bar.setMax(timer_max_sec)
        break_cercled_progress_bar.setMax(break_time_max_sec)

        center_text.text = "READY"

        break_cercled_progress_bar.visibility = GONE

        // Countdown Object for actual countdown
        var time_num = 0
        var break_time_num = 0
        var breaking = false
        var work_completed = false

        mode_text.text = "$timer_max_sec"

        fun timer(millisInFuture:Long,countDownInterval:Long):CountDownTimer{
            return object: CountDownTimer(millisInFuture, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (timer_paused || timer_stopped) {
                        if (timer_stopped){
                        }
                        this.cancel()

                    } else if (breaking) {
                        break_time_num += 1

                        val count_down_num = break_time_max_sec + 1 - break_time_num
                        break_cercled_progress_bar.setProgress(break_time_num, true)
                        center_text.text = "$count_down_num"

                    } else {
                        time_num += 1

                        val count_down_num = timer_max_sec + 1 - time_num
                        cercled_progress_bar.setProgress(time_num, true)
                        center_text.text = time_formatter(count_down_num)
                    }
                }

                override fun onFinish() {
                    mp.start()
                    if (breaking) {
                        breaking = false
                        break_cercled_progress_bar.visibility = GONE
                        cercled_progress_bar.visibility = VISIBLE
                        break_time_num = 0
                        break_cercled_progress_bar.setProgress(0)
                        timer((timer_max_sec * 1000).toLong(),1000).start()
                    } else {
                        if (work_completed) {
                            center_text.text = "DONE"
                            time_num = 0
                            timer_running = false
                        } else {
                            breaking = true
                            cercled_progress_bar.visibility = GONE
                            break_cercled_progress_bar.visibility = VISIBLE
                            time_num = 0
                            cercled_progress_bar.setProgress(0)
                            timer((break_time_max_sec * 1000).toLong(),1000).start()
                        }
                    }
                }
            }
        }

        // Countdown Object for pre-countdown
        var pre_time_num = 0
        pre_cercled_progress_bar.setMax(5)
        val pre_timer = object: CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (timer_stopped){
                    pre_timer_running = false
                    this.cancel()
                } else {
                    pre_time_num += 1
                    val count_down_num = 6 - pre_time_num
                    pre_cercled_progress_bar.setProgress(pre_time_num)
                    center_text.text = "$count_down_num"
                }
            }

            override fun onFinish() {
                mp.start()
                pre_cercled_progress_bar.visibility = GONE
                cercled_progress_bar.visibility = VISIBLE
                pre_time_num = 0
                pre_cercled_progress_bar.setProgress(0)
                timer_running = true
                pre_timer_running = false
                timer((timer_max_sec * 1000).toLong(),1000).start()
            }
        }

        start_button.setOnClickListener({
            timer_stopped = false
            if (!timer_running) {
                cercled_progress_bar.visibility = GONE
                pre_cercled_progress_bar.visibility = VISIBLE
                pre_timer_running = true
                pre_cercled_progress_bar.setMax(5)
                pre_timer.start()
            } else if (timer_paused) {
                timer_paused = false
                if (breaking) {
                    timer(((break_time_max_sec - break_time_num) * 1000).toLong(),1000).start()
                } else {
                    timer(((timer_max_sec - time_num) * 1000).toLong(),1000).start()
                }
            }
        })

        pause_button.setOnClickListener({
            if (timer_running && !timer_paused){
                timer_paused = true
            } else if (pre_timer_running) {
                pre_timer.cancel()
                pre_cercled_progress_bar.setProgress(5,true)
                pre_time_num = 0
                center_text.text = "READY"
            }
        })

        stop_button.setOnClickListener({
            if (timer_running) {
                timer_stopped = true
            } else if (pre_timer_running) {
                pre_timer.cancel()
            }
            cercled_progress_bar.visibility = GONE
            break_cercled_progress_bar.visibility = GONE
            pre_cercled_progress_bar.visibility = VISIBLE
            time_num = 0
            break_time_num = 0
            pre_time_num = 0
            cercled_progress_bar.setProgress(time_num)
            break_cercled_progress_bar.setProgress(break_time_num)
            pre_cercled_progress_bar.setProgress(5,true)
            center_text.text = "READY"
            timer_running = false
            timer_paused = false
            breaking = false
            work_completed = false
        })

        work_time_settings_button.setOnClickListener {
            if(!timer_running) {
                val dialogBuilder = AlertDialog.Builder(this)
                val inflater = this.layoutInflater
                val dialogView = inflater.inflate(R.layout.number_picker_dialog, null)
                dialogBuilder.setView(dialogView)
                val alertDialog = dialogBuilder.create()

                //np1.setOnValueChangedListener { picker, oldVal, newVal ->
                //    val text = "Changed from $oldVal to $newVal"
                //    Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                //}
                //val np2 = alertDialog.findViewById(R.id.numberPicker2) as NumberPicker
                //np2.setMaxValue(60)
                //np2.setMinValue(0)
                //np2.setWrapSelectorWheel(false)
                //np2.setOnValueChangedListener { picker, oldVal, newVal ->
                //    val text = "Changed from $oldVal to $newVal"
                //    Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                //}

                alertDialog.show()
                var set_min = timer_max_sec / 60
                var set_sec = timer_max_sec % 60

                val np1 = alertDialog.findViewById(R.id.numberPicker1) as NumberPicker
                val nums = arrayOfNulls<String>(60)
                for (i in nums.indices)
                    nums[i] = Integer.toString(i)
                np1.setMaxValue(60)
                np1.setMinValue(0)
                np1.setWrapSelectorWheel(false)
                np1.setDisplayedValues(nums)
                np1.setValue(set_min)

                np1.setOnValueChangedListener { _, _, newVal ->
                    set_min = newVal
                }


                val np2 = alertDialog.findViewById(R.id.numberPicker2) as NumberPicker
                var nums2 = arrayOfNulls<String>(59)
                for (i in nums2.indices)
                    nums2[i] = Integer.toString(i)
                np2.setMaxValue(59)
                np2.setMinValue(0)
                np2.setWrapSelectorWheel(false)
                np2.setDisplayedValues(nums2)
                np2.setValue(set_sec)

                np2.setOnValueChangedListener { _, _, newVal ->
                    set_sec = newVal
                }

                val set_time_button = alertDialog.findViewById(R.id.set_time_button) as Button

                set_time_button.setOnClickListener{
                    timer_max_sec = (set_min * 60) + set_sec

                    var set_sec_text = ""
                    if(set_sec < 10){
                        set_sec_text = "0" + set_sec.toString()
                    } else {
                        set_sec_text = set_sec.toString()
                    }
                    mode_text.text = set_min.toString() + ":" + set_sec_text
                    cercled_progress_bar.setMax(timer_max_sec)
                    alertDialog.hide()
                }

                val cancel_button = alertDialog.findViewById(R.id.cancel_button) as Button

                cancel_button.setOnClickListener{
                    alertDialog.hide()
                }


            }
            else{
                Toast.makeText(applicationContext,"Work time cannot be changed while timer is running.",Toast.LENGTH_SHORT).show()
            }

            /*
            val popupMenu: PopupMenu = PopupMenu(this,work_time_settings_button)
            popupMenu.menuInflater.inflate(R.menu.work_time_settings_popup_menu,popupMenu.menu)

            if(!timer_running) {
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_thirty_sec ->
                            timer_max_sec = 30
                        R.id.action_one_min ->
                            timer_max_sec = 60
                        R.id.action_two_min ->
                            timer_max_sec = 120
                        R.id.action_three_min ->
                            timer_max_sec = 180
                        R.id.action_four_min ->
                            timer_max_sec = 240
                        R.id.action_five_min ->
                            timer_max_sec = 300
                        R.id.action_six_min ->
                            timer_max_sec = 360
                        R.id.action_seven_min ->
                            timer_max_sec = 420
                        R.id.action_eight_min ->
                            timer_max_sec = 480
                        R.id.action_nine_min ->
                            timer_max_sec = 540
                        R.id.action_ten_min ->
                            timer_max_sec = 600
                    }
                    mode_text.text = item.title
                    cercled_progress_bar.setMax(timer_max_sec)
                    true
                })
                popupMenu.show()
            }
            else{
                Toast.makeText(applicationContext,"Work time cannot be changed while timer is running.",Toast.LENGTH_SHORT).show()
            }*/
        }


        break_time_settings_button.setOnClickListener {
            if(!timer_running) {
                val dialogBuilder = AlertDialog.Builder(this)
                val inflater = this.layoutInflater
                val dialogView = inflater.inflate(R.layout.number_picker_dialog, null)
                dialogBuilder.setView(dialogView)
                val alertDialog = dialogBuilder.create()

                alertDialog.show()
                var set_min = break_time_max_sec / 60
                var set_sec = break_time_max_sec % 60

                val np1 = alertDialog.findViewById(R.id.numberPicker1) as NumberPicker
                val nums = arrayOfNulls<String>(60)
                for (i in nums.indices)
                    nums[i] = Integer.toString(i)
                np1.setMaxValue(60)
                np1.setMinValue(0)
                np1.setWrapSelectorWheel(false)
                np1.setDisplayedValues(nums)
                np1.setValue(set_min)

                np1.setOnValueChangedListener { _, _, newVal ->
                    set_min = newVal
                }


                val np2 = alertDialog.findViewById(R.id.numberPicker2) as NumberPicker
                var nums2 = arrayOfNulls<String>(59)
                for (i in nums2.indices)
                    nums2[i] = Integer.toString(i)
                np2.setMaxValue(59)
                np2.setMinValue(0)
                np2.setWrapSelectorWheel(false)
                np2.setDisplayedValues(nums2)
                np2.setValue(set_sec)

                np2.setOnValueChangedListener { _, _, newVal ->
                    set_sec = newVal
                }

                val set_time_button = alertDialog.findViewById(R.id.set_time_button) as Button

                set_time_button.setOnClickListener{
                    //Toast.makeText(applicationContext,set_min.toString() + ":" + set_sec.toString(),Toast.LENGTH_SHORT).show()
                    break_time_max_sec = (set_min * 60) + set_sec

                    var set_sec_text = ""
                    if(set_sec < 10){
                        set_sec_text = "0" + set_sec.toString()
                    } else {
                        set_sec_text = set_sec.toString()
                    }
                    break_cercled_progress_bar.setMax(break_time_max_sec)
                    alertDialog.hide()
                }

                val cancel_button = alertDialog.findViewById(R.id.cancel_button) as Button

                cancel_button.setOnClickListener{
                    alertDialog.hide()
                }


            }
            else{
                Toast.makeText(applicationContext,"Break time cannot be changed while timer is running.",Toast.LENGTH_SHORT).show()
            }
        }


    }

    fun time_formatter(time_in_sec: Int): String {
        var min = ""
        var sec = ""
        if(time_in_sec / 60 < 10){
            min = "0${time_in_sec / 60}"
        }else{
            min = "${time_in_sec / 60}"
        }
        if(time_in_sec % 60 < 10){
            sec = "0${time_in_sec % 60}"
        }else{
            sec = "${time_in_sec % 60}"
        }
        return "$min:$sec"
    }
}
