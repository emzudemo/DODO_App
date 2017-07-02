package com.bonsai.maurice.dodo.model;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by maurice on 29.06.17.
 */

public class SyncedDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    private IDataItemCRUDOperations localCRUD;
    private IDataItemCRUDOperations remoteCRUD;

    private boolean remoteAvailable;

    public SyncedDataItemCRUDOperationsImpl(Context context) {
        this.localCRUD = new LocalDataItemCRUDOperationsImpl(context);
        this.remoteCRUD =  new RemoteDataItemCRUDOperationsImpl();
        }


    @Override
    public DataItem createDataItem(DataItem item) {
        item = localCRUD.createDataItem(item);
        if (this.remoteAvailable) {
            remoteCRUD.createDataItem(item);
        }
        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        return localCRUD.readAllDataItems();
    }

    @Override
    public DataItem readData(long id) {
        return null;
    }

    @Override
    public DataItem updateDataItem(long id, DataItem item) {
        item = localCRUD.updateDataItem(id, item);
        if (this.remoteAvailable) {
            remoteCRUD.updateDataItem(id, item);
        }
        return item;
    }

    @Override
    public boolean deleteDataItem(long id) {
        boolean deleted = localCRUD.deleteDataItem(id);
        if (deleted && this.remoteAvailable) {
            remoteCRUD.deleteDataItem(id);
        }
        return deleted;

    }

    @Override
    public boolean deleteAllDataItems() {
        return false;
    }


    public boolean syncDataItems() {
        setIsRemoteAvailable();
        // Prüfen ob lokale ToDos vorhanden sind.
        if (this.remoteAvailable) {
            List<DataItem> localItems = this.localCRUD.readAllDataItems();
            if (localItems.isEmpty()) {
                this.initializeLocalDataItems();
            } else {
                this.replaceAllRemoteDataItems(localItems);
            }
        }
        return this.remoteAvailable;
    }

    private void replaceAllRemoteDataItems(List<DataItem> localItems) {
        //alle Einträge auf dem Server löschen
        this.remoteCRUD.deleteAllDataItems();
        //alle lokalen Items in die Webanwendung speichern
        for (DataItem dataItem : localItems) {
            this.remoteCRUD.createDataItem(dataItem);
        }
    }

    private void initializeLocalDataItems() {
        //alle Items von Remote laden und lokal speichern
        List<DataItem> remoteItems = this.remoteCRUD.readAllDataItems();
        for (DataItem dataItem : remoteItems) {
            this.localCRUD.createDataItem(dataItem);
        }

    }

    private void setIsRemoteAvailable() {
        try {
            URL url = new URL("http://10.0.2.2:8080/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(2500);
            try {
                new BufferedInputStream(urlConnection.getInputStream());
                this.remoteAvailable = true;
            } finally {
                urlConnection.disconnect();
            }
        } catch(IOException e)  {
            this.remoteAvailable = false;
        }

    }

}
