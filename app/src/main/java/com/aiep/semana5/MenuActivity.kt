package com.aiep.semana5

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MenuActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var buttonLogout: Button
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_REQUEST_CODE = 1000
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menuactivity)

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().getReference("usuarios")

        // Inicializar el cliente de localización
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Referencias a los elementos del layout
        buttonLogout = findViewById(R.id.btnlogout)

        // Configuración del mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Listener para el botón de cerrar sesión
        buttonLogout.setOnClickListener {
            cerrarSesion()
        }

        // Verificar permisos y obtener ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            // Obtener la ubicación en tiempo real
            obtenerUbicacion()
        }
    }

    // Configurar el mapa cuando esté listo
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Agregar un marcador en una ubicación predeterminada
        val defaultLocation = LatLng(-33.51822, -70.7184172) // Santiago, Chile
        mMap.addMarker(MarkerOptions().position(defaultLocation).title("Cerrillos"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 13f)) // Zoom 13
    }

    // Obtener ubicación y guardar en Firebase
    private fun obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitud = location.latitude
                val longitud = location.longitude
                Toast.makeText(this, "Latitud: $latitud, Longitud: $longitud", Toast.LENGTH_LONG).show()

                // Guardar localización en Firebase Realtime Database
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                val userLocation = mapOf("latitud" to latitud, "longitud" to longitud)

                if (userId != null) {
                    // Guardar la ubicación bajo el nodo del usuario
                    database.child(userId).child("ubicacion").setValue(userLocation)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Ubicación guardada en Firebase", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar ubicación", Toast.LENGTH_SHORT).show()
                        }
                }

                // Actualizar el mapa con la ubicación actual del usuario
                val currentLocation = LatLng(latitud, longitud)
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(currentLocation).title("Tu ubicación"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f)) // Zoom 15
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Solicitar permisos de ubicación si no se han concedido
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                obtenerUbicacion()
            } else {
                Toast.makeText(this, "Permisos de ubicación denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cerrarSesion() {
        // Cierra sesión del usuario en Firebase
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        Toast.makeText(this, "Has cerrado sesión", Toast.LENGTH_SHORT).show()

        // Redirige al usuario a la pantalla de inicio de sesión
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
        finish() // Finaliza la actividad actual
    }
}
