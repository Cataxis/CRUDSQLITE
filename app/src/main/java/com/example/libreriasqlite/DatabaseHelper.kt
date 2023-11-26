package com.example.libreriasqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Clase que actúa como un helper para interactuar con la base de datos SQLite
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Definición de constantes relacionadas con la base de datos
    companion object {
        const val DATABASE_NAME = "library.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "books"
        const val COLUMN_ID = "_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_EDITORIAL = "editorial"
        const val COLUMN_ISBN = "isbn"
        const val COLUMN_YEAR = "year"
        const val COLUMN_PRICE = "price"
    }

    // Método llamado cuando se crea la base de datos por primera vez
    override fun onCreate(db: SQLiteDatabase) {
        // Definir la estructura de la tabla de libros y ejecutar la consulta de creación
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_AUTHOR TEXT, " +
                "$COLUMN_EDITORIAL TEXT, " +
                "$COLUMN_ISBN TEXT, " +
                "$COLUMN_YEAR INTEGER," +
                "$COLUMN_PRICE INTEGER)"
        db.execSQL(createTableQuery)
    }

    // Método llamado cuando se actualiza la versión de la base de datos
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Eliminar la tabla existente y volver a crearla al actualizar la versión
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Función para agregar un nuevo libro a la base de datos
    fun addBook(titulo: String, autor: String, editorial: String, isbn: String, anio: Int, precio: Int) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, titulo)
            put(COLUMN_AUTHOR, autor)
            put(COLUMN_EDITORIAL, editorial)
            put(COLUMN_ISBN, isbn)
            put(COLUMN_YEAR, anio)
            put(COLUMN_PRICE, precio)
        }
        db.insert(TABLE_NAME, null, values)
    }

    // Función para obtener todos los libros de la base de datos
    fun getAllBooks(): Cursor {
        val db: SQLiteDatabase = readableDatabase
        return db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_TITLE, COLUMN_AUTHOR),
            null,
            null,
            null,
            null,
            null
        )
    }

    // Función para obtener los detalles de un libro específico
    fun getBookDetails(bookId: Long): Cursor {
        val db: SQLiteDatabase = readableDatabase
        return db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_TITLE,
                COLUMN_AUTHOR,
                COLUMN_EDITORIAL,
                COLUMN_ISBN,
                COLUMN_YEAR,
                COLUMN_PRICE,
            ),
            "$COLUMN_ID = ?",
            arrayOf(bookId.toString()),
            null,
            null,
            null
        )
    }

    // Función para actualizar los detalles de un libro en la base de datos
    fun updateBookDetails(
        bookId: Long,
        titulo: String,
        autor: String,
        editorial: String,
        isbn: String,
        anio: Int,
        precio: Int
    ) {
        val db: SQLiteDatabase = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_TITLE, titulo)
            put(COLUMN_AUTHOR, autor)
            put(COLUMN_EDITORIAL, editorial)
            put(COLUMN_ISBN, isbn)
            put(COLUMN_YEAR, anio)
            put(COLUMN_PRICE, precio)
        }

        db.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(bookId.toString())
        )
    }

    // Función para eliminar un libro de la base de datos
    fun deleteBook(bookId: Long) {
        val db: SQLiteDatabase = writableDatabase
        db.delete(
            TABLE_NAME,
            "$COLUMN_ID = ?",
            arrayOf(bookId.toString())
        )
    }
}
