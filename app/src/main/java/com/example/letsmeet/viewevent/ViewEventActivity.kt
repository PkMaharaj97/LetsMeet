@file:Suppress("DEPRECATION")

package com.example.letsmeet.viewevent

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.letsmeet.MainActivity
import com.example.letsmeet.R
import com.example.letsmeet.addevent.AddEvent
import com.example.letsmeet.addevent.ApiService
import com.example.letsmeet.addevent.EventResponse
import com.example.letsmeet.map.MapActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ViewEventActivity : AppCompatActivity() {
    private var tv_title:TextView?=null
    private var tv_from:TextView?=null
    private var tv_to:TextView?=null
    private var tv_location:TextView?=null
    private var tv_address:TextView?=null

    private var tv_duration:TextView?=null
    private var tv_status:TextView?=null
   // private var tv_type:TextView?=null
    private var tv_date:TextView?=null
    private var tv_time:TextView?=null
    private var tv_details:TextView?=null
    private var locationBtn:CardView?=null

    private var Eid:Int=0
    private var long:Double=0.0
    private var latt:Double=0.0
    private var altt:Double=0.0
    private var place:String=""
    private var address:String=""

    private var arrobtn: Button?=null
    private var EventCard:CardView?=null
    private lateinit var infoView:View
    private var historyadapter: HistoryAdapter?=null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)
          declarevariables()

        initvariables()
        goToLocation()


    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var from=intent.getStringExtra("from")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val user_id = sharedPreferences.getString("Name", "")!!

        val inflater = menuInflater
        if(from.equals(user_id,false))
        {
        inflater.inflate(R.menu.sentview_menu, menu)}
        else
        { inflater.inflate(R.menu.receivedview_menu, menu)}

        return true
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.markAsCompleted -> {
            val msg="Completed"
            changeStatus(msg)
            true
        }
        R.id.markAsAccepted -> {
            val msg="Accepted"
            changeStatus(msg)
            true
        }

        R.id.reschedule -> {
            val intent= Intent(this, AddEvent::class.java)
            intent.putExtra("isSchedule",true)
            startActivity(intent)
            true
        }
        R.id.remove-> {
            val msg="Removed"
            changeStatus(msg)
            true
        }
        R.id.reschedule_request -> {
            val msg="Requested"
            changeStatus(msg)
            true
        }
        R.id.view_history -> {
            viewHistory(Eid)
              true
        }


        else -> super.onOptionsItemSelected(item)
    }




    fun declarevariables()
   {tv_title=findViewById(R.id.tv1_title)
       tv_from=findViewById(R.id.tv1_from)
       tv_to=findViewById(R.id.tv1_to)
       tv_location=findViewById(R.id.tv1_location)
       tv_address=findViewById(R.id.tv1_location2)
       tv_duration=findViewById(R.id.tv1_duration)
       tv_status=findViewById(R.id.tv1_status)
      // tv_type=findViewById(R.id.tv1_type)
       tv_date=findViewById(R.id.tv1_date)
       tv_time=findViewById(R.id.tv1_time)
       tv_details=findViewById(R.id.tv1_details)
       locationBtn=findViewById(R.id.location_tracker)
       EventCard=findViewById(R.id.EventCard)
       arrobtn=findViewById(R.id.arrowBtn)
       infoView=findViewById(R.id.InfoView)
       checkInfoView()
   }

fun initvariables()
{
    var Etitle= intent.getStringExtra("title")!!
    tv_title?.text=Etitle
    var Eto=intent.getStringExtra("to")!!
    tv_to?.text=Eto
    var Efrom=intent.getStringExtra("from")!!
    tv_from?.text=Efrom
    var Elocation=intent.getStringExtra("location")!!
    getPlace(Elocation)

    tv_location?.text=place
    tv_address?.text=address
    var Eduration=intent.getStringExtra("duration")!!
    tv_duration?.text=Eduration
    var Edate=intent.getStringExtra("date")!!
    tv_date?.text=Edate
    var Etime=intent.getStringExtra("time")!!
    tv_time?.text=Etime
    var Etype=intent.getStringExtra("type")!!
    //tv_type?.text=Etype
    tv_status?.text=intent.getStringExtra("status")!!
   var Edetails=intent.getStringExtra("details")!!
    tv_details?.text=Edetails
    Eid=intent.getIntExtra("id",0)
    val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)


    val editor = sharedPreferences.edit()
    //2
    editor.putString("Title",Etitle)
    editor.putString("From",Efrom)
    editor.putString("To",Eto)
    editor.putString("Location",Elocation)
    editor.putString("Duration",Eduration)
    editor.putString("Date",Edate)
    editor.putString("Time",Etime)
    editor.putString("Type",Etime)
    editor.putString("Details",Edetails)
    editor.putInt("Sid",Eid)
    editor.apply()
}

    fun changeStatus(status:String)
    {
        ApiService.loginApiCall().changeststus(Eid,status)
            .enqueue(object : Callback<EventResponse> {
                override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                    Log.e("pkResponse::::", response.body().toString())
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
                    Log.e("pkonfailure",t.message!!)


                }

            })
    }

fun getPlace(sample:String)
{
    val locArray=sample.split(",")
     place=locArray[0]
    address=locArray[1]
    long=locArray[2].toDouble()
    latt=locArray[3].toDouble()
    altt=locArray[4].toDouble()

}
fun goToLocation()
{
    locationBtn!!.setOnClickListener{
        intent = Intent(this, MapActivity::class.java)
        intent.putExtra("isFromViewEvent",true)
        intent.putExtra("Place",place)
        intent.putExtra("Address",address)
        intent.putExtra("Long",long)
        intent.putExtra("Latt",latt)
        intent.putExtra("Altt",altt)
        startActivity(intent)
    }
}

   private fun checkInfoView()
    {
        arrobtn?.setOnClickListener {
            if (infoView.visibility ==View.GONE) {
                TransitionManager.beginDelayedTransition(EventCard!!, AutoTransition())
                infoView.visibility = View.VISIBLE
                arrobtn?.setBackgroundResource(R.drawable.arrow_up)
            } else {
                TransitionManager.beginDelayedTransition(EventCard!!, AutoTransition())
                infoView.visibility = View.GONE
                arrobtn?.setBackgroundResource(R.drawable.down_arrow)
            }
        }

    }

    private fun viewHistory(id:Int)
    {


        ApiService.loginApiCall().getHistory(id).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(call: Call<HistoryResponse>,response: Response<HistoryResponse>)
            {
                val historyResponse=response.body()!!
                if(historyResponse.status) {
                  val  historylist = response.body()!!.data
                        val list= arrayListOf<String>()

                    for (history in historylist)
                    {
                        val status=history.Status
                        val timestramp=history.Changed_at
                        val timearray=timestramp.split(" ")
                        val date=timearray[0]
                        val minitime=timearray[1]
                        val microtime=minitime.split(":")
                        val time=microtime[0]+":"+microtime[1]
                        val msg=" $status At $date $time"

                        list.add(msg)
                    }
                    ShowWindow(list)
                }
                else
                    Toast.makeText(applicationContext, historyResponse.message, Toast.LENGTH_LONG).show()

            }
            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                Log.e("pkonfailure", t.message!!)
            } })
    }

fun ShowWindow(historylist:ArrayList<String>)
{
    val popview: View = LayoutInflater.from(this).inflate(R.layout.history_window,null)
    val popupwindow = PopupWindow(popview, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,true)
    val back:Button=popview.findViewById(R.id.close)
    back.setOnClickListener{popupwindow.dismiss()}

    val historylistview: ListView =popview.findViewById(R.id.historyListView)
   val historyadapter  =ArrayAdapter(popview.context,android.R.layout.simple_list_item_1,historylist)
    historylistview.adapter=historyadapter
    popupwindow.showAsDropDown(popview, 80, 300)

}


}
