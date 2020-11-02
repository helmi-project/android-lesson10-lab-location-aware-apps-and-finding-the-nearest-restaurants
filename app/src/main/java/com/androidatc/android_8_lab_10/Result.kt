package com.androidatc.android_8_lab_10

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class Result : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var GMap: GoogleMap
    private var locationManager: LocationManager?= null
    private var locationListener: LocationListener?= null

    override fun onMapReady(googleMap: GoogleMap) {
        GMap = googleMap

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)

                    GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                    GMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            GMap.isMyLocationEnabled = true
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        } else {
            GMap.isMyLocationEnabled = true
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,1f,locationListener!!)
        }

        val currentLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        val getPlaces = GetPlaces(this, currentLocation)
        getPlaces.execute()
    }

    // AsyncTask class allows you to perform background operations and publish results on the UI thread.
    internal inner class GetPlaces(var context: Context, location: Location?): AsyncTask<Void, Void, Void>() {
        private var restaurants: ArrayList<Place?>?= null
        private var currentLocation: Location? = location

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            setMarkers(GMap, restaurants)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            restaurants = getRestaurantsFromGoogle(GMap, currentLocation)
            return null
        }
    }

    fun getRestaurantsFromGoogle(map: GoogleMap, location: Location?): ArrayList<Place?>? {
        val service = PlacesService("AIzaSyAq_i_MVOzZ_1xMlKpFI-DpDfY6KNCHRCk")

        return service.findRestaurants(location!!.latitude, location.longitude)
    }

    private fun getMarkerFromResult(result: Place?): MarkerOptions {
        val mo = MarkerOptions()

        mo.position(LatLng(result!!.latitude, result.longitude)).title(result.name).snippet(result.vicinity)
        //Where the marker title should appear relative to the marker
            .infoWindowAnchor(0.5f, 0.5f)

        return mo
    }

    private fun setMarkers(map: GoogleMap, markers: ArrayList<Place?>?) {
        for (place in markers!!) {
            map.addMarker(getMarkerFromResult(place))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
}
