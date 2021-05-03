package com.example.sqlite.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.sqlite.model.Person

class DatabaseHandler(ctx: Context): SQLiteOpenHelper(ctx, DB_NAME,null,DB_VERSION) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY, $NAME TEXT, $GENDER TEXT, $BIRTH TEXT);"
        p0?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME;"
        p0?.execSQL(DROP_TABLE)
        onCreate(p0)
    }

    fun addPerson(person: Person){
        val p0 = writableDatabase
        val values = ContentValues().apply {
            put(NAME, person.name)
            put(GENDER, person.gender)
            put(BIRTH, person.birth)
        }
        p0.insert(TABLE_NAME,null,values)
    }

    fun getPerson(id: Int): Person{
        val p0 = readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $ID = $id;"
        val cursor: Cursor = p0.rawQuery(selectQuery,null)
        cursor?.moveToFirst()
        val person: Person = popularPerson(cursor)
        cursor.close()
        return person
    }

    fun getPersonList(): ArrayList<Person>{
        val personList = ArrayList<Person>()
        val p0 = readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY $NAME"
        val cursor = p0.rawQuery(selectQuery, null)
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    val person: Person = popularPerson(cursor)
                    personList.add(person)
                }while (cursor.moveToNext())
            }
        }
        cursor.close()
        return personList
    }

    fun updatePerson(person: Person){
        val p0 = writableDatabase
        val values = ContentValues().apply {
            put(NAME, person.name)
            put(GENDER, person.gender)
            put(BIRTH, person.birth)
        }
        p0.update(TABLE_NAME,values,"$ID = ?", arrayOf(person.id.toString()))
    }

    fun popularPerson(cursor: Cursor): Person {
        val person = Person()
        person.id = cursor.getInt(cursor.getColumnIndex(ID))
        person.name = cursor.getString(cursor.getColumnIndex(NAME))
        person.birth = cursor.getString(cursor.getColumnIndex(BIRTH))
        person.gender = cursor.getString(cursor.getColumnIndex(GENDER))
        return person
    }

    fun delPerson(id: Int){
        val p0 = writableDatabase
        p0.delete(TABLE_NAME,"$ID = ?", arrayOf(id.toString()))
    }

    fun searchPerson(str: String): ArrayList<Person>{
        val personList = ArrayList<Person>()
        val p0 = readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE  $NAME LIKE '%$str%' OR $GENDER LIKE '%$str%' OR $BIRTH LIKE '%$str%'; "
        val cursor = p0.rawQuery(selectQuery, null)
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    val person: Person = popularPerson(cursor)
                    personList.add(person)
                }while (cursor.moveToNext())
            }
        }
        cursor.close()
        return personList
    }

    companion object{
        private val DB_VERSION = 1
        private val DB_NAME = "CadWhere"
        private val TABLE_NAME = "Person"
        private val ID = "id"
        private val NAME = "name"
        private val GENDER = "gender"
        private val BIRTH = "birth"
    }
}