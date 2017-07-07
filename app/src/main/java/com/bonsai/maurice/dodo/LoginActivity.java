package com.bonsai.maurice.dodo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.bonsai.maurice.dodo.model.IDataItemCRUDOperationsAsync;
import com.bonsai.maurice.dodo.model.User;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private TextView attempts;
    private Button login_btn;
    private User user;
    private IDataItemCRUDOperationsAsync crudOperations;
    private TextValidator editText;
    int attempt_counter = 5;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isNetworkAvailable())
            setContentView(R.layout.activity_login);
        else
        {
            setContentView(R.layout.activity_login);
            Intent intent = new Intent("dodo.OverviewActivity");
            startActivity(intent);
        }
        username = (EditText)findViewById(R.id.editText_user);
        password = (EditText)findViewById(R.id.editText_password);
        attempts = (TextView)findViewById(R.id.textView_attemt_Count);
        login_btn = (Button)findViewById(R.id.button_login);
        user = new User();
        progressDialog = new ProgressDialog(this);
        username.addTextChangedListener(new TextValidator(username) {
            @Override public void validate(TextView textView, String text) {
                login_btn.setEnabled(false);
                if(username.getText().toString().length() != 0) {
                    validateEmailText();
                    if(password.getText().toString().length() != 0) {
                        login_btn.setEnabled(true);
                    }
                    else{
                        login_btn.setEnabled(false);
                    }
                }
            }
        });
        password.addTextChangedListener(new TextValidator(password) {
            @Override public void validate(TextView textView, String text) {
                login_btn.setEnabled(false);
                if(password.getText().toString().length() != 0) {
                    validatePassword();
                    if(username.getText().toString().length() != 0) {
                        login_btn.setEnabled(true);
                    }
                    else{
                        login_btn.setEnabled(false);
                    }
                }
            }
        });
        crudOperations = ((DataItemApplication)getApplication()).getCRUDOperationsImpl();
        LoginButton();
    }
    public  void LoginButton() {
        attempts.setText(Integer.toString(attempt_counter));
        login_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        if(validateEmailText() ) {
                            user.setEmail(username.getText().toString());
                            user.setPwd(password.getText().toString());
                            crudOperations.authenticateUser(user,  new IDataItemCRUDOperationsAsync.CallbackFunction<Boolean>() {
                                @Override
                                public void process (Boolean result) {
                                    if (result) {
                                        progressDialog.hide();
                                        Toast.makeText(LoginActivity.this,"login successful",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent("dodo.OverviewActivity");
                                        startActivity(intent);
                                    }
                                    else {
                                        progressDialog.hide();
                                        Toast.makeText(LoginActivity.this, "User ans Password are not correct", Toast.LENGTH_SHORT).show();
                                        attempt_counter--;
                                        attempts.setText(Integer.toString(attempt_counter));
                                        if(attempt_counter == 0){
                                            login_btn.setEnabled(false);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
        );
    }
    private boolean validateEmailText(){
        String itemName = username.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(itemName).matches()) {
            username.setError("This is not an email!");
            return false;
        }
        return true;
    }
    private boolean validatePassword(){
        String pass = password.getText().toString();
        String regex = "\\d+";
        if (pass.length() != 6 && pass.matches(regex)) {
            password.setError("Password not right");
            return false;
        }
        return true;
    }
    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}