package com.zaich.getactivitywithlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.text.format.DateFormat.is24HourFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_history1.*
import kotlinx.android.synthetic.main.item_row.*
import java.text.SimpleDateFormat
import java.util.*

class MapsActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var date : String
    private lateinit var time : String
    private lateinit var address : String
    private var currentPosition = LatLng(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getCurrentLocation()
        getTime()
        getAddress()
        btnSet.setOnClickListener {
            addRecord()
            closeKeyboard()
        }

        val histroy = Intent(application,history1 :: class.java)
        btnData.setOnClickListener {
            startActivity(histroy)
        }

    }
    fun closeKeyboard(){
        val view = this.currentFocus
        if (view != null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("RestrictedApi")
    private fun getCurrentLocation(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest()
                .setInterval(5000)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
            )
            return
        }

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        super.onLocationResult(p0)
                        for (location in p0.locations) {
                            mapFragment.getMapAsync {
                                mMap = it

                                if (ActivityCompat.checkSelfPermission(
                                                this@MapsActivity,
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                                this@MapsActivity,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                        ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    return@getMapAsync
                                }
                                mMap.isMyLocationEnabled = true
                                mMap.uiSettings.isZoomControlsEnabled = true
                                mMap.uiSettings.isMapToolbarEnabled = true

                                val locationResult = LocationServices.getFusedLocationProviderClient(
                                        this@MapsActivity
                                ).lastLocation
                                var myLocation = LatLng (0.0,0.0)
                                locationResult.addOnCompleteListener(this@MapsActivity) {
                                    if (it.isSuccessful && (it.result != null)) {
                                        currentPosition = LatLng(
                                                it.result.latitude,
                                                it.result.longitude
                                        )
                                        val geocoder = Geocoder(this@MapsActivity)

                                        if (currentPosition != myLocation) {
                                            myLocation = currentPosition
                                            val geoAddress = geocoder.getFromLocation(
                                                    myLocation.latitude,
                                                    myLocation.longitude,
                                                    1
                                            )
                                            address = geoAddress[0].getAddressLine(0)
                                            addTxt.setText(address)
                                            mMap.clear()
                                            mMap.addMarker(
                                                    MarkerOptions().position(myLocation)
                                                            .title(address)
                                            ).showInfoWindow()
                                            mMap.animateCamera(
                                                    CameraUpdateFactory.newLatLngZoom(
                                                            myLocation,
                                                            15f
                                                    )
                                            )
                                        }
                                    } else {
                                        Toast.makeText(
                                                this@MapsActivity,
                                                "Downloading current location failed.",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                },
                Looper.myLooper()
        )
    }

    private fun addRecord(){
        val name = etKegiatan.text.toString()
        val date = dateTxt.text.toString()
        val time = timeTxt.text.toString()
        val address = addTxt.text.toString()
        val databaseHandler:DatabaseHandler = DatabaseHandler(this)

        if ( !name.isEmpty() && !date.isEmpty() && !time.isEmpty() && !address.isEmpty() ){
            val status = databaseHandler.addAbsen(AbsenModel(0, name,time, address))
            if (status > -1){
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAddress(){
        var currentPosition = LatLng(0.0,0.0)
        if(currentPosition == LatLng(0.0, 0.0)){
            getCurrentLocation()
        }
        getTime()
    }

    @SuppressLint("NewApi")
    private fun getTime(){
        val timeFormat = if(is24HourFormat(this)) "HH:mm:ss" else "hh:mm:ss a"
        date = android.icu.text.SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
        time = android.icu.text.SimpleDateFormat(timeFormat, Locale.getDefault()).format(Date())

        val dateMaps = android.icu.text.SimpleDateFormat("EEEE,\ndd MMMM yyyy", Locale.getDefault()).format(
                Date()
        )
        dateTxt.setText(dateMaps)
        timeTxt.setText(time)
    }
}