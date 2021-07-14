package com.example.letsmeet.viewevent

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.letsmeet.R

class HistoryAdapter(context: Context ,private val m_resultsList: ArrayList<History>) :
    ArrayAdapter<History>(context, R.layout.history_list_item, m_resultsList)
{
    override fun getCount(): Int {
        return m_resultsList.size
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val history = getItem(position)
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.history_list_item, parent, false)
        }
        var tv: TextView? = null


       /*  tv = convertView!!.findViewById(R.id.history_status) as TextView
        tv.text =history.Status
         tv = convertView.findViewById(R.id.history_timestramp) as TextView
        tv.text =history.Changed_at*/

      /*  val status = convertView!!.findViewById(R.id.history_status) as TextView
        val timestramp=convertView!!.findViewById(R.id.history_timestramp) as TextView
        status.text=history.Status
        timestramp.text=history.Changed_at*/
        return convertView!!
    }
    override fun getItem(position: Int): History {
        return m_resultsList[position]
    }


}