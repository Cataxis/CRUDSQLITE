package com.example.libreriasqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.libreriasqlite.databinding.ActivityBookDetailBinding

// Clase que representa la actividad para ver y editar los detalles de un libro
class BookDetailActivity : AppCompatActivity() {

    // Binding para acceder a los elementos de la interfaz de usuario de la actividad
    private lateinit var binding: ActivityBookDetailBinding

    // Helper de base de datos para interactuar con la base de datos SQLite
    private lateinit var databaseHelper: DatabaseHelper

    // Identificador del libro actual
    private var bookId: Long = -1

    // Método que se llama cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout de la actividad utilizando el binding
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el helper de base de datos
        databaseHelper = DatabaseHelper(this)

        // Obtener el ID del libro de la intención que inició la actividad
        bookId = intent.getLongExtra(EXTRA_BOOK_ID, -1)

        // Configurar los listeners para los botones de actualizar y eliminar
        val editButton: Button = binding.botonActualizar
        editButton.setOnClickListener {
            updateBook()
        }

        val deleteButton: Button = binding.botonEliminar
        deleteButton.setOnClickListener {
            deleteBook()
        }

        // Mostrar los detalles del libro en la interfaz de usuario
        displayBookDetails()
    }

    // Método para mostrar los detalles del libro en la interfaz de usuario
    @SuppressLint("Range")
    private fun displayBookDetails() {
        val cursor: Cursor? = getBookDetailsCursor(bookId)

        // Verificar si se obtuvieron resultados en la consulta
        cursor?.use {
            if (it.moveToFirst()) {
                // Obtener los detalles del libro desde el cursor
                val title = it.getString(it.getColumnIndex(DatabaseHelper.COLUMN_TITLE))
                val author = it.getString(it.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR))
                val editorial = it.getString(it.getColumnIndex(DatabaseHelper.COLUMN_EDITORIAL))
                val isbn = it.getString(it.getColumnIndex(DatabaseHelper.COLUMN_ISBN))
                val year = it.getInt(it.getColumnIndex(DatabaseHelper.COLUMN_YEAR))
                val price = it.getInt(it.getColumnIndex(DatabaseHelper.COLUMN_PRICE))

                // Mostrar los detalles en los campos de la interfaz de usuario
                binding.inputTitulo.setText(title)
                binding.inputAutor.setText(author)
                binding.inputEditorial.setText(editorial)
                binding.inputISBN.setText(isbn)
                binding.inputAno.setText(year.toString())
                binding.inputPrecio.setText(price.toString())
            }
        }
    }

    // Método para obtener el cursor con los detalles del libro
    private fun getBookDetailsCursor(bookId: Long): Cursor? {
        val db: SQLiteDatabase = databaseHelper.readableDatabase

        return db.query(
            DatabaseHelper.TABLE_NAME,
            arrayOf(
                DatabaseHelper.COLUMN_TITLE,
                DatabaseHelper.COLUMN_AUTHOR,
                DatabaseHelper.COLUMN_EDITORIAL,
                DatabaseHelper.COLUMN_ISBN,
                DatabaseHelper.COLUMN_YEAR,
                DatabaseHelper.COLUMN_PRICE
            ),
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(bookId.toString()),
            null,
            null,
            null
        )
    }

    // Método para actualizar los detalles del libro en la base de datos
    private fun updateBook() {
        val db: SQLiteDatabase = databaseHelper.writableDatabase

        // Obtener los nuevos valores desde los campos de la interfaz de usuario
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITLE, binding.inputTitulo.text.toString())
            put(DatabaseHelper.COLUMN_AUTHOR, binding.inputAutor.text.toString())
            put(DatabaseHelper.COLUMN_EDITORIAL, binding.inputEditorial.text.toString())
            put(DatabaseHelper.COLUMN_ISBN, binding.inputISBN.text.toString())
            put(DatabaseHelper.COLUMN_YEAR, binding.inputAno.text.toString().toIntOrNull())
            put(DatabaseHelper.COLUMN_PRICE, binding.inputPrecio.text.toString().toIntOrNull())
        }

        // Actualizar el libro en la base de datos utilizando el ID del libro
        db.update(
            DatabaseHelper.TABLE_NAME,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(bookId.toString())
        )

        // Mostrar mensaje Toast
        showToast("Libro actualizado exitosamente.")

        // Finalizar la actividad después de la actualización
        finish()
    }

    // Método para eliminar el libro de la base de datos
    private fun deleteBook() {
        val db: SQLiteDatabase = databaseHelper.writableDatabase

        // Eliminar el libro de la base de datos utilizando el ID del libro
        db.delete(
            DatabaseHelper.TABLE_NAME,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(bookId.toString())
        )

        // Mostrar mensaje Toast
        showToast("Libro eliminado exitosamente.")

        // Establecer el resultado de la operación como RESULT_OK y finalizar la actividad
        setResult(RESULT_OK)
        finish()
    }

    // Función para mostrar mensajes Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Objeto companion para definir constantes relacionadas con la actividad
    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id"
    }
}
