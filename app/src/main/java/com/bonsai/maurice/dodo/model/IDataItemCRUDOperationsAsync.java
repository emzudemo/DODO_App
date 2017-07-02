package com.bonsai.maurice.dodo.model;

import java.util.List;

/**
 * Created by maurice on 24.06.17.
 */

public interface IDataItemCRUDOperationsAsync {

        public static interface CallbackFunction<T> {

            public void process (T result);
        }

        // C
        public void createDataItem(DataItem item,CallbackFunction<DataItem> callback);

        // R
        public void readAllDataItems(CallbackFunction<List<DataItem>> callback);

        // R
        public void readData(long id,CallbackFunction<DataItem> callback);

        // U
        public void updateDataItem(long id, DataItem item, CallbackFunction<DataItem> callback);

        // D
        public void deleteDataItem(long id, CallbackFunction<Boolean> callback);


}
