@file:Suppress("DEPRECATION")

package com.example.letsmeet

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.letsmeet.addevent.ApiService
import com.example.letsmeet.addevent.EventAdapter
import com.example.letsmeet.addevent.EventResponse
import com.example.letsmeet.addevent.EventStruct
import com.example.letsmeet.startup.Detail

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ScheduleFragment : Fragment() {

    lateinit var rootview: View
    var EventsList:ArrayList<EventStruct>? = null
    var eventrecyclerview:RecyclerView?=null
    private var Arrowbtn: Button?=null
    private var Viewallbtn: Button?=null
    private var CalencerCard: CardView?=null
    private var calenderview:com.applandeo.materialcalendarview.CalendarView?=null
    private var tv_noevents: TextView?=null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
         rootview=inflater.inflate(R.layout.fragment_schedule, container, false)
         eventrecyclerview = rootview.findViewById(R.id.schedule_recycler_view) as RecyclerView
        eventrecyclerview!!.layoutManager = LinearLayoutManager(activity)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(rootview.context)
        val user_id = sharedPreferences.getString("Name", "")!!
        Log.e("pkshare",user_id)
        checkCalView()

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ApiService.loginApiCall().GetEventList(user_id).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>,response: Response<EventResponse>)
            {
                if(response.body()!!.status) {
                    EventsList = response.body()!!.data
                    initMatCalender()
                    val EventCounts = response.body()!!.detail
                    addShare(EventCounts)
                    eventrecyclerview!!.adapter = EventAdapter(EventsList!!)
                }
                Log.e("pkSentResponse::::", response.body().toString())
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Toast.makeText(rootview.context, t.message, Toast.LENGTH_LONG).show()
                Log.e("pkonfailure", t.message)


            }

        })

        return rootview

    }

    fun addShare(events: Detail)
    {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(rootview.context)

        val editor = sharedPreferences.edit()
        //2
        editor.putInt("Added", events.Added)
        editor.putInt("Accepted", events.Accepted)
        editor.putInt("Removed", events.Removed)
        editor.putInt("Requested", events.Requested)
        editor.putInt("Completed", events.Completed)
        editor.putInt("Rescheduled", events.Rescheduled)
        editor.putInt("Total", events.Added+events.Removed+events.Requested+ events.Rescheduled+ events.Completed+events.Accepted)
        editor.apply()

    }




    private fun showToast(messege:String)
    {
        Toast.makeText(rootview.context,messege,Toast.LENGTH_SHORT).show()
    }

    private fun checkCalView()
    {
        tv_noevents=rootview.findViewById(R.id.no_events)
        Arrowbtn=rootview.findViewById(R.id.calBtn)
        CalencerCard=rootview.findViewById(R.id.calenderCard)
        Viewallbtn=rootview.findViewById(R.id.viewAll)
        Viewallbtn!!.setOnClickListener{
            calenderview!!.visibility = View.GONE
            tv_noevents!!.visibility=View.GONE
            eventrecyclerview!!.adapter = EventAdapter(EventsList!!)

        }

        Arrowbtn?.setOnClickListener {
            if (calenderview?.visibility ==View.GONE) {
                TransitionManager.beginDelayedTransition(CalencerCard!!, AutoTransition())
                calenderview!!.visibility = View.VISIBLE
                Arrowbtn?.setBackgroundResource(R.drawable.arrow_up)
            } else {
                TransitionManager.beginDelayedTransition(CalencerCard!!, AutoTransition())
                calenderview!!.visibility = View.GONE
                Arrowbtn?.setBackgroundResource(R.drawable.down_arrow)
            }
        }

    }
fun initMatCalender()
{
    calenderview=rootview.findViewById(R.id.calenderView)

    calenderview!!.setDate(Date())
    calenderview!!.setOnDayClickListener(object : OnDayClickListener {

        override fun onDayClick(eventDay: EventDay) {
            val clickedDayCalendar = eventDay.calendar
            val Format = "dd/MM/yyyy"
            val sdf: DateFormat = SimpleDateFormat(Format)
            val date = sdf.format(clickedDayCalendar.time)

            Toast.makeText(rootview.context, date, Toast.LENGTH_SHORT).show()


            val list= arrayListOf<EventStruct>()

            for(event in EventsList!!)
            {
                if(event.Edate==date)
                {
                    list.add(event) }
            }
            if(list.size==0)
                tv_noevents!!.visibility=View.VISIBLE

            else
                tv_noevents!!.visibility=View.GONE

            eventrecyclerview!!.adapter = EventAdapter(list)

        } })


}

}


