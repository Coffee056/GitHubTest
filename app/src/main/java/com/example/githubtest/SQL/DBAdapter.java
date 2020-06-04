package com.example.githubtest.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Date;
import java.sql.Time;

public class DBAdapter {

    private static final String DB_NAME = "BTConnection.db";
    private static final String DB_TABLE_BTConnection = "connectioninfo";
    private static final int DB_VERSION = 1;

    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "datetime";
    public static final String KEY_ISSent = "isSent";
    public static final String KEY_MAC ="mac";
    public static final String KEY_DURATION ="duration";



    private SQLiteDatabase db;
    private final Context context;
    private DBOpenHelper dbOpenHelper;

    public DBAdapter(Context _context) {
        context = _context;
    }

    /** Close the database */
    public void close() {
        if (db != null){
            db.close();
            db = null;
        }
    }

    /** Open the database */
    public void open() throws SQLiteException {
        dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbOpenHelper.getWritableDatabase();
        }
        catch (SQLiteException ex) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }


    public long insertBTConnection(BTConnection connection) {
        ContentValues newValues = new ContentValues();


        newValues.put(KEY_DATE, BTConnection.DateToString(connection.datetime));
        newValues.put(KEY_ISSent, connection.isSent);
        newValues.put(KEY_MAC,connection.MAC_address);
        newValues.put(KEY_DURATION,connection.duration);
        return db.insert(DB_TABLE_BTConnection, null, newValues);
    }


    public BTConnection[] queryAllBTConnection() {
        Cursor results =  db.query(DB_TABLE_BTConnection, new String[] { KEY_ID, KEY_DATE, KEY_ISSent,KEY_DURATION,KEY_MAC},
                null, null, null, null, null);
        return ConvertToBTConnection(results);
    }


    public BTConnection[] queryBTConnectionByID(long id) {
        Cursor results =  db.query(DB_TABLE_BTConnection, new String[] { KEY_ID, KEY_DATE, KEY_ISSent,KEY_DURATION,KEY_MAC},
                KEY_ID + "=" + id, null, null, null, null);
        return ConvertToBTConnection(results);
    }

    public BTConnection[] queryBTConnectionByDate(Date date1, Date date2) {
        Cursor results =  db.query(DB_TABLE_BTConnection, new String[] { KEY_ID, KEY_DATE, KEY_ISSent,KEY_DURATION,KEY_MAC},
                "datetime("+KEY_DATE+") >= "+" datetime(?) and datetime("+KEY_DATE+") <= "+
                        " datetime(?) ORDER BY "+KEY_DATE +" DESC"
                , new String[] {date1.toString(),date2.toString()}, null, null, null);
        return ConvertToBTConnection(results);
    }




    private BTConnection[] ConvertToBTConnection(Cursor cursor){
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        BTConnection[] connections = new BTConnection[resultCounts];
        for (int i = 0 ; i<resultCounts; i++){
            connections[i] = new BTConnection();
            connections[i].ID = cursor.getInt(0);
            connections[i].datetime = BTConnection.strToDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            connections[i].isSent = cursor.getInt(cursor.getColumnIndex(KEY_ISSent));
            connections[i].duration = cursor.getInt(cursor.getColumnIndex(KEY_DURATION));
            connections[i].MAC_address=cursor.getString(cursor.getColumnIndex(KEY_MAC));
            cursor.moveToNext();
        }
        return connections;
    }

    public long deleteAllBTConnection() {
        return db.delete(DB_TABLE_BTConnection, null, null);
    }

    public long deleteOneBTConnection(long id) {
        return db.delete(DB_TABLE_BTConnection,  KEY_ID + "=" + id, null);
    }


    public long updateBTConnection(long id , BTConnection connection){
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_DATE, BTConnection.DateToString(connection.datetime));
        updateValues.put(KEY_ISSent, connection.isSent);
        updateValues.put(KEY_DURATION,connection.duration);

        return db.update(DB_TABLE_BTConnection, updateValues,  KEY_ID + "=" + id, null);
    }



    private static class DBOpenHelper extends SQLiteOpenHelper {

        public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        private static final String DB_CREATE_BTConnection = "create table " +
                DB_TABLE_BTConnection + " (" + KEY_ID + " integer primary key autoincreme" +
                "nt, " +
                KEY_DATE + " text not null," + KEY_ISSent + " integer not null,"+
                KEY_DURATION+" integer not null,"+KEY_MAC +" text not null" +");";

        @Override
        public void onOpen(SQLiteDatabase _db) {
            super.onOpen(_db);
            if(!_db.isReadOnly()) {
                _db.execSQL("PRAGMA foreign_keys = ON;");
            }
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DB_CREATE_BTConnection);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            _db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BTConnection);
            onCreate(_db);
        }
    }
}