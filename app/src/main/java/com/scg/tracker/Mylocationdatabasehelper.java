package com.scg.tracker;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.scg.tracker.models.Locationmodel;

import java.util.ArrayList;
import java.util.List;

public class Mylocationdatabasehelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_database_locations.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_LOCATIONS =
//            "CREATE TABLE locations (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT)";
            "CREATE TABLE IF NOT EXISTS locations (id INTEGER PRIMARY KEY AUTOINCREMENT, lat TEXT, long TEXT,accuracy TEXT, distance TEXT,status TEXT,trip_id INTEGER, type TEXT)";
//            "CREATE TABLE locations (id INTEGER PRIMARY KEY AUTOINCREMENT, lat TEXT, long TEXT, accuracy TEXT,distance TEXT,status TEXT)";

    public Mylocationdatabasehelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS locations");
        onCreate(db);
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM locations");
    }
    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS locations");
    }
    public void addColumnTest(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE locations ADD COLUMN name TEXT");
    }
    public void createTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    public void insertLocation(String lat, String lon, String accuracy, String distance,
                               String status,Integer tripId, String type, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("lat", lat);
        values.put("long", lon);
        values.put("accuracy", accuracy);
        values.put("distance", distance);
        values.put("status", status);
        values.put("trip_id", tripId);
        values.put("type", type);
        values.put("name", name);
        db.insert("locations", null, values);
        db.close();
    }

    public void markLocationAsSynced(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", "synced");
        db.update("locations", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void markAllSynced() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE locations SET status='synced'");
    }

    public void markAllUnsynced() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE locations SET status='unsynced'");
    }

    public List<Locationmodel> getUnsyncedLocations() {
        List<Locationmodel> unsyncedLocations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM locations WHERE status = 'unsynced'", null);

        if (cursor.moveToFirst()) {
            do {
                String lat = cursor.getString(cursor.getColumnIndexOrThrow("lat"));
                String lon = cursor.getString(cursor.getColumnIndexOrThrow("long"));
                String accuracy = cursor.getString(cursor.getColumnIndexOrThrow("accuracy"));
                String distance = cursor.getString(cursor.getColumnIndexOrThrow("distance"));
                Integer tripid = cursor.getInt(cursor.getColumnIndexOrThrow("trip_id"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                unsyncedLocations.add(new Locationmodel(id, tripid,lat, lon,accuracy,distance, status,type,name));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return unsyncedLocations;

    }


    public List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM locations", null);

        if (cursor.moveToFirst()) {
            do {

                String lat = cursor.getString(cursor.getColumnIndexOrThrow("lat"));
                String lon = cursor.getString(cursor.getColumnIndexOrThrow("long"));
                String distance = cursor.getString(cursor.getColumnIndexOrThrow("distance"));
                String accuracy = cursor.getString(cursor.getColumnIndexOrThrow("accuracy"));
                Integer tripId = cursor.getInt(cursor.getColumnIndexOrThrow("trip_id"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                locations.add("la:"+lat+" lo:"+lon+" acc:"+accuracy+" dis:"+distance+" tr_id:"+tripId+" type:"+type+" name:"+name);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return locations;
    }
    public List<String> getUnsyncedLocationsForDisplay() {
        List<String> locations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM locations WHERE status='unsynced'", null);

        if (cursor.moveToFirst()) {
            do {
                String lat = cursor.getString(cursor.getColumnIndexOrThrow("lat"));
                String lon = cursor.getString(cursor.getColumnIndexOrThrow("long"));
                String distance = cursor.getString(cursor.getColumnIndexOrThrow("distance"));
                String accuracy = cursor.getString(cursor.getColumnIndexOrThrow("accuracy"));
                Integer tripId = cursor.getInt(cursor.getColumnIndexOrThrow("trip_id"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                locations.add("la:"+lat+" lo:"+lon+" acc:"+accuracy+" dis:"+distance+" tr_id:"+tripId+" type:"+type+" name:"+name);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return locations;
    }

    public Locationmodel getFirstUnsyncedLocation() {
        SQLiteDatabase db = this.getReadableDatabase();
        Locationmodel location = null;

        Cursor cursor = db.query(
                "locations",                 // Table name
                null,                        // Columns (null = all)
                "status = ?",                // WHERE clause
                new String[]{"unsynced"},          // WHERE args
                null,                        // groupBy
                null,                        // having
                "id ASC",                    // orderBy (sync oldest first)
                "1"                          // limit
        );

        if (cursor != null && cursor.moveToFirst()) {
//
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String lat = cursor.getString(cursor.getColumnIndexOrThrow("lat"));
            String lon = cursor.getString(cursor.getColumnIndexOrThrow("long"));
            String distance = cursor.getString(cursor.getColumnIndexOrThrow("distance"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            String accuracy = cursor.getString(cursor.getColumnIndexOrThrow("accuracy"));
            Integer tripId = cursor.getInt(cursor.getColumnIndexOrThrow("trip_id"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));


            location = new Locationmodel(id,tripId,lat,lon,accuracy,distance,status,type,name);
            // add other fields if needed
            cursor.close();

        }

        return location;
    }

}
