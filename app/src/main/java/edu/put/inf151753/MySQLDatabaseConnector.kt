package edu.put.inf151753

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
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
import java.sql.Connection
import java.sql.DriverManager
import javax.xml.parsers.DocumentBuilderFactory



class MySQLDatabaseConnector (context: Context){
    var connection: Connection
    init{
        connection = DriverManager.getConnection("jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7707673", "sql7707673", "cxnQkQuEjN")

//        clearGames(connection)
//        clearTech(connection)
//        clearPhotos(connection)
    }


    companion object{

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

    fun insertGames(string: String){
        val db = this.connection
        val inputStream = InputSource(StringReader(string))
        val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)

        xmlDoc.documentElement.normalize()

        val items: NodeList = xmlDoc.getElementsByTagName("item")

        if (items.length < 1){
            throw TryAgainException()
        }

        val INSERT_GAME = "INSERT INTO $TABLE_GAMES VALUES (NULL, ?, ?, ?, ?, ?, ?);"

        var insert = db.prepareStatement(INSERT_GAME)

        for (i in 0..items.length-1){
            val itemNode: Node = items.item(i)

            if(itemNode.nodeType == Node.ELEMENT_NODE){
                val elem = itemNode as Element

                val gameid = elem.getAttribute("objectid") ?: "BRAK"
                val name = elem.getElementsByTagName("name").item(0)?.textContent ?: "BRAK"
                val year = elem.getElementsByTagName("yearpublished").item(0)?.textContent ?: "BRAK"
                val thumbnaillink = elem.getElementsByTagName("thumbnail").item(0)?.textContent ?: "BRAK"
                val imagelink = elem.getElementsByTagName("image").item(0)?.textContent ?: "BRAK"
                var ranking = (elem.getElementsByTagName("rank").item(0) as Element).getAttribute("value") ?: "0"
                if (ranking == "Not Ranked") ranking = "0"

                insert.clearParameters()
                insert.setString(1, gameid)
                insert.setString(2, name)
                insert.setString(3, thumbnaillink)
                insert.setString(4, imagelink)
                insert.setString(5, year)
                insert.setString(6, ranking)

                insert.executeUpdate()
            }
        }
    }

    fun getCount(): Int {
        val db = this.connection

        val SELECT_COUNT = "SELECT COUNT(*) FROM $TABLE_GAMES"
        var statement = db.prepareStatement(SELECT_COUNT)
        statement.maxRows = 1
        val rs = statement.executeQuery()
        if (rs.next()){
            return rs.getInt(1)
        }
        return -1
    }

    @SuppressLint("Range")
    fun getGames(): MutableList<Game> {
        var list: MutableList<Game> = ArrayList()
        val db = this.connection
        val SELECT_COUNT = "SELECT * FROM $TABLE_GAMES"
        val statement = db.prepareStatement(SELECT_COUNT)
        val rs = statement.executeQuery()
        while (rs.next()) {
            list.add(Game(rs.getInt(COLUMN_GAMEID),
                            rs.getString(COLUMN_NAME),
                            rs.getString(COLUMN_YEAR),
                            rs.getString(COLUMN_THUMBNAILLINK),
                            rs.getString(COLUMN_IMAGELINK),
                            rs.getInt(COLUMN_RANKING)
            ))
        }
        return list
    }

    @SuppressLint("Range")
    fun getGame(gameId: Int): Game {
        val db = this.connection
        val SELECT_COUNT = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_GAMEID = $gameId"
        var statement = db.prepareStatement(SELECT_COUNT)
        var rs = statement.executeQuery()
        if (rs.next()) {
            return Game(rs.getInt((COLUMN_GAMEID)),
                rs.getString((COLUMN_NAME)),
                rs.getString((COLUMN_YEAR)),
                rs.getString((COLUMN_THUMBNAILLINK)),
                rs.getString((COLUMN_IMAGELINK)),
                rs.getInt((COLUMN_RANKING)))
        }
        throw NoSuchFieldError(gameId.toString())
    }

    fun clearGames(givendb: Connection?=null){
        val db = givendb ?: this.connection
        val DROP_IF_EXISTS = "DROP TABLE IF EXISTS $TABLE_GAMES;"
        val drop = db.prepareStatement(DROP_IF_EXISTS)
        drop.execute()

        val CREATE_IF_NOT_EXISTS = "CREATE TABLE $TABLE_GAMES ($COLUMN_ID int NOT NULL AUTO_INCREMENT, $COLUMN_GAMEID int, $COLUMN_NAME varchar(255), $COLUMN_THUMBNAILLINK varchar(255), $COLUMN_IMAGELINK varchar(255), $COLUMN_YEAR varchar(255), $COLUMN_RANKING int, PRIMARY KEY ($COLUMN_ID))"
        val create = db.prepareStatement(CREATE_IF_NOT_EXISTS)
        create.execute()
    }

    fun getUser(): String{
        val db = this.connection
        val SELECT_COUNT = "SELECT $COLUMN_USERNAME FROM $TABLE_TECH"
        var statement = db.prepareStatement(SELECT_COUNT)
        statement.maxRows = 1
        val rs = statement.executeQuery()
        if (rs.next()){
            return rs.getString(1)
        }
        throw NoSuchFieldError("username")
    }

    fun insertUser(username: String){
        val db = this.connection
        val INSERT_GAME = "INSERT INTO $TABLE_TECH VALUES (?, 0, 0);"

        val insert = db.prepareStatement(INSERT_GAME)
        insert.setString(1, username)

        insert.execute()
    }

    fun getTimestamp(): Long{
        val db = this.connection
        val SELECT_COUNT = "SELECT $COLUMN_LAST FROM $TABLE_TECH"
        var statement = db.prepareStatement(SELECT_COUNT)
        statement.maxRows = 1
        val rs = statement.executeQuery()
        if (rs.next()){
            return rs.getLong(1)
        }

        throw NoSuchFieldError("date")
    }

    fun clearTech(givendb: Connection?=null) {
        val db = givendb ?: this.connection
        val DROP_IF_EXISTS = "DROP TABLE IF EXISTS $TABLE_TECH;"
        val drop = db.prepareStatement(DROP_IF_EXISTS)
        drop.execute()

        val CREATE_IF_NOT_EXISTS = "CREATE TABLE $TABLE_TECH ($COLUMN_USERNAME varchar(255), $COLUMN_LAST LONG, $COLUMN_COUNT INT)"
        val create = db.prepareStatement(CREATE_IF_NOT_EXISTS)
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

    fun synchronize(givendb: Connection?=null) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = givendb ?: this@MySQLDatabaseConnector.connection
            val dbc = this@MySQLDatabaseConnector

            clearGames(db)
            val user = this@MySQLDatabaseConnector.getUser()
//            try {
                val input =
                    getXML("https://www.boardgamegeek.com/xmlapi2/collection?username=${user}&stats=1")

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Połączono z bazą", Toast.LENGTH_SHORT).show()

                    try{
                        CoroutineScope(Dispatchers.IO).launch {
                            dbc.insertGames(input)
                        }
                    }
                    catch (e: TryAgainException){
                        Toast.makeText(context, "Zakolejkowano, spróbuj ponownie po chwili", Toast.LENGTH_SHORT).show()
                    }


                    Toast.makeText(context, "Załadowano dane", Toast.LENGTH_SHORT).show()
                }
                    val current_timestamp = System.currentTimeMillis() / 1000
                    val count = getCount()

                    val UPDATE_STATS =
                        "UPDATE $TABLE_TECH SET $COLUMN_LAST = $current_timestamp, $COLUMN_COUNT = $count;"
                    val statement = db.prepareStatement(UPDATE_STATS)
                    statement.executeUpdate()

                    (context as SyncActivity).updateDate()

//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
//                }
//            }
        }
    }

    fun firstTime(): Boolean{
        val db = this.connection
        if (!tableExists(TABLE_TECH, db)) return true
        val SELECT_COUNT = "SELECT $COLUMN_USERNAME FROM $TABLE_TECH"
        var statement = db.prepareStatement(SELECT_COUNT)
        statement.maxRows = 1
        val rs = statement.executeQuery()
        if (rs.next()) {
            return false
        }
        return true
    }

    fun tableExists(tableName: String, givendb: Connection?=null): Boolean {
        val db = givendb ?: this.connection

        val query =
            "SHOW TABLES LIKE '$tableName'"

        val statement = db.prepareStatement(query)

        val rs = statement.executeQuery()

        if (rs.next()){
            return true
        }
        return false
    }

    fun clearPhotos(givendb: Connection?=null) {
        val db = givendb ?: this.connection

        if(tableExists(TABLE_PHOTOS, db)){
            val list = getPhotos(givendb = db)
            list.forEach { el -> File(el.path).delete() }
        }

        val DROP_IF_EXISTS = "DROP TABLE IF EXISTS $TABLE_PHOTOS;"
        val drop = db.prepareStatement(DROP_IF_EXISTS)
        drop.execute()

        val CREATE_IF_NOT_EXISTS = "CREATE TABLE $TABLE_PHOTOS ($COLUMN_ID int AUTO_INCREMENT, $COLUMN_GAMEID int, $COLUMN_PATH varchar(255), PRIMARY KEY ($COLUMN_ID))"
        val create = db.prepareStatement(CREATE_IF_NOT_EXISTS)
        create.execute()
    }

    @SuppressLint("Range")
    fun getPhotos(gameId: Int?=null, givendb: Connection?=null): MutableList<Photo>{
        val db = givendb ?: this.connection

        var list: MutableList<Photo> = ArrayList()
        var query: String

        if (gameId == null){
            query =
                "SELECT * FROM $TABLE_PHOTOS"
        }else{
            query =
                "SELECT * FROM $TABLE_PHOTOS WHERE $COLUMN_GAMEID = $gameId"
        }

        val statement = db.prepareStatement(query)
        val rs = statement.executeQuery()
        while (rs.next()) {
            list.add(Photo(rs.getString((COLUMN_PATH))))
        }
        return list
    }

    fun insertPhoto(gameId: Int, path: String) {
        val db = this.connection
        val INSERT_GAME = "INSERT INTO $TABLE_PHOTOS VALUES (NULL, $gameId, ?);"

        val insert = db.prepareStatement(INSERT_GAME)
        insert.setString(1, path)

        insert.execute()
    }

    fun deletePhoto(path: String){
        val db = this.connection
        val DELETE_PHOTO = "DELETE FROM $TABLE_PHOTOS WHERE $COLUMN_PATH = ?"

        val delete = db.prepareStatement(DELETE_PHOTO)
        delete.setString(1, path)

        delete.execute()

        File(path).delete()
    }
}