package com.zaich.getactivitywithlocation

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context):SQLiteOpenHelper
    (context,Database_Name, null,Database_Version){

        companion object{
            private val Database_Version = 1
            private val Database_Name = "AbsenDatabase"

            private val Table_Contacts =  "EmployeeTable"

            private val key_id = "id"
            private val key_name = "name"
            private val key_waktu = "waktu"
            private val key_lokasi = "lokasi"
        }

    override fun onCreate(db: SQLiteDatabase?) {
        val Create_Contact_Table = ("Create Table " + Table_Contacts + "("
                + key_id + " Integer Primary Key, "
                + key_name + " Text, "
                + key_waktu + " Timestamp Default Current_Timestamp, "
                + key_lokasi + " Text) ")
        db?.execSQL(Create_Contact_Table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop table if exists $Table_Contacts")
        onCreate(db)
    }

    fun addAbsen(absen:AbsenModel):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(key_name,absen.name)
        contentValues.put(key_waktu,absen.waktu)
        contentValues.put(key_lokasi,absen.lokasi)

        val success = db.insert(Table_Contacts,null,contentValues)
        db.close()
        return success
    }
    fun viewAbsen():ArrayList<AbsenModel>{
        val absenList :ArrayList<AbsenModel> = ArrayList<AbsenModel>()
        val selectQuery = "Select * from $Table_Contacts"

        val db = this.readableDatabase
        var cursor:Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e:SQLException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name : String
        var time : String
        var address : String

        if (cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex(key_id))
                name = cursor.getString(cursor.getColumnIndex(key_name))
                time = cursor.getString(cursor.getColumnIndex(key_waktu))
                address = cursor.getString(cursor.getColumnIndex(key_lokasi))
                val absen = AbsenModel(id,name,time,address)
                absenList.add(absen)
            }while (cursor.moveToNext())
        }
        return absenList
    }
    fun deleteEmployee(absen: AbsenModel): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(key_id, absen.id)

        val success = db.delete(Table_Contacts, key_id + "=" + absen.id, null)
        db.close()
        return success
    }
}