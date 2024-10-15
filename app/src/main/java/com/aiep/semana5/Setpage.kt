package com.aiep.semana5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aiep.semana5.databinding.ActivitySetpageBinding
import com.google.firebase.auth.FirebaseAuth

class Setpage : AppCompatActivity() {

    private lateinit var binding: ActivitySetpageBinding
    private lateinit var buttonLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetpageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar listener para el botón "Ver mi ubicación"
        binding.btnMapview.setOnClickListener {
            // Iniciar la actividad donde se mostrará el mapa con la ubicación del usuario
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        // Configurar listener para el botón "Calcular despacho"
        binding.btnDeliverycost.setOnClickListener {
            // Iniciar la actividad o mostrar un cálculo para el costo del despacho
            val intent = Intent(this, DeliveryCost::class.java)
            startActivity(intent)
        }
        // Boton LogOut
        buttonLogout = findViewById(R.id.btnlogout)
        buttonLogout.setOnClickListener {
            cerrarSesion()
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
