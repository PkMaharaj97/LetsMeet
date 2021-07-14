
package com.example.letsmeet

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.letsmeet.addevent.AddEvent


class MyViewHolder2(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.contact_list, parent, false))
{


    private var pic: ImageView? = null
    private var name: TextView? = null
    private var cardview: CardView?=null


    init {
        name = itemView.findViewById(R.id.cname)
        pic = itemView.findViewById(R.id.cimage)
        cardview=itemView.findViewById(R.id.info_card)

          }


    fun bind(contacts: Contacts) {
        name?.text = contacts.Name
        pic?.setImageResource(contacts.Pic)
        cardview?.setOnClickListener{
            //  Toast.makeText(itemView.context,"its pressed",Toast.LENGTH_LONG).show()

            getpopinfo(itemView,contacts)
        }


    }



    fun getpopinfo(view:View, contacts:Contacts)
    {
        var popview: View =LayoutInflater.from(view.context).inflate(R.layout.contact_info,null)
        val popupwindow = PopupWindow(popview, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,true)
        var back:Button=popview.findViewById(R.id.close)
        var add:Button=popview.findViewById(R.id.add_meebtn)
        popview.findViewById<TextView>(R.id.info_name).text=contacts.Name
        // name.text=contacts.Name
        var email= popview.findViewById<TextView>(R.id.info_email)
        email.text=contacts.Email
        var cell= popview.findViewById<TextView>(R.id.info_phone)
        cell.text=contacts.Phone
        var loc= popview.findViewById<TextView>(R.id.info_location)
        loc.text=contacts.Location
        var pic= popview.findViewById<ImageView>(R.id.info_image)
        pic.setImageResource(contacts.Pic)
        back.setOnClickListener{popupwindow.dismiss()}
        add.setOnClickListener{
            val intent= Intent(view.context,AddEvent::class.java)
            intent.putExtra("contact",contacts.Name)
            intent.putExtra("isNew",true)
            startActivity(view.context,intent,null)
            popupwindow.dismiss()

        }
        popupwindow.showAsDropDown(popview, 80, 300)
    }

}


