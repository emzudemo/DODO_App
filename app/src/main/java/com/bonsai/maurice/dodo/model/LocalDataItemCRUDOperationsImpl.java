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
            db.execSQL("CREATE TABLE " + DATAITEMS + " (ID INTEGER PRIMARY KEY,NAME TEXT, DUEDATE INTEGER, DESCRIPTION TEXT, FAVOURITE INTEGER, DONE INTEGER, CONTACTS TEXT)");
        }
    }

    @Override
    public DataItem createDataItem(DataItem item) {

        int done = item.getDone() ? 1 : 0;
        int favourite = item.getFavourite() ? 1 : 0;

        ContentValues values = new ContentValues();
        values.put("NAME",item.getName());
        values.put("DUEDATE",item.getDuedate());
        values.put("DESCRIPTION", item.getDescription());
        values.put("FAVOURITE", favourite);
        values.put("DONE", done);

        List<Contact> contacts = item.getContacts();

        if (contacts == null) {
            contacts = new ArrayList<Contact>();
        }

        StringBuilder contactsBuilder = new StringBuilder();
        for (Contact contact : contacts){
            contactsBuilder.append(contact.getId()).append(",");
        }
        if (contactsBuilder.length()>0){
            contactsBuilder.deleteCharAt(contactsBuilder.length()-1);
        }
        values.put("CONTACTS", contactsBuilder.toString());

        long id = db.insert(DATAITEMS,null,values);
        item.setId(id);

        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        List<DataItem> items = new ArrayList<DataItem>();

        Cursor cursor = db.query(DATAITEMS, new String[]{"ID","NAME","DUEDATE","DESCRIPTION","FAVOURITE","DONE", "CONTACTS"},null,null,null,null,"ID");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            boolean next = false;
            do {
                DataItem item = new DataItem();
                items.add(item);

                long id = cursor.getLong(cursor.getColumnIndex("ID"));
                String name = cursor.getString(cursor.getColumnIndex("NAME"));
                long duedate = cursor.getLong(cursor.getColumnIndex("DUEDATE"));
                String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                int favourite = cursor.getInt(cursor.getColumnIndex("FAVOURITE"));
                int done = cursor.getInt(cursor.getColumnIndex("DONE"));
                String contacts = cursor.getString(cursor.getColumnIndex("CONTACTS"));
                if (contacts == null){
                    contacts = "";
                }
                if (contacts.length() > 0) {
                    String[] contactsArray = contacts.split("\\,");
                    for (String contactId : contactsArray){
                        Contact contactItem = new Contact(Integer.parseInt(contactId),"","","");
                        item.addContact(contactItem);
                    }
                }


                item.setId(id);
                item.setName(name);
                item.setDuedate(duedate);
                item.setDescription(description);
                item.setFavourite(favourite == 1 ? true : false);
                item.setDone(done == 1 ? true : false);

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

        int done = item.getDone() ? 1 : 0;
        int favourite = item.getFavourite() ? 1 : 0;

        ContentValues values = new ContentValues();
        values.put("ID",item.getId());
        values.put("NAME", item.getName());
        values.put("DUEDATE", item.getDuedate());
        values.put("DESCRIPTION", item.getDescription());
        values.put("FAVOURITE", favourite);
        values.put("DONE", done);

        List<Contact> contacts = item.getContacts();
        StringBuilder contactsBuilder = new StringBuilder();
        for (Contact contact : contacts){
            contactsBuilder.append(contact.getId()).append(",");
        }
        if (contactsBuilder.length()>0){
            contactsBuilder.deleteCharAt(contactsBuilder.length()-1);
        }
        values.put("CONTACTS", contactsBuilder.toString());

        db.update(DATAITEMS,values,"ID=?",new String[]{String.valueOf(id)});
        return item;
    }

    @Override
    public boolean deleteDataItem(long id) {


        int numOfRows =  db.delete(DATAITEMS,"ID=?",new String[]{String.valueOf(id)});

        if(numOfRows > 0) {
            return true;
        }

        return false;

    }

    @Override
    public boolean deleteAllDataItems() {
        return false;
    }

    @Override
    public boolean authenticateUser(User user) {
        return false;
    }

}
