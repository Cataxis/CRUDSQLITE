package com.example.libreriasqlite

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.libreriasqlite.databinding.DialogAddBookBinding

// Clase que representa un cuadro de diálogo para agregar un nuevo libro
class AddBookDialogFragment(private val listener: () -> Unit) : DialogFragment() {

    // Binding para acceder a los elementos de la interfaz de usuario del cuadro de diálogo
    private lateinit var binding: DialogAddBookBinding

    // Método que se llama cuando se crea el cuadro de diálogo
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflar el layout del cuadro de diálogo utilizando el binding
        val inflater = requireActivity().layoutInflater
        binding = DialogAddBookBinding.inflate(inflater)

        // Construir y devolver un cuadro de diálogo utilizando AlertDialog.Builder
        return AlertDialog.Builder(requireContext())
            .setTitle("Agregar Nuevo Libro")
            .setView(binding.root)
            .setPositiveButton("Agregar") { _, _ ->
                // Obtener los valores de los campos de entrada en el cuadro de diálogo
                val titulo = binding.inputTitulo.text.toString()
                val autor = binding.inputAutor.text.toString()
                val editorial = binding.inputEditorial.text.toString()
                val isbn = binding.inputISBN.text.toString()
                val anio = binding.inputAno.text.toString().toIntOrNull()
                val precio = binding.inputPrecio.text.toString().toIntOrNull()

                // Validar que los campos obligatorios no estén vacíos
                if (titulo.isNotEmpty() && autor.isNotEmpty() && anio != null && precio != null) {
                    // Llamar a la función NuevoLibro para agregar el libro a la base de datos
                    NuevoLibro(titulo, autor, editorial, isbn, anio, precio)
                    // Invocar la función del listener para realizar acciones después de agregar el libro
                    listener.invoke()
                } else {
                    showToast("Por favor, complete los campos obligatorios.")
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    // Función para agregar un nuevo libro a la base de datos
    private fun NuevoLibro(
        titulo: String,
        autor: String,
        editorial: String,
        isbn: String,
        anio: Int?,
        precio: Int?
    ) {
        // Obtener una instancia de la base de datos en modo escritura
        val db: SQLiteDatabase = DatabaseHelper(requireContext()).writableDatabase

        // Crear un objeto ContentValues para almacenar los valores del libro
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITLE, titulo)
            put(DatabaseHelper.COLUMN_AUTHOR, autor)
            put(DatabaseHelper.COLUMN_EDITORIAL, editorial)
            put(DatabaseHelper.COLUMN_ISBN, isbn)
            put(DatabaseHelper.COLUMN_YEAR, anio)
            put(DatabaseHelper.COLUMN_PRICE, precio)
        }

        // Insertar el nuevo libro en la base de datos
        db.insert(DatabaseHelper.TABLE_NAME, null, values)
    }

    // Función para mostrar mensajes Toast
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
