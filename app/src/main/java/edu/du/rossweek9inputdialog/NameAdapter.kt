package edu.du.rossweek9inputdialog

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NameAdapter(contacts: ArrayList<Contact>, listener: (Contact) -> Unit) :
    RecyclerView.Adapter<NameAdapter.ContactViewHolder>() {

    private val items: ArrayList<Contact> = contacts
    val listener = listener

    inner class ContactViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        fun bind(contact: Contact){
            val formatter = SimpleDateFormat("d MMM, yyyy")
            val newDateText = formatter.format(contact.date)
            itemView.findViewById<TextView>(R.id.event_name).text = contact.name
            itemView.findViewById<TextView>(R.id.event_date).text = newDateText

            itemView.setOnClickListener{ listener.invoke(contact)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_contact, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(items[position])
    }
}