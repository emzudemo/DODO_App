package com.bonsai.maurice.dodo;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bonsai.maurice.dodo.model.Contact;
import com.bonsai.maurice.dodo.model.DataItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by maurice on 03.06.17.
 */

public class DetailviewActivity extends AppCompatActivity {

    public static final String DATA_ITEM = "dataItem";

    public static final int RESULT_DELETE_ITEM = 10;

    public static  final int REQUEST_PICK_CONTACT = 1;
    public static final int RESULT_UPDATE_ITEM = 20;

    private boolean update = false;

    private TextView itemNameText;
    private TextView itemDescriptionText;
    private EditText itemDuedateText;
    private EditText itemDuetimeText;
    private CheckBox itemFavStat;
    private CheckBox itemDoneStat;

    private Button saveItemButton;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog duetimePickerDialog;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    private int mHour,mMinute;

    private Date date;

    private DataItem item;
    private List<TextView> selectedContacts = new ArrayList<TextView>();
    private LinearLayout layout;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);

        super.onCreate(savedInstanceState);

        // select layout
        setContentView(R.layout.activity_detailview);

        //Datepicker
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY);
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.GERMANY);

        //read out ui elements
        itemNameText = (TextView) findViewById(R.id.itemName);
        itemDescriptionText = (TextView) findViewById(R.id.itemDescription);
        itemFavStat = (CheckBox)findViewById(R.id.itemFav);
        saveItemButton = (Button) findViewById(R.id.saveItem);
        itemDoneStat = (CheckBox) findViewById(R.id.itemDone);
        itemDuedateText = (EditText) findViewById(R.id.itemDuedate);
        itemDuedateText.setInputType(InputType.TYPE_NULL);
        itemDuetimeText = (EditText) findViewById(R.id.itemDuetime);
        itemDuetimeText.setInputType(InputType.TYPE_NULL);
        layout = (LinearLayout) findViewById(R.id.layout);


        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);;
                itemDuedateText.setText(dateFormatter.format(newDate.getTime()));
            }

        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        Calendar time = Calendar.getInstance();
        mHour = time.get(Calendar.HOUR_OF_DAY);
        mMinute = time.get(Calendar.MINUTE);

        duetimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                itemDuetimeText.setText(hourOfDay + ":" + minute);
            }

        }, mHour,mMinute,true);






        //set content on ui elements
        setTitle(R.string.title_detailview);
        item = (DataItem) getIntent().getSerializableExtra(DATA_ITEM);
        if (item != null) {
            itemNameText.setText(item.getName());
            itemDescriptionText.setText(item.getDescription());
            Calendar savedDate = Calendar.getInstance();
            savedDate.setTimeInMillis(item.getDuedate());
            itemDuedateText.setText(dateFormatter.format(savedDate.getTime()));
            itemDuetimeText.setText(timeFormatter.format(savedDate.getTime()));
            itemFavStat.setChecked(item.getFavourite());
            itemDoneStat.setChecked(item.getDone());

            List<Contact> contacts = item.getContacts();
            for (Contact contact : contacts) {
                long contactId = contact.getId();
                if (contactId >= 0) {
                    String uriString = "content://com.android.contacts/data/" + contactId;
                    Uri uri = Uri.parse(uriString);
                    Contact newContact = processSelectedContact(uri);
                    contact.setName(newContact.getName());
                }
            }

            update = true;

        } else {
            item = new DataItem();
            itemNameText.setText("No Name");
            itemDescriptionText.setText("No description");
            Calendar savedDate = Calendar.getInstance();
            itemDuedateText.setText(dateFormatter.format(savedDate.getTime()));
            itemDuetimeText.setText(timeFormatter.format(savedDate.getTime()));
            itemFavStat.setChecked(false);
            itemDoneStat.setChecked(false);
        }

        //prepare for user interaction
        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });


        itemDuedateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        itemDuetimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duetimePickerDialog.show();
            }
        });

        itemNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validateEmailText();
                }
                return false;
            }
        });

    }

    private boolean validateEmailText() {
        String itemName = itemNameText.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(itemName).matches()) {
            itemNameText.setError("This is not an email adress!");
            return false;
        }
        return true;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveItem() {

        String itemName = itemNameText.getText().toString();
        if(itemName.length() == 0){
            itemNameText.setError("Your DODO needs a name!");
        }
        else {
            String itemDescription = itemDescriptionText.getText().toString();

            Boolean itemFav = itemFavStat.isChecked();
            Boolean itemDone = itemDoneStat.isChecked();


            String itemDuedate = itemDuedateText.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String itemDuetime = itemDuetimeText.getText().toString();
            SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
            Date date = null;
            Date time = null;

            try {
                date = sdf.parse(itemDuedate);
                time = stf.parse(itemDuetime);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }


            long millieDate = date.getTime();
            long millieTime = time.getTime();

            millieDate += millieTime;

            Intent returnIntent = new Intent();

            if (update) {
                item.setName(itemName);
                item.setDescription(itemDescription);
                item.setFavourite(itemFav);
                item.setDone(itemDone);
                item.setDuedate(millieDate);
                returnIntent.putExtra(DATA_ITEM, item);
                setResult(RESULT_UPDATE_ITEM, returnIntent);
                finish();
            } else {
                if (item == null) {
                    item = new DataItem();
                }
                item.setName(itemName);
                item.setDescription(itemDescription);
                item.setFavourite(itemFav);
                item.setDone(itemDone);
                item.setDuedate(millieDate);
                returnIntent.putExtra(DATA_ITEM, item);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }
    }

    private void deleteItem () {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(DATA_ITEM,item);

        setResult(RESULT_DELETE_ITEM,returnIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_detailview, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saveItem) {
            saveItem();
            return true;
        }
        else if (item.getItemId() == R.id.deleteItem) {
            AlertDialog dialogBox = AskOption();
            dialogBox.show();
        }

        else if (item.getItemId() == R.id.addContact) {
            addContact();
            return true;
        }

        else {

            return super.onOptionsItemSelected(item);
        }
        return false;
    }

    private void addContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(pickContactIntent,REQUEST_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_CONTACT && resultCode == RESULT_OK) {
            Log.i("Detailview", "Got data from contact picker: " + data);
            Contact contact = processSelectedContact(data.getData());
            item.addContact(contact);
        }
    }

    private Contact processSelectedContact(Uri uri) {
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        cursor.moveToNext();
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        final TextView contactView = new TextView(this);
        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView)v;
                String textViewText = textView.getText().toString();
                List<Contact> contacts = item.getContacts();
                for (int i = selectedContacts.size()-1; i>=0; i--){
                    if (selectedContacts.get(i).getText().toString().equals(textViewText)){
                        layout.removeViewInLayout(selectedContacts.get(i));
                    }
                }
                for (int i = contacts.size() -1; i>=0; i--){
                    if (contacts.get(i).getName().toString().equals(textViewText)){
                        contacts.remove(contacts.get(i));
                    }
                }
            }
        });
        contactView.setText(name);
        layout.addView(contactView);
        selectedContacts.add(contactView);

        long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));

        return new Contact(contactId, name, "", "");
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteItem();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }


}
