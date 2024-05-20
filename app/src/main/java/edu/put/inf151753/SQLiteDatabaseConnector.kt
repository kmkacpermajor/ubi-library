package edu.put.inf151753

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.xml.parsers.DocumentBuilderFactory



class SQLiteDatabaseConnector (context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){
    companion object{
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "library.db"

        val TABLE_GAMES = "games"
        val TABLE_TECH = "tech"
        val TABLE_PHOTOS = "photos"

        val COLUMN_ID = "_id"
        val COLUMN_GAMEID = "gameid"
        val COLUMN_NAME = "name"
        val COLUMN_THUMBNAILLINK = "thumbnaillink"
        val COLUMN_IMAGELINK = "imagelink"
        val COLUMN_YEAR = "year"
        val COLUMN_RANKING = "ranking"

        val COLUMN_USERNAME = "username"
        val COLUMN_LAST = "last"
        val COLUMN_COUNT = "count"

        val COLUMN_PATH = "path"
    }

    val context = context

    override fun onCreate(db: SQLiteDatabase) {
        clearGames(db)
        clearTech(db)
        clearPhotos(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    fun insertGames(string: String){
        val db = this.writableDatabase
        val inputStream = InputSource(StringReader(string))
        val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)

        xmlDoc.documentElement.normalize()

        val items: NodeList = xmlDoc.getElementsByTagName("item")

        if (items.length < 1){
            throw TryAgainException()
        }

        val INSERT_GAME = "INSERT INTO $TABLE_GAMES VALUES (NULL, ?, ?, ?, ?, ?, ?);"

        val insert = db.compileStatement(INSERT_GAME)

        for (i in 0..items.length-1){
            val itemNode: Node = items.item(i)

            if(itemNode.nodeType == Node.ELEMENT_NODE){
                val elem = itemNode as Element

                val gameid = elem.getAttribute("objectid") ?: "BRAK"
                val name = elem.getElementsByTagName("name").item(0)?.textContent ?: "BRAK"
                val year = elem.getElementsByTagName("yearpublished").item(0)?.textContent ?: "BRAK"
                val thumbnaillink = elem.getElementsByTagName("thumbnail").item(0)?.textContent ?: "BRAK"
                val imagelink = elem.getElementsByTagName("image").item(0)?.textContent ?: "BRAK"
                val ranking = (elem.getElementsByTagName("rank").item(0) as Element).getAttribute("value") ?: "BRAK"

                insert.clearBindings()
                insert.bindString(1, gameid)
                insert.bindString(2, name)
                insert.bindString(3, thumbnaillink)
                insert.bindString(4, imagelink)
                insert.bindString(5, year)
                insert.bindString(6, ranking)

                insert.executeInsert()
            }
        }
    }

    fun getCount(): Int {
        val db = this.writableDatabase
        val SELECT_COUNT = "SELECT COUNT(*) FROM $TABLE_GAMES"
        var cursor = db.rawQuery(SELECT_COUNT, null)
        if (cursor.moveToFirst()) {
            return cursor.getInt(0)
        }
        return -1
    }

    @SuppressLint("Range")
    fun getGames(): MutableList<Game> {
        var list: MutableList<Game> = ArrayList()
        val db = this.writableDatabase
        val SELECT_COUNT = "SELECT * FROM $TABLE_GAMES"
        var cursor = db.rawQuery(SELECT_COUNT, null)
        if (cursor.moveToFirst()) {
            do{
                list.add(Game(cursor.getInt(cursor.getColumnIndex(COLUMN_GAMEID)),
                                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                                cursor.getString(cursor.getColumnIndex(COLUMN_YEAR)),
                                cursor.getString(cursor.getColumnIndex(COLUMN_THUMBNAILLINK)),
                                cursor.getString(cursor.getColumnIndex(COLUMN_IMAGELINK)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_RANKING))
                ))
            }while(cursor.moveToNext())
        }
        return list
    }

    @SuppressLint("Range")
    fun getGame(gameId: Int): Game {
        val db = this.writableDatabase
        val SELECT_COUNT = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_GAMEID = $gameId"
        var cursor = db.rawQuery(SELECT_COUNT, null)
        if (cursor.moveToFirst()) {
            return Game(cursor.getInt(cursor.getColumnIndex(COLUMN_GAMEID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_YEAR)),
                cursor.getString(cursor.getColumnIndex(COLUMN_THUMBNAILLINK)),
                cursor.getString(cursor.getColumnIndex(COLUMN_IMAGELINK)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_RANKING)))
        }
        throw NoSuchFieldError(gameId.toString())
    }

    fun clearGames(givendb: SQLiteDatabase?=null){
        val db = givendb ?: this.writableDatabase
        val DROP_IF_EXISTS = "DROP TABLE IF EXISTS $TABLE_GAMES;"
        val drop = db.compileStatement(DROP_IF_EXISTS)
        drop.execute()

        val CREATE_IF_NOT_EXISTS = "CREATE TABLE $TABLE_GAMES ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_GAMEID INTEGER, $COLUMN_NAME TEXT, $COLUMN_THUMBNAILLINK TEXT, $COLUMN_IMAGELINK TEXT, $COLUMN_YEAR TEXT, $COLUMN_RANKING INTEGER)"
        val create = db.compileStatement(CREATE_IF_NOT_EXISTS)
        create.execute()
    }

    fun getUser(): String{
        val db = this.readableDatabase
        val SELECT_COUNT = "SELECT $COLUMN_USERNAME FROM $TABLE_TECH"
        var cursor = db.rawQuery(SELECT_COUNT, null)
        if (cursor.moveToFirst()) {
            return cursor.getString(0)
        }
        throw NoSuchFieldError("username")
    }

    fun insertUser(username: String){
        val db = this.writableDatabase
        val INSERT_GAME = "INSERT INTO $TABLE_TECH VALUES (?, 0, 0, 0);"

        val insert = db.compileStatement(INSERT_GAME)
        insert.bindString(1, username)

        insert.executeInsert()
    }

    fun getTimestamp(): Long{
        val db = this.readableDatabase
        val SELECT_COUNT = "SELECT $COLUMN_LAST FROM $TABLE_TECH"
        var cursor = db.rawQuery(SELECT_COUNT, null)
        if (cursor.moveToFirst()) {
            return cursor.getLong(0)
        }
        throw NoSuchFieldError("date")
    }

    fun clearTech(givendb: SQLiteDatabase?=null) {
        val db = givendb ?: this.writableDatabase
        val DROP_IF_EXISTS = "DROP TABLE IF EXISTS $TABLE_TECH;"
        val drop = db.compileStatement(DROP_IF_EXISTS)
        drop.execute()

        val CREATE_IF_NOT_EXISTS = "CREATE TABLE $TABLE_TECH ($COLUMN_USERNAME TEXT, $COLUMN_LAST LONG, $COLUMN_COUNT INT)"
        val create = db.compileStatement(CREATE_IF_NOT_EXISTS)
        create.execute()
    }

    fun getXML(link: String): String {
        var sb = StringBuilder()
        var connection: HttpURLConnection
        val url = URL(link)
        connection = url.openConnection() as HttpURLConnection
        connection.setRequestMethod("GET")
        connection.setUseCaches(false)
        connection.connect()

        val br =
            BufferedReader(
                InputStreamReader(
                    connection.inputStream,
                    StandardCharsets.UTF_8
                )
            )
        var line: String?
        while (br.readLine().also { line = it } != null) {
            sb.append(line).append("\n")
        }
        br.close()
        return sb.toString()
    }

    fun synchronize(givendb: SQLiteDatabase?=null) {
        val db = givendb ?: this.writableDatabase
        val dbc = this

        Toast.makeText(context, "Rozpoczynam synchronizacje", Toast.LENGTH_SHORT).show()
        clearGames(db)
        val user = this.getUser()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val input =
                    getXML("https://www.boardgamegeek.com/xmlapi2/collection?username=${user}&stats=1")

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Połączono z bazą", Toast.LENGTH_SHORT).show()

                    try{
                        dbc.insertGames(input)
                    }
                    catch (e: TryAgainException){
                        Toast.makeText(context, "Zakolejkowano, spróbuj ponownie po chwili", Toast.LENGTH_SHORT).show()
                    }


                    Toast.makeText(context, "Załadowano dane", Toast.LENGTH_SHORT).show()

                    val current_timestamp = System.currentTimeMillis() / 1000
                    val count = getCount()

                    val UPDATE_STATS =
                        "UPDATE $TABLE_TECH SET $COLUMN_LAST = $current_timestamp, $COLUMN_COUNT = $count;"
                    db.execSQL(UPDATE_STATS)

                    (context as SyncActivity).updateDate()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun firstTime(): Boolean{
        val db = this.readableDatabase
        val SELECT_COUNT = "SELECT $COLUMN_USERNAME FROM $TABLE_TECH"
        var cursor = db.rawQuery(SELECT_COUNT, null)
        if (cursor.moveToFirst()) {
            return false
        }
        return true
    }

    fun tableExists(tableName: String, givendb: SQLiteDatabase?=null): Boolean {
        val db = givendb ?: this.writableDatabase

        val query =
            "select DISTINCT tbl_name from sqlite_master where tbl_name = '$tableName'"
        db.rawQuery(query, null).use { cursor ->
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    return true
                }
            }
            return false
        }
    }

    fun clearPhotos(givendb: SQLiteDatabase?=null) {
        val db = givendb ?: this.writableDatabase

        if(tableExists(TABLE_PHOTOS, db)){
            val list = getPhotos(givendb = db)
            list.forEach { el -> File(el.path).delete() }
        }

        val DROP_IF_EXISTS = "DROP TABLE IF EXISTS $TABLE_PHOTOS;"
        val drop = db.compileStatement(DROP_IF_EXISTS)
        drop.execute()

        val CREATE_IF_NOT_EXISTS = "CREATE TABLE $TABLE_PHOTOS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_GAMEID INTEGER, $COLUMN_PATH TEXT)"
        val create = db.compileStatement(CREATE_IF_NOT_EXISTS)
        create.execute()
    }

    @SuppressLint("Range")
    fun getPhotos(gameId: Int?=null, givendb: SQLiteDatabase?=null): MutableList<Photo>{
        val db = givendb ?: this.writableDatabase

        var list: MutableList<Photo> = ArrayList()
        var query: String

        if (gameId == null){
            query =
                "SELECT * FROM $TABLE_PHOTOS"
        }else{
            query =
                "SELECT * FROM $TABLE_PHOTOS WHERE $COLUMN_GAMEID = $gameId"
        }

        db.rawQuery(query, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    list.add(Photo(cursor.getString(cursor.getColumnIndex(COLUMN_PATH))))
                } while (cursor.moveToNext())
            }
        }
        return list
    }

    fun insertPhoto(gameId: Int, path: String) {
        val db = this.writableDatabase
        val INSERT_GAME = "INSERT INTO $TABLE_PHOTOS VALUES (NULL, $gameId, ?);"

        val insert = db.compileStatement(INSERT_GAME)
        insert.bindString(1, path)

        insert.executeInsert()
    }

    fun deletePhoto(path: String){
        val db = this.writableDatabase
        val DELETE_PHOTO = "DELETE FROM $TABLE_PHOTOS WHERE $COLUMN_PATH = ?"

        val delete = db.compileStatement(DELETE_PHOTO)
        delete.bindString(1, path)

        delete.executeUpdateDelete()

        File(path).delete()
    }
}