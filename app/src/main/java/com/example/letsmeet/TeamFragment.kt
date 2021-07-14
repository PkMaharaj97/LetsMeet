package com.example.letsmeet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


@Suppress("UNREACHABLE_CODE")
class TeamFragment : Fragment() {
    var user1 = Contacts(R.drawable.img1, "Praveen", "praveen@gmail.com",  "Koppal", "8792543454")
    var user2 = Contacts(R.drawable.img4, "Kiran", "kiran@gmail.com", "Banglore", "9887656543")
    var user3 = Contacts(R.drawable.img3, "pkmaharaj", "pkmahi@gmail.com","Bhagynagar", "7787656543")
    var user4 = Contacts(R.drawable.img4, "Kiran", "praveen@gmail.com","Bhagynagar", "7787656543")


    var contactlist = listOf(user1, user2,user3,user4,user1,user3,user4,user3,user1, user2,user3,user4)



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootview=inflater.inflate(R.layout.fragment_team, container, false)

        var mrecyclerview = rootview.findViewById(R.id.team_recycler_view) as RecyclerView
        mrecyclerview.layoutManager = LinearLayoutManager(activity)
        mrecyclerview.adapter = ContactAdapter(contactlist)


        return rootview


        }

    }





