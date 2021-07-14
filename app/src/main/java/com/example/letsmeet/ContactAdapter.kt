package com.example.letsmeet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
class ContactAdapter(private val myDataset:List<Contacts>) :
    RecyclerView.Adapter<MyViewHolder2>() {



    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder2 {
        // create a new view
        val v = LayoutInflater.from(parent.context)

        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder2(v,parent)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // holder.textView.text = myDataset[position]
        val contacts: Contacts=myDataset[position]
        holder.bind(contacts)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}
