@file:Suppress("DEPRECATION")

package com.example.letsmeet.addevent
import android.annotation.SuppressLint
import android.content.Intent
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.letsmeet.R
import com.example.letsmeet.viewevent.ViewEventActivity

class EventAdapter(private val SenDataset:ArrayList<EventStruct>):
    RecyclerView.Adapter<EventViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): EventViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)

        return EventViewHolder(v,parent)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val sentevents: EventStruct=SenDataset[position]
       
        holder.bind(sentevents)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = SenDataset.size
}

/*
*
* Viewholder starts here
*
*
* */
class EventViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.events_list, parent, false))
{


    // private var pic: ImageView? = null
    private var sen_title: TextView? = null
  //  private var to_from: TextView? = null
  //  private var status: TextView? = null
  private var status_icon: ImageView? = null
    private var sen_del_date_time: TextView? = null
    private var sen_cardview: CardView?=null
    private var layout: View?=null

    init {
        sen_title = itemView.findViewById(R.id.to_title)
      //  to_from = itemView.findViewById(R.id.to_from)
      //  status = itemView.findViewById(R.id.status)
        status_icon=itemView.findViewById(R.id.status_icon)

        layout=itemView.findViewById(R.id.back_layout)
        sen_del_date_time = itemView.findViewById(R.id.to_delivered_date_time)
       sen_cardview=itemView.findViewById(R.id.to_event_card)

    }


    @SuppressLint("SetTextI18n")
    fun bind(sentevents: EventStruct) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(sen_title!!.context)
        val name = sharedPreferences.getString("Name", "")!!
        sen_title?.text =sentevents.Etitle

        val timestramp=sentevents.Ecreated_at
        val timearray=timestramp.split(" ")
        val date=timearray[0]
        val minitime=timearray[1]
        val microtime=minitime.split(":")
        val time=microtime[0]+":"+microtime[1]

        sen_del_date_time?.text="$date $time"
        if(sentevents.Estatus=="Added"){
            layout!!.setBackgroundResource(R.color.greenCard)
            status_icon!!.setImageResource(R.drawable.added)}

        if(sentevents.Estatus=="Accepted"){
            layout!!.setBackgroundResource(R.drawable.acc_grad)
            status_icon!!.setImageResource(R.drawable.accepted)}

        if(sentevents.Estatus=="Requested"){
            layout!!.setBackgroundResource(R.drawable.req_grad)

            status_icon!!.setImageResource(R.drawable.requested)}

        if(sentevents.Estatus=="Rescheduled"){
            layout!!.setBackgroundResource(R.drawable.res_grad)
            status_icon!!.setImageResource(R.drawable.rescheduled)}

        if(sentevents.Estatus=="Completed"){
            layout!!.setBackgroundResource(R.color.lightCard)
            status_icon!!.setImageResource(R.drawable.completed)}

        if(sentevents.Estatus=="Removed"){
            layout!!.setBackgroundResource(R.drawable.gradient_red)
            status_icon!!.setImageResource(R.drawable.removed)}


        sen_cardview?.setOnClickListener{
               var  intent = Intent(itemView.context, ViewEventActivity::class.java)
            intent.putExtra("title",sentevents.Etitle)
            intent.putExtra("from",sentevents.Efrom)
            intent.putExtra("to",sentevents.Eto)
            intent.putExtra("location",sentevents.Elocation)
            intent.putExtra("duration",sentevents.Eduration)
            intent.putExtra("date",sentevents.Edate)
            intent.putExtra("time",sentevents.Etime)
            intent.putExtra("status",sentevents.Estatus)
            intent.putExtra("type",sentevents.Etype)
            intent.putExtra("details",sentevents.Edetails)
            intent.putExtra("id",sentevents.Eid)
            startActivity(itemView.context,intent,null)

        }


    }



}