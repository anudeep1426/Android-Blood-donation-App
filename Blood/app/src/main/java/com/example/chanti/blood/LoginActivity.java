package com.example.chanti.blood;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by chanti on 05-Mar-16.
 **/
public class LoginActivity extends Activity {
    String loginTxt, passwordTxt;
    Button loginBt;
    EditText userName,paswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Firebase.setAndroidContext(this);
        userName = (EditText) findViewById(R.id.editUserName);
        paswd = (EditText) findViewById(R.id.editPassword);
    }

    public void onClickLogin(View v) {
        loginBt = (Button) findViewById(R.id.loginBtn);
        if (v.getId() == R.id.loginBtn) {
            loginTxt = ((EditText) findViewById(R.id.editUserName)).getText().toString();
            passwordTxt = ((EditText) findViewById(R.id.editPassword)).getText().toString();
            final Firebase ref = new Firebase("https://bloodmanagement.firebaseio.com/Users");

            if(loginTxt.isEmpty()){
                userName.setError("Field is required");

            }else {
                userName.setError(null);
                if (passwordTxt.isEmpty()) {
                    paswd.setError("Field is required");
                }
                else {
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(loginTxt)) {
                                DataSnapshot q = dataSnapshot.child(loginTxt).child("password");
                                if (passwordTxt.equals(q.getValue().toString())) {
                                    paswd.setError(null);
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    paswd.setError("incorrect password..please try again");
                                }
                            }
                            String data = dataSnapshot.child(loginTxt).toString();
                            SharedPreferences preferences = getSharedPreferences("Login", 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("mobile", loginTxt);
                            editor.putString("profile", data);
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                        }
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }

                    });
                }
            }
        }
    }

    public void onClickRegister(View v) {
        if(v.getId() == R.id.register) {
            Intent r = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(r);
        }
    }
}