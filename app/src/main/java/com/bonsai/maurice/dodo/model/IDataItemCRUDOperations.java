package com.bonsai.maurice.dodo.model;

import java.util.List;

/**
 * Created by maurice on 05.06.17.
 */

public interface IDataItemCRUDOperations {

    // C
    public DataItem createDataItem(DataItem item);

    // R
    public List<DataItem> readAllDataItems();

    // R
    public DataItem readData(long id);

    // U
    public DataItem updateDataItem(long id, DataItem item);

    // D
    public boolean deleteDataItem(long id);
    public boolean deleteAllDataItems();


}
