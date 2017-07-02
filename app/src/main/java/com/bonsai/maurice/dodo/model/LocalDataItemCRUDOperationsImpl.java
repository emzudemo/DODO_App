package com.bonsai.maurice.dodo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maurice on 05.06.17.
 */

public class LocalDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    public static final String DATAITEMS = "DATAITEMS";
    protected static String logger = LocalDataItemCRUDOperationsImpl.class.getSimpleName();

    private SQLiteDatabase db;

    public LocalDataItemCRUDOperationsImpl(Context context) {

        db = context.openOrCreateDatabase("mydb.sqlite", Context.MODE_PRIVATE, null);
        if (db.getVersion() == 0) {
            db.setVersion(1);
            db.execSQL("CREATE TABLE " + DATAITEMS + " (ID INTEGER PRIMARY KEY,NAME TEXT, DUEDATE INTEGER)");
        }
    }

    @Override
    public DataItem createDataItem(DataItem item) {

        ContentValues values = new ContentValues();
        values.put("NAME",item.getName());
        values.put("DUEDATE",item.getDuedate());

        long id = db.insert(DATAITEMS,null,values);
        item.setId(id);

        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        List<DataItem> items = new ArrayList<DataItem>();

        Cursor cursor = db.query(DATAITEMS, new String[]{"ID","NAME","DUEDATE"},null,null,null,null,"ID");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            boolean next = false;
            do {
                DataItem item = new DataItem();
                items.add(item);

                long id = cursor.getLong(cursor.getColumnIndex("ID"));
                String name = cursor.getString(cursor.getColumnIndex("NAME"));
                long duedate = cursor.getLong(cursor.getColumnIndex("DUEDATE"));

                item.setId(id);
                item.setName(name);
                item.setDuedate(duedate);

                next = cursor.moveToNext();
            } while (next);
        }

        return items;
    }

    @Override
    public DataItem readData(long id) {
        return null;
    }

    @Override
    public DataItem updateDataItem(long id, DataItem item) {
        return null;
    }

    @Override
    public boolean deleteDataItem(long id) {


        int numOfRows =  db.delete(DATAITEMS,"ID=?",new String[]{String.valueOf(id)});

        if(numOfRows > 0) {
            return true;
        }

        return false;

    }

}
