package com.bonsai.maurice.dodo.model;

import java.io.IOException;
import java.util.List;

import okhttp3.internal.http.RetryAndFollowUpInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by maurice on 11.06.17.
 */

public class RemoteDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    public interface IDataItemCRUDWebAPI {

        @POST("/api/todos")
        public Call<DataItem> createDataItem(@Body DataItem item);

        @GET("/api/todos")
        public Call<List<DataItem>> readAllDataItems();

        @GET("api/todos/{id}")
        public Call<DataItem> readData(@Path("id") long id);

        @PUT("/api/todos/{id}")
        public Call<DataItem> updateDataItem(@Path("id") long id, @Body DataItem item);

        @DELETE("/api/todos/{id}")
        public Call<Boolean> deleteDataItem(@Path("id") long id);


    }

    private IDataItemCRUDWebAPI webAPI;

    public RemoteDataItemCRUDOperationsImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.webAPI = retrofit.create(IDataItemCRUDWebAPI.class);
    }


    @Override
    public DataItem createDataItem(DataItem item) {

        try {
            DataItem created = this.webAPI.createDataItem(item).execute().body();
            return created;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DataItem> readAllDataItems() {
        try {
        return this.webAPI.readAllDataItems().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataItem readData(long id) {
        return null;
    }

    @Override
    public DataItem updateDataItem(long id, DataItem item) {
        try {
            return this.webAPI.updateDataItem(id, item).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteDataItem(long id) {
        try {
            return this.webAPI.deleteDataItem(id).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}