package com.bonsai.maurice.dodo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bonsai.maurice.dodo.model.DataItem;
import com.bonsai.maurice.dodo.model.IDataItemCRUDOperations;
import com.bonsai.maurice.dodo.model.IDataItemCRUDOperationsAsync;
import com.bonsai.maurice.dodo.model.LocalDataItemCRUDOperationsImpl;
import com.bonsai.maurice.dodo.model.RemoteDataItemCRUDOperationsImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OverviewActivity extends AppCompatActivity  {

    public static final String DATA_ITEM = "dataItem";
    public static final int EDIT_ITEM = 2;
    public static final int CREATE_ITEM = 1;
    protected static String logger = OverviewActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    private ViewGroup listView;
    private View addItemAction;
    private ArrayAdapter<DataItem> listViewAdapter;
    private List<DataItem> itemsList = new ArrayList<DataItem>();

    //private List<DataItem> items = Arrays.asList(new DataItem[]{new DataItem("Bla"),new DataItem("Dododoo"),new DataItem("mannomann"),new DataItem("test123")});

    private IDataItemCRUDOperationsAsync crudOperations;

    private class ItemViewHolder {

        public TextView itemNameView;
        public CheckBox itemDoneView;
        public TextView itemDuedateView;
        public CheckBox itemFavView;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. select the view to be controlled
        setContentView(R.layout.activity_overview);

        // 2. read out elements from the view

        listView = (ViewGroup)findViewById(R.id.listView);
        Log.i(logger, "listView: " + listView);
        addItemAction = findViewById(R.id.addItemAction);

        progressDialog = new ProgressDialog(this);

        // 3. set content on the elements
        setTitle(R.string.title_overview);


        // 4. set listeners to allow user interaction

        addItemAction.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addNewItem();
            }
        });

        //instantiate listview with adapter
        listViewAdapter = new ArrayAdapter<DataItem>(this,R.layout.itemview_overview, itemsList) {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @NonNull
            @Override
            public View getView(int position, View itemView, ViewGroup parent) {

                if (itemView != null) {
                    Log.i(logger,"reusing existing itemView for element at position: " + position);
                } else {
                    Log.i(logger,"creating new itemView for element at position: " + position);
                    //create a new instanceof list item view
                    itemView = getLayoutInflater().inflate(R.layout.itemview_overview, null);
                    //read out the the text view for the item name
                    TextView itemNameView = (TextView)itemView.findViewById(R.id.itemName);
                    TextView itemDuedateView = (TextView) itemView.findViewById(R.id.duedate);
                    //read out the checkbox
                    CheckBox itemDoneView = (CheckBox) itemView.findViewById(R.id.itemDone);
                    CheckBox itemFavView = (CheckBox) itemView.findViewById(R.id.favStatus);
                    //create a new instance if the view holder
                    ItemViewHolder itemViewHolder = new ItemViewHolder();
                    //set the itemNameView attribute on view holder to text view
                    itemViewHolder.itemNameView = itemNameView;
                    itemViewHolder.itemDoneView = itemDoneView;
                    itemViewHolder.itemDuedateView = itemDuedateView;
                    itemViewHolder.itemFavView = itemFavView;
                    //set the view holder on the list item view
                    itemView.setTag(itemViewHolder);
                }

                ItemViewHolder viewHolder = (ItemViewHolder)itemView.getTag();
                

                final DataItem item = getItem(position);
               // Log.i(logger, "creating view for position " + position + " and item: " +  item);

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String duedate = sdf.format(item.getDuedate());

                viewHolder.itemNameView.setText(item.getName());
                viewHolder.itemDoneView.setOnCheckedChangeListener(null);
                viewHolder.itemDoneView.setChecked(item.getDone());
                viewHolder.itemFavView.setVisibility(item.getFavourite() ? View.VISIBLE : View.INVISIBLE);
                viewHolder.itemDuedateView.setText(duedate);
                viewHolder.itemDoneView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        item.setDone(isChecked);
                        crudOperations.updateDataItem(item.getId(),item,new IDataItemCRUDOperationsAsync.CallbackFunction<DataItem>(){
                            @Override
                            public void process(DataItem dataItem) {
                                Toast.makeText(OverviewActivity.this,dataItem.getName() + " updated!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                });

                return itemView;

            }

        };
        ((ListView)listView).setAdapter(listViewAdapter);
        listViewAdapter.setNotifyOnChange(true);

        ((ListView) listView).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataItem selectedItem = listViewAdapter.getItem(position);
                showDetailViewForItem(selectedItem);
            }
        });


        progressDialog.show();
        crudOperations = ((DataItemApplication)getApplication()).getCRUDOperationsImpl(); //null;/*new SimpleDataItemCRUDOperationsImpl();*/ /*new LocalDataItemCRUDOperationsImpl(this);*/  /* new RemoteDataItemCRUDOperationsImpl(); */

        crudOperations.syncDataItems(new IDataItemCRUDOperationsAsync.CallbackFunction<Boolean>() {
            @Override
            public void process(Boolean isOnline) {
                readItemsAndFillListView();
                if (!isOnline) {
                    Toast.makeText(OverviewActivity.this, "You are offline!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }





    private void readItemsAndFillListView() {


        progressDialog.show();

        crudOperations.readAllDataItems(new IDataItemCRUDOperationsAsync.CallbackFunction<List<DataItem>>() {
            @Override
            public void process(List<DataItem> result) {
                progressDialog.hide();
                for (DataItem item : result) {
                    addItemToListView(item);
                }

            }
        });

    }

    private void createAndShowItem(/*final*/ DataItem item) {

        crudOperations.createDataItem(item, new IDataItemCRUDOperationsAsync.CallbackFunction<DataItem>() {
            @Override
            public void process(DataItem result) {
                addItemToListView(result);
                progressDialog.hide();
            }
        });


    }

    private void addItemToListView(DataItem item) {

        listViewAdapter.add(item);

    }


    private void showDetailViewForItem(DataItem item) {
        Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
        detailviewIntent.putExtra(DATA_ITEM, item);

        startActivityForResult(detailviewIntent, EDIT_ITEM);
    }
    
    private void addNewItem() {
        Intent addNewItemIntent = new Intent(this, DetailviewActivity.class);

        startActivityForResult(addNewItemIntent, CREATE_ITEM);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CREATE_ITEM && resultCode == Activity.RESULT_OK) {
            DataItem item = (DataItem) data.getSerializableExtra(DATA_ITEM);
            //addItemToListView(item);
            createAndShowItem(item);
        }
        else if (requestCode == EDIT_ITEM) {
            if (resultCode == DetailviewActivity.RESULT_DELETE_ITEM) {
                DataItem item = (DataItem) data.getSerializableExtra(DATA_ITEM);
                deleteAndRemoveItem(item);
            }
            else if (resultCode == DetailviewActivity.RESULT_UPDATE_ITEM){
                DataItem item = (DataItem) data.getSerializableExtra(DATA_ITEM);
                updateItem(item);
            }
        }
    }

    private void updateItem(DataItem item) {
        progressDialog.show();

        crudOperations.updateDataItem(item.getId(), item, new IDataItemCRUDOperationsAsync.CallbackFunction<DataItem>() {
            @Override
            public void process(DataItem result) {
                progressDialog.hide();
                listViewAdapter.clear();
                readItemsAndFillListView();
            }
        });
    }

    private void deleteAndRemoveItem(final DataItem item) {



        crudOperations.deleteDataItem(item.getId(), new IDataItemCRUDOperationsAsync.CallbackFunction<Boolean>() {
            @Override
            public void process(Boolean deleted) {
                if (deleted) {
                    listViewAdapter.remove(findDataItemInList(item.getId()));
                }
            }
        });
    }

    private DataItem findDataItemInList(long id) {
        for (int i=0; i < listViewAdapter.getCount();i++){
            if (listViewAdapter.getItem(i).getId() == id) {
                return listViewAdapter.getItem(i);
            }
        }
        return null;
    }

    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_overview,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void sortItems() {
        Collections.sort(itemsList, new Comparator<DataItem>() {
            @Override
            public int compare(DataItem o1, DataItem o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        this.listViewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sortItems) {
            sortItems();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}