@file:Suppress("DEPRECATION")

package com.example.letsmeet.addevent

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.letsmeet.MainActivity
import com.example.letsmeet.R
import com.example.letsmeet.map.MapActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class AddEvent : AppCompatActivity() {
    private var et_title:EditText?=null
    private var et_details:EditText?=null
    private var et_location:EditText?=null
    private var et_hours:EditText?=null
    private var et_mins:EditText?=null
    private var et_date:EditText?=null
    private var et_time:EditText?=null
    private var bt_submit:Button?=null
    private var bt_check:Button?=null
    private var PickTimeBtn:ImageButton?=null
    private var PickDateBtn:ImageButton?=null
    private var LocationPicker:ImageButton?=null
    private var eto:String=""

    private var locationInfo:String=""
    var cal = Calendar.getInstance()




    override fun onCreate(savedInstanceState: Bundle?)

    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        supportActionBar?.hide()
        declarevariables()
        NewRequest()
        checkRescheduleRequest()
        dateSetListener()
        timepicker()
        getLocation()
      //  picklocation()

    }

    fun declarevariables()
    {
        bt_submit = findViewById(R.id.ev_submit)
        bt_check = findViewById(R.id.chk_btn)
        PickDateBtn = findViewById(R.id.date_picker)
        PickTimeBtn = findViewById(R.id.time_picker)
        LocationPicker = findViewById(R.id.location_picker)
        et_title = findViewById(R.id.ev_title)
        et_details = findViewById(R.id.ev_details)
        et_location = findViewById(R.id.ev_location)
        et_hours = findViewById(R.id.ev_Hours)
        et_mins = findViewById(R.id.ev_mins)
        et_date = findViewById(R.id.ev_date)
        et_time = findViewById(R.id.ev_time)

    }



    fun NewRequest() {

        val isNew = intent.getBooleanExtra("isNew",false)
        if(isNew) {
            showToast("it is new request")
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val user_id = sharedPreferences.getString("Name", "")!!
              Log.e("addshereeee",user_id)
            eto = intent.getStringExtra("contact")!!
            bt_check?.setOnClickListener { getscheduledetails(eto) }
            bt_submit?.setOnClickListener {
                if (user_id.isNotEmpty()) {
                    Addeventdetails(user_id, eto)
                } else {
                    Toast.makeText(applicationContext, "Plz Login First", Toast.LENGTH_LONG).show()
                }
            }
            LocationPicker!!.setOnClickListener {
                showToast("it is from new request")
                intent = Intent(this, MapActivity::class.java)
                intent.putExtra("isfromAddevent", true)
                intent.putExtra("locationFor", eto)
                 startActivity(intent)

            }
        }

    }
    private fun Addeventdetails(from:String,to:String)
    {

        var etitle=et_title?.text.toString()
        var edetails=et_details?.text.toString()
        var elocation=locationInfo
        var edate=et_date?.text.toString()
        var eduration=et_hours?.text.toString()+"Hrs."+et_mins?.text.toString()+"Mins"
        var etime=et_time?.text.toString()


        ApiService.loginApiCall().AddEvent(etitle,edetails,elocation,etime,"(Ind)","Added",edate, from,to,eduration)
                .enqueue(object : Callback<EventResponse> {
                    override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                        Log.e("AddResponse::::", response.body().toString())
                        var EventAddResponse = response.body()!!
                        if (EventAddResponse.status) {
                            Toast.makeText(applicationContext, EventAddResponse.message, Toast.LENGTH_LONG).show()

                            var i = Intent(applicationContext, MainActivity::class.java)
                            startActivity(i)

                        }else{
                            Toast.makeText(applicationContext, EventAddResponse.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        Log.e("Add failure",t.message)


                    }

                })
        }

    private fun getscheduledetails(name:String) {
        var date = findViewById<EditText>(R.id.ev_date).text.toString()
        if (date.isEmpty()) {
            Toast.makeText(this, "Enter Date First", Toast.LENGTH_LONG).show()
        } else {
            ApiService.loginApiCall().getschedule(name, date)
                .enqueue(object : Callback<ExtraResponse> {
                    override fun onResponse(call: Call<ExtraResponse>, response: Response<ExtraResponse>) {
                        Log.e("getscheduleResponse::::", response.body().toString())
                        if (response.body()!!.status) {
                            var scheduleList = response.body()!!.data
                           var intt= scheduleList.size

                           var buffer= StringBuffer()


                             for (x in 0 until intt)
                             {
                                // for (list in scheduleList)
                                 buffer.append("Time:"+scheduleList[x].Etime)
                                 buffer.append(" Duration:"+scheduleList[x].Eduration+"\r\n")

                            }
                            showMessage("$intt Events Found", buffer.toString())

                        } else {
                            showMessage("Schedule","You Can Add")
                             }


                    }

                    override fun onFailure(call: Call<ExtraResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        Log.e("getschedule failure", t.message)


                    }

                })

        }
    }

    fun showMessage(title: String?, Message: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(Message)
        builder.show()
    }

    private fun rescheduleEventDetails(from:String,to:String,Eid:Int)
    {
        var etitle=et_title?.text.toString()
        var edetails=et_details?.text.toString()
        var elocation=et_location?.text.toString()
        var edate=et_date?.text.toString()
        var eduration=et_hours?.text.toString()+"Hrs."+et_mins?.text.toString()+"Mins"
        var etime=et_time?.text.toString()


        ApiService.loginApiCall().rescheduleEvent(Eid,etitle,edetails,locationInfo,etime,"(Ind)","Rescheduled",edate, from!!,to,eduration)
            .enqueue(object : Callback<EventResponse> {
                override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                    Log.e("RescheduleResponse::::", response.body().toString())
                    var EventAddResponse = response.body()!!
                    if (EventAddResponse.status) {
                        Toast.makeText(applicationContext, EventAddResponse.message, Toast.LENGTH_LONG).show()
                        var i = Intent(applicationContext, MainActivity::class.java)
                        startActivity(i)

                    }else{
                        Toast.makeText(applicationContext, EventAddResponse.message, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    Log.e("reschedule failure",t.message)


                }

            })
    }

private fun dateSetListener()
{
    val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
    // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
    PickDateBtn!!.setOnClickListener {
        DatePickerDialog(this, dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()
    }




}
    @SuppressLint("SimpleDateFormat")
   private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat)
        et_date!!.setText(sdf.format(cal.time))}

    @SuppressLint("SetTextI18n")
    fun timepicker()
    {
        PickTimeBtn?.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR)
            val minute = c.get(Calendar.MINUTE)
            var am_pm=""
            if (c.get(Calendar.AM_PM) == Calendar.AM)
                am_pm = "AM"
            else if (c.get(Calendar.AM_PM) == Calendar.PM)
                am_pm = "PM"

            val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { view, h, m ->

                et_time?.setText("$h:$m:$am_pm")
            }),hour,minute,true)

            tpd.show()
        }
    }

fun getLocation()
{
    val isLocation=intent.getBooleanExtra("islocation",false)
    if(isLocation)
    {
        val plName=intent.getStringExtra("placeName")
        val plAdd=intent.getStringExtra("placeAddress")
        val plLong=intent.getStringExtra("long")
        val plLatt=intent.getStringExtra("latt")
        val plAltt=intent.getStringExtra("altt")

        et_location?.setText("$plName,$plAdd")
        locationInfo="$plName,$plAdd,$plLong,$plLatt,$plAltt"
    }

}

   /* fun picklocation() {
        LocationPicker!!.setOnClickListener {
            intent = Intent(this, MapActivity::class.java)
            intent.putExtra("isfromAddevent", true)
            intent.putExtra("locationFor", eto)
            if(intent.getBooleanExtra("isSchedule",false))
            {
                intent.putExtra("isFromReschedule", true)
                Toast.makeText(this,"its from reschedule",Toast.LENGTH_SHORT).show()
            }
            startActivity(intent)

        }
    }*/

    fun checkRescheduleRequest()
    {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val user_id = sharedPreferences.getString("Name", "")!!
        if(intent.getBooleanExtra("isSchedule",false)&&sharedPreferences.contains("Title"))
        {
            showToast("its reschedule request")

            et_title?.setText(sharedPreferences.getString("Title",""))
            et_details?.setText(sharedPreferences.getString("Details",""))
            et_location?.setText(sharedPreferences.getString("Location",""))
            et_date?.setText(sharedPreferences.getString("Date",""))
            et_time?.setText(sharedPreferences.getString("Time",""))
            bt_submit?.text="Reschedule"
            var eventId=sharedPreferences.getInt("Sid",0)
             eto=sharedPreferences.getString("To","")!!
            bt_check?.setOnClickListener{getscheduledetails(eto)}

            bt_submit?.setOnClickListener{
                if(eventId!=0||user_id=="")
                {
                    rescheduleEventDetails(user_id,eto,eventId) }
                else{Toast.makeText(applicationContext,"UserId or EventId Error",Toast.LENGTH_SHORT).show()}

            }

            LocationPicker!!.setOnClickListener {
                intent = Intent(this, MapActivity::class.java)
                intent.putExtra("locationFor", eto)
                intent.putExtra("isFromReschedule", true)
                Toast.makeText(this,"its from reschedule",Toast.LENGTH_SHORT).show()
                startActivity(intent)

            }


        }
    }
fun showToast(msg:String)
{
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()

}

}
