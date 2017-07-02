package com.bonsai.maurice.dodo;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.bonsai.maurice.dodo.model.DataItem;
import com.bonsai.maurice.dodo.model.IDataItemCRUDOperations;
import com.bonsai.maurice.dodo.model.IDataItemCRUDOperationsAsync;
import com.bonsai.maurice.dodo.model.LocalDataItemCRUDOperationsImpl;
import com.bonsai.maurice.dodo.model.RemoteDataItemCRUDOperationsImpl;
import com.bonsai.maurice.dodo.model.SyncedDataItemCRUDOperationsImpl;

import java.util.List;

/**
 * Created by maurice on 24.06.17.
 */

public class DataItemApplication extends Application implements IDataItemCRUDOperationsAsync {

    private static String logger = DataItemApplication.class.getSimpleName();

    private IDataItemCRUDOperations syncCRUDOperations;

    @Override
    public void onCreate() {
       // super.onCreate();
        Log.i(logger, "onCreate()");
        syncCRUDOperations = new SyncedDataItemCRUDOperationsImpl(this);
    }

    public IDataItemCRUDOperationsAsync getCRUDOperationsImpl() {
        return this;
    }

    @Override
    public void createDataItem(DataItem item, final CallbackFunction<DataItem> callback) {
        new AsyncTask<DataItem, Void,DataItem>() {
            @Override
            protected DataItem doInBackground(DataItem... params) {
                return syncCRUDOperations.createDataItem(params[0]);
            }

            @Override
            protected void onPostExecute(DataItem dataItem) {
                callback.process(dataItem);
            }

        }.execute(item);
    }

    @Override
    public void readAllDataItems(final CallbackFunction<List<DataItem>> callback) {

        new AsyncTask<Void,Void,List<DataItem>>() {

            @Override
            protected List<DataItem> doInBackground(Void... params) {
                return syncCRUDOperations.readAllDataItems();
            }

            @Override
            protected void onPostExecute(List<DataItem> dataItems) {
                callback.process(dataItems);
            }
        }.execute();

    }

    @Override
    public void readData(long id, CallbackFunction<DataItem> callback) {

    }

    @Override
    public void updateDataItem(long id, final DataItem item, final CallbackFunction<DataItem> callback) {

        new AsyncTask<Long,Void,DataItem>(){
            @Override
            protected DataItem doInBackground(Long... params) {
                return syncCRUDOperations.updateDataItem(params[0],item);
            }
            @Override
            protected void onPostExecute(DataItem dataItem) {
                callback.process(dataItem);
            }
        }.execute(id);

    }

    @Override
    public void deleteDataItem(long id, final CallbackFunction<Boolean> callback) {

        new AsyncTask<Long,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Long... params) {
                return syncCRUDOperations.deleteDataItem(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                callback.process(result);
            }
        }.execute(id);

    }
}
