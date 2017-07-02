package com.bonsai.maurice.dodo.model;

import android.content.Context;

import java.util.List;

/**
 * Created by maurice on 29.06.17.
 */

public class SyncedDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    private IDataItemCRUDOperations localCRUD;
    private IDataItemCRUDOperations remoteCRUD;

    private boolean remoteAvailable = true;

    public SyncedDataItemCRUDOperationsImpl(Context context) {
        this.localCRUD = new LocalDataItemCRUDOperationsImpl(context);
        this.remoteCRUD =  new RemoteDataItemCRUDOperationsImpl();
    }

    @Override
    public DataItem createDataItem(DataItem item) {
        item = localCRUD.createDataItem(item);
        if (remoteAvailable) {
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

        return remoteCRUD.updateDataItem(id, item);
    }

    @Override
    public boolean deleteDataItem(long id) {
        boolean deleted = localCRUD.deleteDataItem(id);
        if (deleted && remoteAvailable) {
            remoteCRUD.deleteDataItem(id);
        }
        return deleted;

    }
}
