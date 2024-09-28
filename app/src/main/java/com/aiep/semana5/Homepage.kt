package com.aiep.semana5

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.aiep.semana5.databinding.ActivityHomepageBinding

class Homepage : AppCompatActivity() {

    private lateinit var binding: ActivityHomepageBinding
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración del binding
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Auth
        auth = Firebase.auth

        // Listener para el botón de login
        binding.btnLogin.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            // Validar que el correo y la contraseña no estén vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa tu correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Intentar autenticar al usuario
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Autenticación exitosa, mover a MenuActivity
                        val intent = Intent(this, MenuActivity::class.java)
                        startActivity(intent)
                        finish() // Finaliza la actividad actual
                    } else {
                        // Si la autenticación falla, mostrar un mensaje al usuario
                        Toast.makeText(baseContext, "Autenticación fallida.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Referencia al botón de registro desde el XML
        binding.btnRegistro.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        // Referencia al EditText de la contraseña
        binding.password.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEndPosition = binding.password.right - binding.password.compoundDrawables[2].bounds.width()
                if (event.rawX >= drawableEndPosition) {
                    togglePasswordVisibility()  // Cambiar visibilidad de la contraseña
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    override fun onStart() {
        super.onStart()
        // Verifica si hay un usuario autenticado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish() // Finaliza la actividad actual
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            // Mostrar la contraseña
            binding.password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0) // Cambiar a icono de "ocultar"
        } else {
            // Ocultar la contraseña
            binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0) // Cambiar a icono de "mostrar"
        }
        // Mover el cursor al final del texto
        binding.password.setSelection(binding.password.text.length)
    }
}





