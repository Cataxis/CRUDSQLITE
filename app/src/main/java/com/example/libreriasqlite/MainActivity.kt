package com.example.libreriasqlite

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.cursoradapter.widget.SimpleCursorAdapter
import com.example.libreriasqlite.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Declaración de variables miembro
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var adapter: SimpleCursorAdapter
    private lateinit var binding: ActivityMainBinding

    // Método llamado cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización del diseño de la actividad
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización del helper de la base de datos y la vista de lista
        databaseHelper = DatabaseHelper(this)
        listView = binding.listaLibros

        // Obtención de todos los libros y configuración del adaptador de la lista
        val cursor = getAllBooks()
        val fromColumns = arrayOf(
            DatabaseHelper.COLUMN_TITLE,
            DatabaseHelper.COLUMN_AUTHOR
        )
        val toViews = intArrayOf(
            android.R.id.text1,
            android.R.id.text2
        )
        adapter = SimpleCursorAdapter(
            this, android.R.layout.simple_list_item_2, cursor, fromColumns, toViews, 0
        )
        listView.adapter = adapter

        // Configuración del clic en un elemento de la lista para ver detalles
        listView.setOnItemClickListener { _, _, position, _ ->
            val itemId = listView.getItemIdAtPosition(position)
            showBookDetail(itemId)
        }

        // Configuración del botón para agregar un nuevo libro
        val addButton: Button = binding.botonAgregarLibro
        addButton.setOnClickListener {
            showAddBookDialog()
        }
    }

    // Método para obtener todos los libros de la base de datos
    private fun getAllBooks(): Cursor {
        val db: SQLiteDatabase = databaseHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_NAME,
            arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_TITLE,
                DatabaseHelper.COLUMN_AUTHOR
            ),
            null, null, null, null, null
        )
    }

    // Método para mostrar el diálogo de agregar libro
    private fun showAddBookDialog() {
        val dialog = AddBookDialogFragment {
            updateListView()
        }
        dialog.show(supportFragmentManager, "AddBookDialogFragment")
    }

    // Método para actualizar la vista de lista después de agregar un nuevo libro
    fun updateListView() {
        val newCursor = getAllBooks()
        adapter.changeCursor(newCursor)
    }

    // Método para mostrar los detalles de un libro seleccionado
    private fun showBookDetail(bookId: Long) {
        val intent = Intent(this, BookDetailActivity::class.java)
        intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, bookId)
        startActivityForResult(intent, REQUEST_CODE_BOOK_DETAIL)
    }

    // Método llamado cuando se completa una actividad secundaria
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Verificar si la actividad es BookDetailActivity y si se ha actualizado la lista
        if (requestCode == REQUEST_CODE_BOOK_DETAIL && resultCode == RESULT_OK) {
            updateListView()
        }
    }

    // Compañero objeto para almacenar constantes
    companion object {
        const val REQUEST_CODE_BOOK_DETAIL = 1
    }
}
