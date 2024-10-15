package com.aiep.semana5

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.atan2
import kotlin.math.sqrt

class DeliveryCost : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvRealTimeLocation: TextView
    private lateinit var tvDistanceResult: TextView
    private lateinit var buttonBack: Button

    // Latitud y longitud predefinida
    private val predefinedLat = -33.4005915
    private val predefinedLon = -70.7581426

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deliverycost)

        // Inicializar los elementos de la interfaz
        tvRealTimeLocation = findViewById(R.id.tv_real_time_location)
        tvDistanceResult = findViewById(R.id.deliverycost_result)

        // Configurar la latitud y longitud predefinida en el TextView
        findViewById<TextView>(R.id.address_storage).text =
            "Latitud: $predefinedLat, Longitud: $predefinedLon"

        // Obtener la ubicación en tiempo real
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getRealTimeLocation()

        // Boton volver
        buttonBack = findViewById(R.id.btnback)
        buttonBack.setOnClickListener {
            volver()
        }
    }

    private fun getRealTimeLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val realTimeLat = location.latitude
                val realTimeLon = location.longitude

                // Mostrar la ubicación en tiempo real
                tvRealTimeLocation.text = "Latitud: $realTimeLat, Longitud: $realTimeLon"

                // Calcular la distancia entre ambas ubicaciones
                val distanceKm = calculateDistance(predefinedLat, predefinedLon, realTimeLat, realTimeLon)

                // Calcular el costo
                val cost = distanceKm * 300
                tvDistanceResult.text = "Costo: $%.2f CLP".format(cost)
            }
        }
    }

    // Método para calcular la distancia entre dos coordenadas
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))  // Corrección con atan2

        return earthRadiusKm * c
    }

    private fun volver() {
        val intent = Intent(this, Setpage::class.java)
        startActivity(intent)
        finish()
    }
}
