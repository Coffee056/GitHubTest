package com.example.githubtest.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;


public class DBAdapter {

    private static final String DB_NAME = "BTConnection.db";
    private static final String DB_TABLE_BTConnection = "connectioninfo";
    private static final String DB_TABLE_BroadcastKey = "broadcastket";
    private static final String DB_TABLE_SafetyReminder = "safetyreminder";
    private static final int DB_VERSION = 1;

    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "datetime";
    public static final String KEY_ISSent = "isSent";
    public static final String KEY_MAC ="mac";
    public static final String KEY_DURATION ="duration";

    public static final String KEY_CONNECT_DATE = "connect_date";
    public static final String KEY_CONNECT_TIME = "connect_time";
    public static final String KEY_CONNECT_MAC = "connect_mac";
    public static final String KEY_SELF_MAC = "self_mac";

    public static final String KEY_IS_CONFIRM = "isConfirm";


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

    public long insertBroadcastKey(BroadcastKey connection) {
        ContentValues newValues = new ContentValues();

        newValues.put(KEY_CONNECT_DATE, BTConnection.DateToString(connection.connect_date));
        newValues.put(KEY_CONNECT_TIME, connection.connect_time);
        newValues.put(KEY_CONNECT_MAC,connection.connect_mac);
        newValues.put(KEY_SELF_MAC,connection.self_mac);
        return db.insert(DB_TABLE_BroadcastKey, null, newValues);
    }

    public long insertSafetyReminder(SafetyReminder connection) {
        ContentValues newValues = new ContentValues();

        newValues.put(KEY_CONNECT_DATE, BTConnection.DateToString(connection.connect_date));
        newValues.put(KEY_CONNECT_TIME, connection.connect_time);
        newValues.put(KEY_IS_CONFIRM, connection.isConfirm);
        return db.insert(DB_TABLE_SafetyReminder, null, newValues);
    }

    public BTConnection[] queryAllBTConnection() {
        Cursor results =  db.query(DB_TABLE_BTConnection, new String[] { KEY_ID, KEY_DATE, KEY_ISSent,KEY_DURATION,KEY_MAC},
                null, null, null, null, null);
        return ConvertToBTConnection(results);
    }

    public BTConnection[] queryUnsentBTConnection() {
        Cursor results =  db.query(DB_TABLE_BTConnection, new String[] { KEY_ID, KEY_DATE, KEY_ISSent,KEY_DURATION,KEY_MAC},
                KEY_ISSent + "=" +0, null, null, null, null);
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
                , new String[] {BTConnection.DateToString(date1),BTConnection.DateToString(date2)}, null, null, null);
        return ConvertToBTConnection(results);
    }

    public BroadcastKey[] queryAllBroadcastKey(){
        Cursor results =  db.query(DB_TABLE_BroadcastKey, new String[] { KEY_ID, KEY_CONNECT_DATE, KEY_CONNECT_TIME,KEY_CONNECT_MAC,KEY_SELF_MAC},
                        null,null, null, null, KEY_CONNECT_DATE+" DESC ");
        return ConvertToBroadcastKey(results);
    }

    public SafetyReminder[] queryAllSafetyReminder(){
        Cursor results =  db.query(DB_TABLE_SafetyReminder, new String[] { KEY_ID, KEY_CONNECT_DATE, KEY_CONNECT_TIME,KEY_IS_CONFIRM},
                null,null, null, null, null);
        return ConvertToSafetyReminder(results);
    }

    public SafetyReminder[] queryUncofirmSafetyReminder(){
        Cursor results =  db.query(DB_TABLE_SafetyReminder, new String[] { KEY_ID, KEY_CONNECT_DATE, KEY_CONNECT_TIME,KEY_IS_CONFIRM},
                KEY_IS_CONFIRM+"="+0,null, null, null, null);
        return ConvertToSafetyReminder(results);
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

    private BroadcastKey[] ConvertToBroadcastKey(Cursor cursor){
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        BroadcastKey[] connections = new BroadcastKey[resultCounts];
        for (int i = 0 ; i<resultCounts; i++){
            connections[i] = new BroadcastKey();
            connections[i].ID = cursor.getInt(0);
            connections[i].connect_date = BTConnection.strToDate(cursor.getString(cursor.getColumnIndex(KEY_CONNECT_DATE)));
            connections[i].connect_time = cursor.getInt(cursor.getColumnIndex(KEY_CONNECT_TIME));
            connections[i].connect_mac=cursor.getString(cursor.getColumnIndex(KEY_CONNECT_MAC));
            connections[i].self_mac=cursor.getString(cursor.getColumnIndex(KEY_SELF_MAC));
            cursor.moveToNext();
        }
        return connections;
    }

    private SafetyReminder[] ConvertToSafetyReminder(Cursor cursor){
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        SafetyReminder[] connections = new SafetyReminder[resultCounts];
        for (int i = 0 ; i<resultCounts; i++){
            connections[i] = new SafetyReminder();
            connections[i].ID = cursor.getInt(0);
            connections[i].connect_date = BTConnection.strToDate(cursor.getString(cursor.getColumnIndex(KEY_CONNECT_DATE)));
            connections[i].connect_time = cursor.getInt(cursor.getColumnIndex(KEY_CONNECT_TIME));
            connections[i].isConfirm = cursor.getInt(cursor.getColumnIndex(KEY_IS_CONFIRM));
            cursor.moveToNext();
        }
        return connections;
    }

    public long deleteAllBTConnection() {
        return db.delete(DB_TABLE_BTConnection, null, null);
    }

    public long deleteAllBroadcastKsy() {
        return db.delete(DB_TABLE_BroadcastKey, null, null);
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

    public long updateSafetyReminder(long id , SafetyReminder connection){
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_CONNECT_DATE, BTConnection.DateToString(connection.connect_date));
        updateValues.put(KEY_CONNECT_TIME, connection.connect_time);
        updateValues.put(KEY_IS_CONFIRM,connection.isConfirm);

        return db.update(DB_TABLE_SafetyReminder, updateValues,  KEY_ID + "=" + id, null);
    }



    private static class DBOpenHelper extends SQLiteOpenHelper {

        public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        private static final String DB_CREATE_BTConnection = "create table " +
                DB_TABLE_BTConnection + " (" + KEY_ID + " integer primary key autoincrement, " +
                KEY_DATE + " text not null," + KEY_ISSent + " integer not null,"+
                KEY_DURATION+" integer not null,"+KEY_MAC +" text not null" +");";

        private static final String DB_CREATE_BroadcastKey = "create table " +
                DB_TABLE_BroadcastKey + " (" + KEY_ID + " integer primary key autoincrement, " +
                KEY_CONNECT_DATE + " text not null," + KEY_CONNECT_TIME + " integer not null,"+
                KEY_CONNECT_MAC+" text not null,"+KEY_SELF_MAC +" text not null" +");";

        private static final String DB_CREATE_SafetyReminder = "create table " +
                DB_TABLE_SafetyReminder + " (" + KEY_ID + " integer primary key autoincrement, " +
                KEY_CONNECT_DATE + " text not null," + KEY_CONNECT_TIME + " integer not null,"+
                KEY_IS_CONFIRM + " integer not null);";

        @Override
        public void onOpen(SQLiteDatabase _db) {
            super.onOpen(_db);
            if(!_db.isReadOnly()) {
                _db.execSQL("PRAGMA foreign_keys = ON;");
            }
        }

        @Override
        public void onCreate(SQLiteDatabase _db)
        {
            _db.execSQL(DB_CREATE_BTConnection);
            _db.execSQL(DB_CREATE_BroadcastKey);
            _db.execSQL(DB_CREATE_SafetyReminder);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            _db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BTConnection);
            _db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BroadcastKey);
            _db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SafetyReminder);
            onCreate(_db);
        }
    }
}