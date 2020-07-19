package com.example.tdm2_td01_exo5.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tdm2_td01_exo5.R
import com.example.tdm2_td01_exo5.adapter.ContactAdapter
import com.example.tdm2_td01_exo5.data.entity.Contact
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    private lateinit var viewModel: ContactViewModel
    private lateinit var viewModelFactory: ContactViewModelFactory
    private lateinit var contact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkPermission(Manifest.permission.READ_CONTACTS)) {

            //set recycle view adapter
            val recyclerView: RecyclerView = recycler_view as RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.setHasFixedSize(true)

            val adapter = ContactAdapter()
            recyclerView.adapter = adapter

            //set ViewModel
            viewModelFactory = ContactViewModelFactory(this.application)
            viewModel =
                ViewModelProviders.of(this, viewModelFactory).get(ContactViewModel::class.java)
            adapter.setContacts(viewModel.getAllContact())

            //send sms on click
            adapter.setOnItemClickListener(object : ContactAdapter.OnItemClickListener {
                override fun onItemClick(contact: Contact?) {
                    this@MainActivity.contact = contact!!
                    showDialog()
                }
            })

        }

    }

    private fun checkPermission(permission: String): Boolean {
        val check = ContextCompat.checkSelfPermission(this, permission)
        return (check == PackageManager.PERMISSION_GRANTED)
    }

    private fun sendSms(message : String) {
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                contact.number,
                null,
                message,
                null,
                null
            )
            Toast.makeText(
                this@MainActivity,
                "SMS send to ${contact?.name}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@MainActivity,
                "You don't have permission to send SMS",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alertdialog_custom_view, null)
        builder.setCancelable(false)
        builder.setView(dialogView)

        val btnPositive: Button = dialogView.findViewById(R.id.dialog_positive_btn) as Button
        val btnNegative: Button = dialogView.findViewById(R.id.dialog_negative_btn) as Button
        val messageEdit = dialogView.findViewById(R.id.message) as EditText
        val dialog: AlertDialog = builder.create()

        btnPositive.setOnClickListener {

            if(messageEdit.text.isEmpty()){
                Toast.makeText(this,"your message is empty",Toast.LENGTH_SHORT).show()
            }else{
                dialog.cancel()
                sendSms(messageEdit.text.toString())
            }
        }

        btnNegative.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


}
