package edu.du.rossweek9inputdialog

import android.app.Dialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private val list = ArrayList<Contact>()
    private var adapter: NameAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = NameAdapter(list) { contact: Contact ->
            showDialog(contact)
        }

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { _ ->
            showDialog(null)
        }

    }
    private fun showDialog(contact: Contact?) {
        val dialogBuilder = AlertDialog.Builder(this)
        val layout = LayoutInflater.from(this).inflate(R.layout.view_dialog, null)
        dialogBuilder.setView(layout)
        if(contact != null){
            dialogBuilder.setTitle("Update Event")
        } else {
            dialogBuilder.setTitle("Add Event")
        }
        val dialog: Dialog = dialogBuilder.create()
        connectViews(layout, contact, dialog)

        dialog.show()
    }

    private fun connectViews(layout: View, contact: Contact?, dialog: Dialog) {
        val editName = layout.findViewById<EditText>(R.id.edit_name)
        val editDate = layout.findViewById<CalendarView>(R.id.edit_calendar)
        if (contact != null) {
            editName.text.append(contact.name)
            editDate.date = contact.date
        }

        var selectedDate:Long = editDate.date
        editDate.setOnDateChangeListener(
            OnDateChangeListener{ view, year, month, dayOfMonth ->
                val fixedMonth = month+1
                val dateString = "$dayOfMonth-$fixedMonth-$year"
                val mDate = SimpleDateFormat("dd-MM-yyyy").parse(dateString)
                selectedDate = mDate.time
            }
        )

        layout.findViewById<Button>(R.id.button_save).setOnClickListener {
            val newName = editName.text.toString()
            val newDate = selectedDate

            if(contact == null) {
                list.add(Contact(newName, newDate))
            } else {
                contact.name = newName
                contact.date = newDate
            }
            adapter?.notifyDataSetChanged()
            dialog.hide()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}