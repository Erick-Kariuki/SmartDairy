package com.example.dairyapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context):
SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "farm_name"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT," +
                "$COLUMN_PASSWORD TEXT)")
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun addFarm(user: User){
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_USERNAME, user.farmname)
        values.put(COLUMN_PASSWORD, user.password)

        db.insert(TABLE_NAME,null, values)
        db.close()
    }

    fun checkUser(farmname: String):Boolean {
        val columns = arrayOf(COLUMN_USERNAME)

        val db = this.readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(farmname)

        val cursor: Cursor = db.query(TABLE_NAME,columns,selection,
            selectionArgs,null, null, null)

        val cursorCount: Int = cursor.count
        cursor.close()
        db.close()
        return cursorCount > 0
    }

    fun checkUser(farmname: String, password: String):Boolean {
        val columns = arrayOf(COLUMN_USERNAME)

        val db = this.readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(farmname,password)

        val cursor: Cursor = db.query(TABLE_NAME,columns,selection,
            selectionArgs,null, null, null)

        val cursorCount: Int = cursor.count
        cursor.close()
        db.close()
        return cursorCount > 0
    }
}