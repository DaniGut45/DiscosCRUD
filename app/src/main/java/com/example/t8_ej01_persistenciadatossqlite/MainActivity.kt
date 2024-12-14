package com.example.t8_ej01_persistenciadatossqlite

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    // Variables de la interfaz de usuario
    private lateinit var etNombre: EditText
    private lateinit var etAnio: EditText
    private lateinit var btnAgregar: Button
    private lateinit var btnVerTodos: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var dbHandler: DatabaseHelper

    // Objeto que almacenará el disco que será actualizado o eliminado
    private var discoSeleccionado: Disco? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa las vistas
        etNombre = findViewById(R.id.etNombreDisco)
        etAnio = findViewById(R.id.etAnioPublicacion)
        btnAgregar = findViewById(R.id.btnAdd)
        btnVerTodos = findViewById(R.id.btnViewAll)
        btnActualizar = findViewById(R.id.btnUpdate)
        btnEliminar = findViewById(R.id.btnDelete)
        recyclerView = findViewById(R.id.rvDiscos)

        // Inicializa la base de datos
        dbHandler = DatabaseHelper(this)

        // Configuración de RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configura los botones
        btnAgregar.setOnClickListener { addDisco() }
        btnVerTodos.setOnClickListener { viewDiscos() }
        btnActualizar.setOnClickListener { updateDisco() }
        btnEliminar.setOnClickListener { deleteDisco() }

        // Muestra la lista de discos al iniciar la actividad
        viewDiscos()
    }

    // Método para agregar un nuevo disco
    private fun addDisco() {
        val nombre = etNombre.text.toString()
        val anio = etAnio.text.toString()

        if (nombre.isNotEmpty() && anio.isNotEmpty()) {
            val disco = Disco(nombre = nombre, anio = anio.toInt())
            val status = dbHandler.addDisco(disco)

            if (status > -1) {
                Toast.makeText(applicationContext, "Disco agregado", Toast.LENGTH_LONG).show()
                clearEditTexts()
                viewDiscos()
            }
        } else {
            Toast.makeText(applicationContext, "Nombre y Año son requeridos", Toast.LENGTH_LONG).show()
        }
    }

    // Método para mostrar todos los discos
    private fun viewDiscos() {
        val discosList = dbHandler.getAllDiscos()
        val adapter = DiscosAdapter(discosList, ::onDiscoSelected)
        recyclerView.adapter = adapter
    }

    // Método para limpiar los campos de texto
    private fun clearEditTexts() {
        etNombre.text.clear()
        etAnio.text.clear()

    }

    // Método para seleccionar un disco de la lista y cargarlo en los campos para actualizar o eliminar
    private fun onDiscoSelected(disco: Disco) {
        discoSeleccionado = disco
        etNombre.setText(disco.nombre)
        etAnio.setText(disco.anio.toString())
    }

    // Método para actualizar un disco
    private fun updateDisco() {
        val nombre = etNombre.text.toString()
        val anio = etAnio.text.toString()

        if (discoSeleccionado != null && nombre.isNotEmpty() && anio.isNotEmpty()) {
            val disco = discoSeleccionado!!
            disco.nombre = nombre
            disco.anio = anio.toInt()

            val status = dbHandler.updateDisco(disco)

            if (status > 0) {
                Toast.makeText(applicationContext, "Disco actualizado", Toast.LENGTH_LONG).show()
                clearEditTexts()
                viewDiscos()
                discoSeleccionado = null
            } else {
                Toast.makeText(applicationContext, "Error al actualizar el disco", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(applicationContext, "Selecciona un disco y completa los campos", Toast.LENGTH_LONG).show()
        }
    }

    // Método para eliminar un disco
    private fun deleteDisco() {
        if (discoSeleccionado != null) {
            val disco = discoSeleccionado!!
            val status = dbHandler.deleteDisco(disco)

            if (status > 0) {
                Toast.makeText(applicationContext, "Disco eliminado", Toast.LENGTH_LONG).show()
                clearEditTexts()
                viewDiscos()
                discoSeleccionado = null
            } else {
                Toast.makeText(applicationContext, "Error al eliminar el disco", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(applicationContext, "Selecciona un disco para eliminar", Toast.LENGTH_LONG).show()
        }
    }
}
