package com.bonsai.maurice.dodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bonsai.maurice.dodo.R;
import com.bonsai.maurice.dodo.TextValidator;

public class LoginActivity extends AppCompatActivity {

    private static EditText username;
    private static EditText password;
    private static TextView attempts;
    private static Button login_btn;

    private TextValidator editText;

    int attempt_counter = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText)findViewById(R.id.editText_user);
        password = (EditText)findViewById(R.id.editText_password);
        attempts = (TextView)findViewById(R.id.textView_attemt_Count);
        login_btn = (Button)findViewById(R.id.button_login);

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




        LoginButton();
    }

    public  void LoginButton() {


        attempts.setText(Integer.toString(attempt_counter));

        login_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(username.getText().toString().equals("user@user.de") &&
                                password.getText().toString().equals("123456") && validateEmailText() ) {
                            Toast.makeText(LoginActivity.this,"User and Password is correct",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent("dodo.OverviewActivity");
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this,"User and Password is not correct",
                                    Toast.LENGTH_SHORT).show();
                            attempt_counter--;
                            attempts.setText(Integer.toString(attempt_counter));
                            if(attempt_counter == 0){
                                login_btn.setEnabled(false);
                            }
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
}