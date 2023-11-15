package edu.du.rossweek9inputdialog

import android.app.Dialog
import android.content.Context
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
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {

    private var list = ContactList(ArrayList())
    private var adapter: NameAdapter? = null
    private var useDrawer = false
    private lateinit var selectedDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadListFromFile()

        adapter = NameAdapter(list.contacts) { contact: Contact ->
            if (useDrawer) {
                showDrawer(contact)
            } else {
                showDialog(contact)
            }
        }

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { _ ->
            if (useDrawer) {
                showDrawer(null)
            } else {
                showDialog(null)
            }
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


    private fun showDrawer(contact: Contact?){
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val layout = LayoutInflater.from(this).inflate(R.layout.view_dialog, null)
        bottomSheetDialog.setContentView(layout)

        connectViews(layout, contact, bottomSheetDialog)

        bottomSheetDialog.show()
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
                list.contacts.add(Contact(newName, newDate))
            } else {
                contact.name = newName
                contact.date = newDate
            }
            saveListToFile()
            adapter?.notifyDataSetChanged()
            dialog.hide()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_input -> {
                useDrawer = !useDrawer
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveListToFile() {
        applicationContext.openFileOutput("output.json", Context.MODE_PRIVATE).use {
            it.write(getStringForList(list).toByteArray())
        }
    }

    private fun loadListFromFile() {
        try {
            val fileInputStream = applicationContext.openFileInput("output.json")
            val inStream = BufferedReader(InputStreamReader(fileInputStream))
            list = getListForString(inStream.readLine())
        } catch (e: FileNotFoundException){

        }
    }
    private fun getStringForList(list: ContactList): String {
        val gson = Gson()
        val jsonString = gson.toJson(list)

        return jsonString
    }

    private fun getListForString(saved: String): ContactList {
        val gson = Gson()
        list = gson.fromJson(saved, ContactList::class.java)

        return list
    }
}