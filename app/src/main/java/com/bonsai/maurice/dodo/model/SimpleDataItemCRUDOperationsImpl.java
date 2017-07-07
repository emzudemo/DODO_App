package com.bonsai.maurice.dodo.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by maurice on 05.06.17.
 */

public class SimpleDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    @Override
    public DataItem createDataItem(DataItem item) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(new DataItem[]{new DataItem("Huren"),new DataItem("Söhne"),new DataItem("Mann"),new DataItem("Heims")});
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
