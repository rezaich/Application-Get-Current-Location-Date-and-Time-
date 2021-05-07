package com.zaich.getactivitywithlocation

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_history1.*

class history1: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history1)
        setupListOfData()
    }

    private fun getItemList():ArrayList<AbsenModel>{
        val databasehandler:DatabaseHandler = DatabaseHandler(this)
        val absenList : ArrayList<AbsenModel> = databasehandler.viewAbsen()
        return absenList
    }

    private fun setupListOfData(){
        if (getItemList().size > 0){
            rv_data.visibility = View.VISIBLE
            tvNodata.visibility = View.GONE
            rv_data.layoutManager = LinearLayoutManager(this)
            val itemAdapter = ItemAdapter(this, getItemList())
            rv_data.adapter = itemAdapter
        }else {
            rv_data.visibility = View.GONE
            tvNodata.visibility = View.VISIBLE
        }
    }
    fun deleteRecordAlertDialog(absenModel: AbsenModel){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete Record")
        builder.setMessage("Are u sure?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //menampilkan tombol yes
        builder.setPositiveButton("Yes") { dialog: DialogInterface, which: Int ->
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            val status = databaseHandler.deleteEmployee(AbsenModel(absenModel.id,"","",""))
            if (status > -1){
                Toast.makeText(this, "Record deleted successfully", Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
            setupListOfData()
        }
        //menampilkan tombol yes
        builder.setNegativeButton("No") { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        //memastikan user menekan yes no
        alertDialog.setCancelable(false)
        //nampilin kotak dialog
        alertDialog.show()
    }
}